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

public record License(
    String licenseId,
    String name,
    long referenceNumber,
    boolean isDeprecatedLicenseId,
    boolean isOsiApproved,
    boolean isFsfLibre
) {
    public static List<License> getRegistered() {
        return Registered.SINGLETON.licenses;
    }

    private enum Registered {
        SINGLETON;
        final List<License> licenses;
        Registered() {
            this.licenses = InitializeUtils.loadLicenses();
        }
    }
}