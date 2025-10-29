package com.scoreretriever.presentation.component.impl.enhanced2d

class CircularNumber<T>(
    value: T,
    val length: T,
    val add: (T, T) -> T,
    val sub: (T, T) -> T,
    val mod: (T, T) -> T,
    val one: T
) {
    var value: T = mod(value, length)
        set(v) { field = mod(v, length) }

    operator fun plus(increment: T): CircularNumber<T> {
        value = mod(add(value, increment), length)
        return this
    }

    operator fun minus(decrement: T): CircularNumber<T> {
        value = mod(add(sub(value, decrement), length), length)
        return this
    }

    operator fun inc(): CircularNumber<T> = plus(one)
    operator fun dec(): CircularNumber<T> = minus(one)
}

fun CircularInt(value: Int, length: Int) = CircularNumber(
    value, length,
    add = { a, b -> a + b },
    sub = { a, b -> a - b },
    mod = { a, b -> ((a % b) + b) % b },
    one = 1
)

fun CircularFloat(value: Float, length: Float) = CircularNumber(
    value, length,
    add = { a, b -> a + b },
    sub = { a, b -> a - b },
    mod = { a, b ->
        val r = a % b
        if (r < 0f) r + b else r
    },
    one = 1f
)

fun CircularDouble(value: Double, length: Double) = CircularNumber(
    value, length,
    add = { a, b -> a + b },
    sub = { a, b -> a - b },
    mod = { a, b ->
        val r = a % b
        if (r < 0.0) r + b else r
    },
    one = 1.0
)
