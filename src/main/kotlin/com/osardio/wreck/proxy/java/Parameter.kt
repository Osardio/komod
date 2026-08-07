package com.osardio.wreck.proxy.java

import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.AnnotationExpr
import com.osardio.wreck.context.ChangeContext
import com.osardio.wreck.proxy.NodeProxy

class Parameter(
    ctx: ChangeContext,
    override val ast: Parameter
) : NodeProxy<Parameter>(ctx, ast) {

    var name: String
        get() = ast.nameAsString
        set(value) { update { setName(value) } }

    var modifiers: Set<Modifier.Keyword>
        get() = ast.modifiers.map { it.keyword }.toSet()
        set(value) { update {
            modifiers.clear()
            modifiers.addAll(value.map { Modifier(it) })
        } }

    var annotations: List<AnnotationExpr>
        get() = ast.annotations
        set(value) { update {
            annotations.clear()
            annotations.addAll(value)
        } }

    val type: Type get() = Type(ctx, ast.type)
}
