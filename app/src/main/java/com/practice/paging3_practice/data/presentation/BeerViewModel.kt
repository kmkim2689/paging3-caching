package com.practice.paging3_practice.data.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import com.practice.paging3_practice.data.local.BeerEntity
import com.practice.paging3_practice.data.mappers.toBeer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class BeerViewModel @Inject constructor(
    private val pager: Pager<Int, BeerEntity>
) : ViewModel() {

    // that flow will trigger a new emission of paging data every time we scroll and we want to trigger loading hte next page
    // public final val beerPagingFlow: Flow<PagingData<Beer>>
    val beerPagingFlow = pager
        .flow
        .map { pagingData ->
            pagingData.map { it.toBeer() }
        }
        .cachedIn(viewModelScope)
}