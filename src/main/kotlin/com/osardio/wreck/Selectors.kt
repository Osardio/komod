package com.osardio.wreck

import com.osardio.wreck.context.ModificationContext
import com.osardio.wreck.proxy.java.Class
import com.osardio.wreck.proxy.java.File
import com.osardio.wreck.proxy.java.Method

class JavaFileSet(private val files: List<File>) {
    fun filter(predicate: (File) -> Boolean): JavaFileSet = JavaFileSet(files.filter(predicate))

    val classes: List<Class> by lazy { files.flatMap { it.classes } }
    val methods: List<Method> by lazy { classes.flatMap { it.methods } }

    internal fun applyAll() {
        files.forEach { it.applyChanges() }
    }
}

fun modification(block: ModificationContext.() -> Unit) {
    val context = ModificationContext()
    context.block()
    context.javaFiles.applyAll()
}

val ModificationContext.classes: List<Class> get() = javaFiles.classes
fun ModificationContext.classes(predicate: (Class.() -> Boolean)): List<Class> = classes.filter { predicate(it) }
val List<Class>.methods: List<Method> get() = flatMap { it.methods }
fun List<Class>.methods(predicate: (Method.() -> Boolean)): List<Method> = methods.filter { predicate(it) }
