package com.osardio.komod

interface Modification {
    fun modification(): ProjectFile.() -> Unit
}
