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

import com.github.javaparser.ast.body.FieldDeclaration
import com.osardio.komod.ChangeContext
import com.osardio.komod.NodeProxy

class Field(
    ctx: ChangeContext,
    override val ast: FieldDeclaration
) : NodeProxy<FieldDeclaration>(ctx, ast), Annotatable {

    var name: String
        get() = ast.variables.first().nameAsString
        set(value) {
            update {
                variables.first().setName(value)
            }
        }

    var type: Type
        get() = Type(ctx, ast.variables.first().type)
        set(value) {
            update {
                variables.first().setType(value.ast)
            }
        }

    var modifiers: Set<Modifier.Keyword>
        get() = ast.modifiers.map { Modifier(ctx, it).keyword }.toSet()
        set(value) {
            update {
                modifiers.clear()
                modifiers.addAll(value.sortedBy { it.ordinal }.map { Modifier.toAst(it) })
            }
        }

    override var annotations: List<Annotation>
        get() = ast.annotations.map { Annotation(ctx, it) }
        set(value) {
            update {
                annotations.clear()
                annotations.addAll(value.map { it.ast })
            }
        }

    fun remove() {
        update { remove() }
    }
}
