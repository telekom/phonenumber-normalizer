/*
 * Copyright © 2026 Deutsche Telekom AG (opensource@telekom.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.telekom.phonenumbernormalizer.numberplans

import spock.lang.Specification

import javax.xml.parsers.DocumentBuilderFactory
import java.nio.file.Path
import java.nio.file.Paths

class GeneratedRegionDialingMetadataTest extends Specification {

    private static final Path PROJECT_DIR = Paths.get("").toAbsolutePath()

    def "generated metadata records the configured LibPhoneNumber version"() {
        given:
        String generatedVersion = GeneratedRegionDialingMetadata.LIBPHONENUMBER_VERSION
        String configuredVersion = pomLibPhoneNumberVersion()

        expect:
        assert generatedVersion == configuredVersion:
        "GeneratedRegionDialingMetadata was generated with LibPhoneNumber ${generatedVersion}, " +
                "but pom.xml configures ${configuredVersion}. Run 'mvn -DskipTests generate-sources' " +
                "and commit the generated GeneratedRegionDialingMetadata.java"
        GeneratedRegionDialingMetadata.LIBPHONENUMBER_VERSION == pomLibPhoneNumberVersion()
    }

    private static String pomLibPhoneNumberVersion() {
        def documentBuilderFactory = DocumentBuilderFactory.newInstance()
        documentBuilderFactory.setNamespaceAware(false)
        def document = documentBuilderFactory.newDocumentBuilder().parse(PROJECT_DIR.resolve("pom.xml").toFile())
        document.getElementsByTagName("libphonenumber.version").item(0).textContent
    }
}
