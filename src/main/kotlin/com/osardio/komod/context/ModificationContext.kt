package com.osardio.komod.context

import com.osardio.komod.JavaFileSet
import java.io.File
import com.osardio.komod.proxy.java.File as JavaFile

class ModificationContext(val javaFiles: JavaFileSet) {
    constructor() : this(
        JavaFileSet(
            File(".").walk()
                .filter { it.isFile && it.extension == "java" }
                .map { JavaFile(it) }
                .toList()
        )
    )
}
