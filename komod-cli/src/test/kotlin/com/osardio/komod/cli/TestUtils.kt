/*
 * Copyright (C) 2026 Osardio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.osardio.komod.cli

import java.io.File

/** Builds a small project tree on disk for scanning. */
fun projectTree(): File {
    val root = File(System.getProperty("java.io.tmpdir"), "komod-walk-" + java.util.UUID.randomUUID())
    root.mkdirs()
    File(root, "A.java").writeText("public class A {}")
    File(root, "B.txt").writeText("not java")
    val build = File(root, "build"); build.mkdirs()
    File(build, "C.java").writeText("public class C {}")
    val git = File(root, ".git"); git.mkdirs()
    File(git, "D.java").writeText("public class D {}")
    val out = File(root, "out"); out.mkdirs()
    File(out, "E.java").writeText("public class E {}")
    val sub = File(root, "src/main"); sub.mkdirs()
    File(sub, "F.java").writeText("public class F {}")
    val target = File(root, "target"); target.mkdirs()
    File(target, "G.java").writeText("public class G {}")
    return root
}
