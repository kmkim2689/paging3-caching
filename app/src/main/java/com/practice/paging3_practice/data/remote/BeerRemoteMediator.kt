package com.practice.paging3_practice.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.practice.paging3_practice.data.local.BeerDatabase
import com.practice.paging3_practice.data.local.BeerEntity
import com.practice.paging3_practice.data.mappers.toBeerEntity
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class BeerRemoteMediator(
    private val beerDb: BeerDatabase,
    private val beerApi: BeerApi
): RemoteMediator<Int, BeerEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BeerEntity>
    ): MediatorResult {
        return try {
            // need to get the corresponding load key
            // the key, here, will be current key => current page
            // as load() function is called multiple times every time we want to load a page...
            // can get this from loadType
            val loadKey: Int = when (loadType) {
                // refresh할 경우, 1페이지부터 다시 load
                LoadType.REFRESH -> 1
                // prepend... 지원하지 않고자 함(앞에 추가)
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                // append... 뒤에 추가 : 페이지 추가로 불러오기
                LoadType.APPEND -> {
                    // calculate the next page
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        // last item이 없는 상태이므로, 처음부터 불러와야
                        1
                    } else {
                        // next page calculation
                        (lastItem.id / state.config.pageSize) + 1
                    }
                }
            }

            // beer data from api
            val beers = beerApi.getBeers(
                page = loadKey,
                pageCount = state.config.pageSize
            )

            // caching
            // withTransaction : execute multiple sql calls one after another
            // and only execute them all only if they all succeed
            // 이것을 쓰지 않으면, 위의 쿼리가 fail이어도 다음것이 성공이면 적용됨.
            // 만약 clearAll이 실패한 채로 추가된다면, refresh 시 중복된 값이 나오는 부적절한 결과가 나올 것
            beerDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // clear all the cache
                    beerDb.dao.clearAll()
                }

                // BeerDto to BeerEntity
                val beerEntities = beers.map { it.toBeerEntity() }
                beerDb.dao.upsertAll(beerEntities)
            }

            MediatorResult.Success(
                // 가져오는 결과가 비어있다면, 끝까지 갔음을 나타냄
                // if true, stop paginating
                endOfPaginationReached = beers.isEmpty()
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}