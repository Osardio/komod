package com.osardio.komod.proxy.java

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.stmt.Statement
import com.osardio.komod.context.ChangeContext
import com.osardio.komod.proxy.NodeProxy

class Statement(
    ctx: ChangeContext,
    override val ast: Statement
) : NodeProxy<Statement>(ctx, ast) {
    constructor(value: String) : this(ChangeContext(), StaticJavaParser.parseStatement(value))
}
