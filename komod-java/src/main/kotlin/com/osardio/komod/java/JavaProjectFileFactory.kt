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

package com.osardio.komod.java

import com.osardio.komod.ProjectFile
import com.osardio.komod.ProjectFileFactory
import com.osardio.komod.java.proxy.JavaFile
import java.io.File

/** Creates [JavaFile] wrappers for `.java` sources. Registered via the ServiceLoader. */
class JavaProjectFileFactory : ProjectFileFactory {
    override fun create(file: File): ProjectFile? =
        if (file.isFile && file.extension == "java") JavaFile(file) else null
}
