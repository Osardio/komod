package com.osardio.wreck.proxy.java

import com.github.javaparser.ast.Modifier as JModifier
import com.osardio.wreck.context.ChangeContext
import com.osardio.wreck.proxy.NodeProxy

class Modifier(
    ctx: ChangeContext,
    override val ast: JModifier
) : NodeProxy<JModifier>(ctx, ast) {

    enum class Keyword {
        PUBLIC, PROTECTED, PRIVATE, ABSTRACT, DEFAULT, STATIC, FINAL,
        TRANSIENT, VOLATILE, SYNCHRONIZED, NATIVE, STRICTFP,
        TRANSITIVE, SEALED, NON_SEALED
    }

    val keyword: Keyword
        get() = Keyword.valueOf(ast.keyword.name)

    companion object {
        internal fun toAst(keyword: Keyword): JModifier =
            JModifier(JModifier.Keyword.valueOf(keyword.name))
    }
}
