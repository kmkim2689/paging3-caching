package com.practice.paging3_practice.data.mappers

import com.practice.paging3_practice.data.local.BeerEntity
import com.practice.paging3_practice.data.remote.BeerDto
import com.practice.paging3_practice.domain.Beer

fun BeerDto.toBeerEntity(): BeerEntity {
    return BeerEntity(
        id = id,
        name = name,
        tagline = tagline,
        description = description,
        firstBrewed = first_brewed,
        imageUrl = image_url
    )
}

fun BeerEntity.toBeer(): Beer {
    return Beer(
        id = id,
        name = name,
        tagline = tagline,
        firstBrewed = firstBrewed,
        description = description,
        imageUrl = imageUrl
    )
}