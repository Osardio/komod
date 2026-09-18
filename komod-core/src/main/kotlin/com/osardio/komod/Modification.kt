package com.osardio.komod

import com.osardio.komod.java.proxy.ProjectFile

interface Modification {
    fun modification(): ProjectFile.() -> Unit
}
