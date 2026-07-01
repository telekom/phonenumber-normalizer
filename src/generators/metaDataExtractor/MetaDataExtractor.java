/*
 * Copyright © 2023 Deutsche Telekom AG (opensource@telekom.de)
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

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonemetadata;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Extracts static dialing metadata from Google's LibPhoneNumber and writes the runtime lookup class.
 */
public final class MetaDataExtractor {

    private static final String DEFAULT_OUTPUT_FILE =
            "src/main/java/de/telekom/phonenumbernormalizer/numberplans/GeneratedRegionDialingMetadata.java";

    private MetaDataExtractor() {
    }

    public static void main(String[] args) throws Exception {
        Path outputFile = Paths.get(args.length > 0 ? args[0] : DEFAULT_OUTPUT_FILE);
        String libPhoneNumberVersion = args.length > 1 ? args[1] : resolveLibPhoneNumberVersion();
        OffsetDateTime generatedAt = OffsetDateTime.now(ZoneOffset.UTC).withNano(0);
        Files.createDirectories(outputFile.getParent());
        Files.writeString(outputFile, generateSource(libPhoneNumberVersion, generatedAt), StandardCharsets.UTF_8);
    }

    private static String resolveLibPhoneNumberVersion() {
        Package phoneNumberPackage = PhoneNumberUtil.class.getPackage();
        String version = phoneNumberPackage == null ? null : phoneNumberPackage.getImplementationVersion();
        return version == null || version.isBlank() ? "unknown" : version;
    }

    private static String generateSource(String libPhoneNumberVersion, OffsetDateTime generatedAt) throws Exception {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Method getMetadataForRegion = phoneUtil.getClass().getDeclaredMethod("getMetadataForRegion", String.class);
        getMetadataForRegion.setAccessible(true); // NOSONAR - build-time extraction of private LibPhoneNumber metadata

        StringBuilder source = new StringBuilder();
        appendHeader(source, generatedAt.getYear());
        appendClassStart(source, libPhoneNumberVersion, generatedAt);

        Set<String> regions = new TreeSet<>(phoneUtil.getSupportedRegions());
        for (String region : regions) {
            Phonemetadata.PhoneMetadata metadata =
                    (Phonemetadata.PhoneMetadata) getMetadataForRegion.invoke(phoneUtil, region);
            appendMetadataEntry(source, region, metadata);
        }

        appendClassEnd(source);
        return source.toString();
    }

    private static void appendHeader(StringBuilder source, int copyrightYear) {
        source.append("/*\n")
                .append(" * Copyright © ")
                .append(copyrightYear)
                .append("""
                 Deutsche Telekom AG (opensource@telekom.de)
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
                package de.telekom.phonenumbernormalizer.numberplans;

                import java.time.OffsetDateTime;
                import java.util.Collections;
                import java.util.HashMap;
                import java.util.Locale;
                import java.util.Map;

                """);
    }

    private static void appendClassStart(
            StringBuilder source,
            String libPhoneNumberVersion,
            OffsetDateTime generatedAt
    ) {
        source.append("""
                /**
                 * Region dialing metadata used by {@link PhoneLibWrapper}.
                 * <p>
                 * This class is generated from the configured LibPhoneNumber dependency during Maven's generate-sources
                 * phase by src/generators/metaDataExtractor/MetaDataExtractor.java.
                 * </p>
                 */
                public final class GeneratedRegionDialingMetadata {

                """);
        source.append("    public static final String LIBPHONENUMBER_VERSION = ")
                .append(toJavaString(libPhoneNumberVersion))
                .append(";\n");
        source.append("    public static final OffsetDateTime GENERATED_AT = OffsetDateTime.parse(")
                .append(toJavaString(generatedAt.toString()))
                .append(");\n\n");
        source.append("""
                    private static final Map<String, RegionDialingMetadata> METADATA_BY_REGION = createMetadata();

                    private GeneratedRegionDialingMetadata() {
                    }

                    public static RegionDialingMetadata forRegion(String regionCode) {
                        if (regionCode == null) {
                            return null;
                        }
                        return METADATA_BY_REGION.get(regionCode.toUpperCase(Locale.ROOT));
                    }

                    private static Map<String, RegionDialingMetadata> createMetadata() {
                        Map<String, RegionDialingMetadata> metadata = new HashMap<>();
                """);
    }

    private static void appendMetadataEntry(StringBuilder source, String region, Phonemetadata.PhoneMetadata metadata) {
        source.append("        metadata.put(\"")
                .append(escapeJava(region.toUpperCase(Locale.ROOT)))
                .append("\", new RegionDialingMetadata(")
                .append(toJavaString(metadata.getInternationalPrefix()))
                .append(", ")
                .append(toJavaString(metadata.getNationalPrefix()))
                .append(", ")
                .append(metadata.hasNationalPrefix())
                .append("));\n");
    }

    private static void appendClassEnd(StringBuilder source) {
        source.append("""
                        return Collections.unmodifiableMap(metadata);
                    }
                }
                """);
    }

    private static String toJavaString(String value) {
        return value == null ? "null" : "\"" + escapeJava(value) + "\"";
    }

    private static String escapeJava(String value) {
        StringBuilder result = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            switch (current) {
                case '\\':
                    result.append("\\\\");
                    break;
                case '"':
                    result.append("\\\"");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                default:
                    result.append(current);
                    break;
            }
        }
        return result.toString();
    }
}
