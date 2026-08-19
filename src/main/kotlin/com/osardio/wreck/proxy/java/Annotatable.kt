package com.osardio.wreck.proxy.java

interface Annotatable {
    val file: File?
    var annotations: List<Annotation>
}