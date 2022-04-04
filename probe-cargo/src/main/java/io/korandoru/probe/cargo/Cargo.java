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

package io.korandoru.probe.cargo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.korandoru.probe.spdx.LicenseExpression;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record Cargo(List<Crate> crates) {

    public static Cargo parseCargo(Path root) throws Exception {
        final var objectMapper = new ObjectMapper();
        final var metadataCommand = new ProcessBuilder()
            .directory(root.toFile())
            .command("cargo", "metadata");
        final var metadata = objectMapper.readTree(metadataCommand.start().getInputStream());
        final var packages = metadata.path("packages");
        final var crates = new ArrayList<Crate>();
        for (final var pkg : (Iterable<JsonNode>) (packages::elements)) {
            final var name = pkg.path("name").asText();
            final var licenseNode = pkg.path("license");
            if (licenseNode.isMissingNode()) {
                final var licenseFile = pkg.path("license_file").asText("null");
                log.warn("Cargo package '{}' doesn't have a license field but with license-file: {}.", name, licenseFile);
                crates.add(new Crate(name, "Unknown"));
                continue;
            }
            final List<String> normalizedLicenses;
            try {
                final var licenseExpression = LicenseExpression.parse(licenseNode.asText());
                final var licenses = LicenseExpression.normalize(licenseExpression);
                normalizedLicenses = new ArrayList<>(licenses.stream().map(Object::toString).toList());
            } catch (Exception e) {
                log.warn("Cannot normalize expression {}, fallback to unknown", licenseNode.asText(), e);
                crates.add(new Crate(name, "Unknown"));
                continue;
            }
            final var license = String.join(" OR ", normalizedLicenses);
            crates.add(new Crate(name, license));
        }
        return new Cargo(crates);
    }

}
