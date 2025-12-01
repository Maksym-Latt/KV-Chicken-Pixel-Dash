package com.chicken.pixeldash.domain.model

import com.chicken.pixeldash.R

object SkinCatalog {
    val allSkins: List<Skin> = listOf(
        Skin(id = "classic", name = "Classic Chick", price = 0, drawable = R.drawable.chicken_1),
        Skin(id = "red", name = "Red Pixel", price = 320, drawable = R.drawable.chicken_2),
        Skin(id = "green", name = "Green GameBoy", price = 750, drawable = R.drawable.chicken_3),
        Skin(id = "cyber", name = "Cyber Grid", price = 1200, drawable = R.drawable.chicken_4),
    )

    fun findById(id: String): Skin = allSkins.firstOrNull { it.id == id } ?: allSkins.first()
}
