package com.osardio.wreck.proxy.java

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.AnnotationExpr
import com.github.javaparser.ast.stmt.BlockStmt
import com.osardio.wreck.context.ChangeContext
import com.osardio.wreck.proxy.NodeProxy

class Method(
    ctx: ChangeContext,
    override val ast: MethodDeclaration
) : NodeProxy<MethodDeclaration>(ctx, ast) {

    var name: String
        get() = ast.nameAsString
        set(value) { update { setName(value) } }

    var parameters: List<Parameter>
        get() = ast.parameters.map { Parameter(ctx, it) }
        set(value) { update {
            parameters.clear()
            parameters.addAll(value.map { it.ast })
        } }

    var type: Type
        get() = Type(ctx, ast.type)
        set(value) { update { setType(value.ast) } }

    var statements: List<Statement>
        get() = ast.body.orElse(null)?.statements?.map { Statement(ctx, it) } ?: emptyList()
        set(value) { update {
            val body = ast.body.orElseGet { BlockStmt() }
            body.statements.clear()
            body.statements.addAll(value.map { it.ast })
            setBody(body)
        } }

    var modifiers: Set<Modifier.Keyword>
        get() = ast.modifiers.map { Modifier(ctx, it).keyword }.toSet()
        set(value) { update {
            modifiers.clear()
            modifiers.addAll(value.map { Modifier.toAst(it) })
        } }

    var annotations: List<AnnotationExpr>
        get() = ast.annotations
        set(value) { update {
            annotations.clear()
            annotations.addAll(value)
        } }
}
