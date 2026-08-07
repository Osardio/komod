package com.osardio.wreck.proxy.java

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.osardio.wreck.context.ChangeContext
import com.osardio.wreck.proxy.NodeProxy

class Class(
    ctx: ChangeContext,
    private val cu: CompilationUnit,
    override val ast: ClassOrInterfaceDeclaration
) : NodeProxy<ClassOrInterfaceDeclaration>(ctx, ast) {

    var name: String
        get() = ast.nameAsString
        set(value) { update { setName(value) } }

    val fqn: String by lazy {
        val pkg = cu.packageDeclaration?.orElse(null)?.nameAsString ?: ""
        if (pkg.isEmpty()) name else "$pkg.$name"
    }

    var modifiers: Set<Modifier.Keyword>
        get() = ast.modifiers.map { it.keyword }.toSet()
        set(value) { update {
            modifiers.clear()
            modifiers.addAll(value.map { Modifier(it) })
        } }

    val methods: List<Method> by lazy {
        ast.methods.map { Method(ctx, it) }
    }
}
