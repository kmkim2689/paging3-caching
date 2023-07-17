package com.practice.paging3_practice.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface BeerDao {

    @Upsert
    // upsert = update + insert
    // 중복되는 값이 있다면 update, 중복되는 값이 없다면 insert
    suspend fun upsertAll(beers: List<BeerEntity>)

    // for getting each page source
    // for paging3 library => PagingSource 형태를 반환. room과 상호작용할 수 있음
    // key : int(page), value : Beer Entity
    @Query("SELECT * FROM BeerEntity")
    fun pagingSource(): PagingSource<Int, BeerEntity>

    // 검색 기능을 구현 가능... 여기서는 생략

    // for clearing the cache
    @Query("DELETE FROM BeerEntity")
    suspend fun clearAll()
}