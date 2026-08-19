package com.osardio.wreck.proxy.java

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.ImportDeclaration
import com.osardio.wreck.context.ChangeContext
import com.osardio.wreck.proxy.NodeProxy

class Import(
    ctx: ChangeContext,
    override val ast: ImportDeclaration
) : NodeProxy<ImportDeclaration>(ctx, ast) {
    constructor(fqn: String) : this(ChangeContext(), StaticJavaParser.parseImport("import $fqn;"))
}