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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum InitializeUtils {
    ;

    public static List<License> loadLicenses() {
        return loadLicensesOrExceptions("licenses");
    }

    public static List<Exception> loadExceptions() {
        return loadLicensesOrExceptions("exceptions");
    }

    private static <T> List<T> loadLicensesOrExceptions(String type) {
        final var path = "/" + type + ".json";
        try {
            final var exceptionsFile = License.class.getResource(path);
            final var mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final var nodes = mapper.readTree(exceptionsFile).get(type).toPrettyString();
            return mapper.readValue(nodes, new TypeReference<>() {
            });
        } catch (Throwable t) {
            log.warn("Cannot load {} from {}.", type, path, t);
            return Collections.emptyList();
        }
    }
}
