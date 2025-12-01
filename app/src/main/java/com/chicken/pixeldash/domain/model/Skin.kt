package com.chicken.pixeldash.domain.model

import androidx.annotation.DrawableRes

data class Skin(
    val id: String,
    val name: String,
    val price: Int,
    @DrawableRes val drawable: Int
)
