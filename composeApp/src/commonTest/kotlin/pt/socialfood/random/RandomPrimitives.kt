package pt.socialfood.random

import kotlin.random.Random

private const val DEFAULT_STRING_LENGTH = 8
private val ALPHANUMERIC = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun randomString(length: Int = DEFAULT_STRING_LENGTH): String =
    (1..length).map { ALPHANUMERIC.random() }.joinToString("")

fun randomInt(from: Int = 0, until: Int = 1_000): Int = Random.nextInt(from, until)

fun randomLong(from: Long = 0L, until: Long = 1_000_000L): Long = Random.nextLong(from, until)

fun randomDouble(from: Double = 0.0, until: Double = 5.0): Double = Random.nextDouble(from, until)

fun randomBoolean(): Boolean = Random.nextBoolean()

fun randomId(prefix: String = "id"): String = "$prefix-${randomString()}"

fun randomEmail(): String = "${randomString(6)}@${randomString(5)}.com"

fun randomUrl(): String = "https://${randomString(6)}.com/${randomString(6)}"

inline fun <reified T : Enum<T>> randomEnum(): T = enumValues<T>().random()

fun <T> randomList(size: Int = randomInt(1, 4), generator: (index: Int) -> T): List<T> = List(size) { generator(it) }

/** Returns null with the given [chance] (0.0..1.0), otherwise a value from [generator]. */
fun <T> randomNullable(chance: Double = 0.5, generator: () -> T): T? =
    if (Random.nextDouble() < chance) generator() else null
