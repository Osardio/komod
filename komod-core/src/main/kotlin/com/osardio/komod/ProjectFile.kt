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

package com.osardio.komod

import java.io.File

interface ProjectFile {
    val file: File
    val name: String

    /**
     * Applies the accumulated changes; if the content changed, persists the result
     * (e.g. rewrites the file in-place). Files untouched by the modification are left as-is.
     *
     * @return `true` if the file was changed and persisted, `false` if nothing was applied.
     */
    fun commit(): Boolean
}
