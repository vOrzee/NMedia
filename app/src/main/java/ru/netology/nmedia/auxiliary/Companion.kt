package ru.netology.nmedia.auxiliary

import android.os.Bundle
import ru.netology.nmedia.auxiliary.ConstantValues.POST_KEY
import ru.netology.nmedia.dto.Post


class Companion {

    companion object {
        var Bundle.textArg: String?
            set(value) = putString(POST_KEY, value)
            get() = getString(POST_KEY)
        var Bundle.longArg: Long
            set(value) = putLong(POST_KEY, value)
            get() = getLong(POST_KEY)
    }
}