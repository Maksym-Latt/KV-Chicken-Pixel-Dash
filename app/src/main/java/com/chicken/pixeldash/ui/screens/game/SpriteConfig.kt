package com.chicken.pixeldash.ui.screens.game

// -------------------------
//  PIXEL SIZE CONFIG
// -------------------------
internal object SpriteConfig {
    // visual scale (как объект выглядит на экране)
    const val CHICKEN_SCALE = 0.45f
    const val ROCK_SCALE = 0.32f
    const val BOX_SCALE = 0.32f
    const val EGG_SCALE = 0.23f

    // hitbox scale (насколько большой коллайдер)
    const val CHICKEN_HITBOX = 0.70f
    const val ROCK_HITBOX = 0.70f
    const val BOX_HITBOX = 0.80f
    const val EGG_HITBOX = 1.00f
}


// -------------------------
//  BASE PNG SIZES
// -------------------------
const val PLAYER_BASE_WIDTH = 142f
const val PLAYER_BASE_HEIGHT = 154f

const val ROCK_BASE_WIDTH = 179f
const val ROCK_BASE_HEIGHT = 108f

const val BOX_BASE_WIDTH = 152f
const val BOX_BASE_HEIGHT = 254f

const val EGG_BASE_WIDTH = 146f
const val EGG_BASE_HEIGHT = 179f


// -------------------------
//  ENTITY EXTENSIONS
// -------------------------

data class Rect(val left: Float, val top: Float, val right: Float, val bottom: Float)

// Доступ к размерам
internal fun Entity.spriteWidth(): Float = spriteSize().first
internal fun Entity.spriteHeight(): Float = spriteSize().second

// Выбор визуального масштаба по типу
internal fun Entity.autoSizeScale(): Float =
    when (type) {
        EntityType.Rock -> SpriteConfig.ROCK_SCALE
        EntityType.Box  -> SpriteConfig.BOX_SCALE
        EntityType.Egg  -> SpriteConfig.EGG_SCALE
    }

// Выбор хитбокса по типу
internal fun Entity.autoHitboxScale(): Float =
    when (type) {
        EntityType.Rock -> SpriteConfig.ROCK_HITBOX
        EntityType.Box  -> SpriteConfig.BOX_HITBOX
        EntityType.Egg  -> SpriteConfig.EGG_HITBOX
    }

// Финальный визуальный размер
internal fun Entity.spriteSize(): Pair<Float, Float> {
    val s = autoSizeScale()
    return when (type) {
        EntityType.Rock -> ROCK_BASE_WIDTH * s to ROCK_BASE_HEIGHT * s
        EntityType.Box  -> BOX_BASE_WIDTH * s to BOX_BASE_HEIGHT * s
        EntityType.Egg  -> EGG_BASE_WIDTH * s to EGG_BASE_HEIGHT * s
    }
}

// Прямоугольник хитбокса сущности
internal fun Entity.hitboxRect(): Rect {
    val (spriteWidth, spriteHeight) = spriteSize()
    val h = autoHitboxScale()

    val hitboxWidth = spriteWidth * h
    val hitboxHeight = spriteHeight * h

    val left = x + (spriteWidth - hitboxWidth) / 2f
    val top = y + (spriteHeight - hitboxHeight) / 2f

    return Rect(left, top, left + hitboxWidth, top + hitboxHeight)
}


// -------------------------
//  PLAYER SIZE + HITBOX
// -------------------------

internal fun playerSpriteSize(): Pair<Float, Float> =
    PLAYER_BASE_WIDTH * SpriteConfig.CHICKEN_SCALE to
            PLAYER_BASE_HEIGHT * SpriteConfig.CHICKEN_SCALE

internal fun playerHitboxRect(y: Float): Rect {
    val (spriteWidth, spriteHeight) = playerSpriteSize()
    val h = SpriteConfig.CHICKEN_HITBOX

    val hitboxWidth = spriteWidth * h
    val hitboxHeight = spriteHeight * h

    val left = PLAYER_X + (spriteWidth - hitboxWidth) / 2f
    val top = y + (spriteHeight - hitboxHeight) / 2f

    return Rect(left, top, left + hitboxWidth, top + hitboxHeight)
}


// -------------------------
//  COLLISION HELPER
// -------------------------

internal fun intersects(a: Rect, b: Rect): Boolean {
    val horizontal = a.left < b.right && a.right > b.left
    val vertical = a.top < b.bottom && a.bottom > b.top
    return horizontal && vertical
}
