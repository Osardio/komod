/* Copyright (C) 2026 Osardio
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
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

internal class CommandLine {
    val parser = ArgParser("komod")
    val artifact by parser.option(
        ArgType.String, "artifact", "a",
        "Maven coordinates of the recipe artifact: groupId:artifactId:version"
    ).required()
    val modification by parser.option(
        ArgType.String, "modification", "m",
        "FQN of the Modification implementation class"
    ).required()
    val target by parser.option(
        ArgType.String, "target", "t",
        "Directory of the target project (default: the current directory)"
    ).default(File(".").absoluteFile.path)
    val verbose by parser.option(
        ArgType.Boolean, "verbose", "v",
        "Print changed/skipped files with absolute paths"
    ).default(false)
    val noExcludes by parser.option(
        ArgType.Boolean, "no-excludes", "w",
        "Disable the default directory exclusions of the target scan (e.g., build, .gradle)"
    ).default(false)

    fun parse(args: Array<String>) = parser.parse(args)
}
