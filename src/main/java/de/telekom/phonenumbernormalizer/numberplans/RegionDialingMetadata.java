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
package de.telekom.phonenumbernormalizer.numberplans;

/**
 * Dialing metadata extracted from Google's LibPhoneNumber at build time.
 *
 * @param internationalPrefix international dialing prefix used in the region
 * @param nationalPrefix national access code used in the region
 * @param hasNationalPrefix if the region metadata explicitly defines a national prefix
 */
public record RegionDialingMetadata(String internationalPrefix, String nationalPrefix, boolean hasNationalPrefix) {
}
