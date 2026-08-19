package com.osardio.wreck.proxy.java

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.stmt.Statement
import com.osardio.wreck.context.ChangeContext
import com.osardio.wreck.proxy.NodeProxy

class Statement(
    ctx: ChangeContext,
    override val ast: Statement
) : NodeProxy<Statement>(ctx, ast) {
    constructor(value: String) : this(ChangeContext(), StaticJavaParser.parseStatement(value))
}
