package pt.socialfood.random

import kotlin.random.Random

private const val DEFAULT_STRING_LENGTH = 8
private val ALPHANUMERIC = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun Random.nextString(length: Int = DEFAULT_STRING_LENGTH): String =
    (1..length).map { ALPHANUMERIC.random(this) }.joinToString("")

fun Random.nextEmail(): String = "${nextString(6)}@${nextString(5)}.com"

fun Random.nextUrl(): String = "https://${nextString(6)}.com/${nextString(6)}"

inline fun <reified T : Enum<T>> Random.nextEnum(): T = enumValues<T>()[nextInt(enumValues<T>().size)]

fun <T> Random.nextList(size: Int = nextInt(1, 4), generator: Random.(index: Int) -> T): List<T> =
    List(size) { generator(it) }

/** Returns null with the given [chance] (0.0..1.0), otherwise a value from [generator]. */
fun <T> Random.nextNullable(chance: Double = 0.5, generator: Random.() -> T): T? =
    if (nextDouble() < chance) generator() else null
