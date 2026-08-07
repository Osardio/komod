package com.osardio.wreck.context

import com.osardio.wreck.JavaFileSet
import java.io.File
import com.osardio.wreck.proxy.java.File as JavaFile

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
