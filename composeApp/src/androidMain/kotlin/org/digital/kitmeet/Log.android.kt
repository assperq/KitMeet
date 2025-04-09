package org.digital.kitmeet

import android.util.Log

actual fun log(str: String) {
    Log.d("LOG", str)
}