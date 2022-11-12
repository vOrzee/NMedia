package ru.netology.nmedia.auxiliary

object NumberTranslator {
    fun translateNumber(count: Int) = when (count) {
        in 1..999 -> "$count"
        in 1_000..9_999 -> "${count / 1000}.${count % 1000 / 100}K"
        in 10_000..999_000 -> "${count / 1000}K"
        in 1_000_000..9_999_999 -> "${count / 1_000_000}.${count % 1_000_000 / 100_000}M"
        in 10_000_000..Int.MAX_VALUE -> "${count / 1_000_000}M"
        else -> "0"
    }
}