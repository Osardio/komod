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

import org.apache.maven.model.Model
import org.apache.maven.model.building.*
import java.io.File
import java.util.*

/**
 * Wraps the default model builder and injects the running JVM system properties into every
 * [ModelBuildingRequest]. Some POMs (e.g. javaparser's) activate profiles on a `<jdk>` range,
 * which the model builder can only evaluate when `java.version` is present in the request's
 * system properties.
 */
class SystemPropertiesModelBuilder : ModelBuilder {

    private val delegate: ModelBuilder = DefaultModelBuilderFactory().newInstance()

    private fun prepare(request: ModelBuildingRequest): ModelBuildingRequest = request.apply {
        if (systemProperties == null || systemProperties.isEmpty) {
            systemProperties = currentSystemProperties()
        }
        if (userProperties == null || userProperties.isEmpty) {
            userProperties = Properties()
        }
    }

    override fun build(request: ModelBuildingRequest): ModelBuildingResult =
        delegate.build(prepare(request))

    override fun build(
        request: ModelBuildingRequest,
        result: ModelBuildingResult
    ): ModelBuildingResult = delegate.build(prepare(request), result)

    override fun buildRawModel(file: File, validationLevel: Int, locationTracking: Boolean): Result<Model> =
        delegate.buildRawModel(file, validationLevel, locationTracking) as Result<Model>

    companion object {

        private fun currentSystemProperties(): Properties {
            val props = Properties()
            System.getProperties().toList().forEach { (key, value) ->
                if (key !is String || value == null) return@forEach
                props.setProperty(key, value.toString())
            }
            return props
        }
    }
}
