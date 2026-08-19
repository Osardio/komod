package com.osardio.komod.proxy.java

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.BlockStmt
import com.osardio.komod.context.ChangeContext
import com.osardio.komod.proxy.NodeProxy

class Method(
    ctx: ChangeContext,
    override val ast: MethodDeclaration
) : NodeProxy<MethodDeclaration>(ctx, ast), Annotatable {

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
            modifiers.addAll(value.sortedBy { it.ordinal }.map { Modifier.toAst(it) })
        } }

    override var annotations: List<Annotation>
        get() = ast.annotations.map { Annotation(ctx, it) }
        set(value) { update {
            annotations.clear()
            annotations.addAll(value.map { it.ast })
        } }
}
