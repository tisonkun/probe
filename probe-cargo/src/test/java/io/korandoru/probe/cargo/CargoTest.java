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

import java.nio.file.Path;
import java.util.Objects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CargoTest {

    @Test
    void parseDepLicenses() throws Exception {
        final var rootDir = Objects.requireNonNull(CargoTest.class.getResource("/stub"));
        final var cargo = Cargo.parseCargo(Path.of(rootDir.toURI()));
        Assertions.assertThat(cargo.crates()).containsExactlyInAnyOrder(
            new Crate("anyhow", "Apache-2.0 OR MIT"),
            new Crate("stub", "Apache-2.0"));
    }

}
