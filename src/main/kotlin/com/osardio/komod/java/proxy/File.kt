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

package com.osardio.komod.java.proxy

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter
import com.osardio.komod.ChangeContext
import java.io.File

class File {
    private val ctx = ChangeContext()
    private val cu: CompilationUnit
    val name: String

    constructor(file: File) {
        name = file.name
        cu = StaticJavaParser.parse(file)
    }

    constructor(name: String, content: String) {
        this.name = name
        cu = StaticJavaParser.parse(content)
        LexicalPreservingPrinter.setup(cu)
    }

    val classes: List<Class> by lazy {
        cu.findAll(ClassOrInterfaceDeclaration::class.java).map { Class(ctx, cu, it) }
    }

    var imports: List<Import>
        get() = cu.imports.map { Import(ctx, it) }
        set(value) {
            ctx.add {
                cu.imports.clear()
                cu.imports.addAll(value.map { it.ast })
            }
        }

    fun ensureImport(fqn: String) {
        ctx.add {
            if (cu.imports.none { it.nameAsString == fqn }) {
                cu.imports.add(StaticJavaParser.parseImport("import $fqn;"))
            }
        }
    }

    fun getContent(): String = LexicalPreservingPrinter.print(cu)

    internal fun applyChanges() {
        ctx.applyAll()
    }
}
