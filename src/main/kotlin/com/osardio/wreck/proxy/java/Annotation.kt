package com.osardio.wreck.proxy.java

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.expr.AnnotationExpr
import com.osardio.wreck.context.ChangeContext
import com.osardio.wreck.proxy.NodeProxy

class Annotation(
    ctx: ChangeContext,
    override val ast: AnnotationExpr
) : NodeProxy<AnnotationExpr>(ctx, ast) {
    constructor(value: String) : this(ChangeContext(), StaticJavaParser.parseAnnotation("@$value"))
}
