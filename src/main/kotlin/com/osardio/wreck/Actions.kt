package com.osardio.wreck

import com.osardio.wreck.proxy.java.Annotatable
import com.osardio.wreck.proxy.java.Annotation

fun Annotatable.addAnnotation(fqn: String) {
    val file = requireNotNull(file) { "addAnnotation: цель не привязана к файлу" }
    file.ensureImport(fqn)
    annotations += Annotation(fqn.substringAfterLast('.'))
}