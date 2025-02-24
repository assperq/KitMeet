package org.digital.kitmeet

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform