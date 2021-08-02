package io.github.vinccool96.observationskt.utils

import kotlin.random.Random

object RandomUtils {

    private val random: Random
        get() = Random(System.nanoTime())

    fun randomString(length: Int): String {
        val chars = (('a'..'z') + ('A'..'Z') + ('0'..'9')).sorted()
        return (0 until length).map { chars[this.random.nextInt(chars.size)] }.joinToString("")
    }

    fun nextInt(): Int {
        return this.random.nextInt()
    }

}