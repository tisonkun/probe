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

import java.util.List;
import java.util.Optional;

public final class RegisteredLicenses {

    private static final RegisteredLicenses REGISTERED_LICENSES = new RegisteredLicenses();

    public static List<License> get() {
        return REGISTERED_LICENSES.licenses;
    }

    public static Optional<License> find(String id) {
        return REGISTERED_LICENSES.licenses.stream()
            .filter(e -> e.id().equals(id))
            .findFirst();
    }

    private final List<License> licenses;

    private RegisteredLicenses() {
        this.licenses = InitializeUtils.loadLicenses();
    }

}
