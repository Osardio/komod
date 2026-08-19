package com.osardio.wreck

import com.osardio.wreck.context.ModificationContext
import com.osardio.wreck.proxy.java.Class
import com.osardio.wreck.proxy.java.File
import com.osardio.wreck.proxy.java.Method
import com.osardio.wreck.proxy.java.Parameter

class JavaFileSet(internal val files: List<File>) {
    internal fun applyAll() {
        files.forEach { it.applyChanges() }
    }
}

fun modification(block: File.() -> Unit) {
    val files = ModificationContext().javaFiles
    files.files.forEach { it.block() }
    files.applyAll()
}

fun File.classes(filter: Class.() -> Boolean, action: Class.() -> Unit) {
    classes.filter { filter(it) }.forEach { it.action() }
}

fun File.classes(action: Class.() -> Unit) {
    classes.forEach { it.action() }
}

fun Class.methods(filter: Method.() -> Boolean, action: Method.() -> Unit) {
    this.methods.filter { filter(it) }.forEach { it.action() }
}

fun Class.methods(action: Method.() -> Unit) {
    this.methods.forEach { it.action() }
}

fun Method.parameters(filter: Parameter.() -> Boolean, action: Parameter.() -> Unit) {
    this.parameters.filter { filter(it) }.forEach { it.action() }
}

fun Method.parameters(action: Parameter.() -> Unit) {
    this.parameters.forEach { it.action() }
}
