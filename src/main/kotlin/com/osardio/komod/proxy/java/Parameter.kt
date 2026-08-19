package com.osardio.komod.proxy.java

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.body.Parameter
import com.osardio.komod.context.ChangeContext
import com.osardio.komod.proxy.NodeProxy

class Parameter(
    ctx: ChangeContext,
    override val ast: Parameter
) : NodeProxy<Parameter>(ctx, ast), Annotatable {
    constructor(value: String) : this(ChangeContext(), StaticJavaParser.parseParameter(value))

    var name: String
        get() = ast.nameAsString
        set(value) { update { setName(value) } }

    var modifiers: Set<Modifier.Keyword>
        get() = ast.modifiers.map { Modifier(ctx, it).keyword }.toSet()
        set(value) { update {
            modifiers.clear()
            modifiers.addAll(value.map { Modifier.toAst(it) })
        } }

    override var annotations: List<Annotation>
        get() = ast.annotations.map { Annotation(ctx, it) }
        set(value) { update {
            annotations.clear()
            annotations.addAll(value.map { it.ast })
        } }

    val type: Type get() = Type(ctx, ast.type)
}
