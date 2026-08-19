package com.osardio.wreck

import com.osardio.wreck.proxy.java.Annotatable
import com.osardio.wreck.proxy.java.Annotation
import com.osardio.wreck.proxy.java.File

context(file: File)
fun Annotatable.addAnnotation(fqn: String) {
    file.ensureImport(fqn)
    annotations += Annotation(fqn.substringAfterLast('.'))
}