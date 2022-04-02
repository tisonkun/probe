/*
 * Copyright 2022 Koranduru Contributors
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

package io.korandoru.probe.spdx;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum InitializeUtils {
    ;

    public static List<License> loadLicenses() {
        return loadLicensesOrExceptions("licenses", "/licenses.json", License.class);
    }

    public static List<LicenseException> loadLicenseExceptions() {
        return loadLicensesOrExceptions("exceptions", "/exceptions.json", LicenseException.class);
    }

    private static <T> List<T> loadLicensesOrExceptions(String type, String path, Class<T> elementClass) {
        try {
            final var exceptionsFile = InitializeUtils.class.getResource(path);
            final var mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            return mapper.convertValue(
                mapper.readTree(exceptionsFile).path(type),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, elementClass));
        } catch (Throwable t) {
            log.warn("Cannot load {} from {}.", type, path, t);
            return Collections.emptyList();
        }
    }
}
