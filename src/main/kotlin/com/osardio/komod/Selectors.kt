/*
 * Copyright (C) 2026 Osardio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.osardio.komod

import com.osardio.komod.context.ModificationContext
import com.osardio.komod.proxy.java.Class
import com.osardio.komod.proxy.java.Field
import com.osardio.komod.proxy.java.File
import com.osardio.komod.proxy.java.Method
import com.osardio.komod.proxy.java.Parameter

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

fun Class.fields(filter: Field.() -> Boolean, action: Field.() -> Unit) {
    this.fields.filter { filter(it) }.forEach { it.action() }
}

fun Class.fields(action: Field.() -> Unit) {
    this.fields.forEach { it.action() }
}
