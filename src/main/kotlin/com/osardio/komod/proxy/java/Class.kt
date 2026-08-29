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

package com.osardio.komod.proxy.java

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.osardio.komod.context.ChangeContext
import com.osardio.komod.proxy.NodeProxy

class Class(
    ctx: ChangeContext,
    private val cu: CompilationUnit,
    override val ast: ClassOrInterfaceDeclaration
) : NodeProxy<ClassOrInterfaceDeclaration>(ctx, ast), Annotatable {

    var name: String
        get() = ast.nameAsString
        set(value) {
            update { setName(value) }
        }

    val fqn: String by lazy {
        val pkg = cu.packageDeclaration?.orElse(null)?.nameAsString ?: ""
        if (pkg.isEmpty()) name else "$pkg.$name"
    }

    var modifiers: Set<Modifier.Keyword>
        get() = ast.modifiers.map { Modifier(ctx, it).keyword }.toSet()
        set(value) {
            update {
                modifiers.clear()
                modifiers.addAll(value.map { Modifier.toAst(it) })
            }
        }

    val methods: List<Method> by lazy {
        ast.methods.map { Method(ctx, it) }
    }

    override var annotations: List<Annotation>
        get() = ast.annotations.map { Annotation(ctx, it) }
        set(value) {
            update {
                annotations.clear()
                annotations.addAll(value.map { it.ast })
            }
        }
}
