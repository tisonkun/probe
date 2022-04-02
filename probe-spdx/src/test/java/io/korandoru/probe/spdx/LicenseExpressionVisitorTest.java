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

import io.korandoru.probe.spdx.antlr.generated.LicenseExpressionLexer;
import io.korandoru.probe.spdx.antlr.generated.LicenseExpressionParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LicenseExpressionVisitorTest {

    @Test
    void parseApacheLicense() {
        final var licenseExpression = parseLicenseExpression("Apache-2.0");
        Assertions.assertThat(licenseExpression).isInstanceOf(LicenseExpression.Single.class);

        final var singleLicense = (LicenseExpression.Single) licenseExpression;
        Assertions.assertThat(singleLicense.license().id()).isEqualTo("Apache-2.0");
        Assertions.assertThat(singleLicense.exception()).isNull();
        Assertions.assertThat(singleLicense.orLater()).isFalse();
    }

    @Test
    void parseOrLicenses() {
        final var licenseExpression = parseLicenseExpression("MIT OR Apache-2.0");
        Assertions.assertThat(licenseExpression).isInstanceOf(LicenseExpression.Or.class);

        final var or = (LicenseExpression.Or) licenseExpression;
        Assertions.assertThat(((LicenseExpression.Single) or.leftLicense()).license().id()).isEqualTo("MIT");
        Assertions.assertThat(((LicenseExpression.Single) or.rightLicense()).license().id()).isEqualTo("Apache-2.0");
    }

    @Test
    void normalize() {
        final var firstOrder = parseLicenseExpression("MIT OR Apache-2.0");
        final var secondOrder = parseLicenseExpression("(Apache-2.0 OR (MIT))");
        Assertions.assertThat(LicenseExpression.normalize(firstOrder)).isEqualTo(LicenseExpression.normalize(secondOrder));

        var withExceptionCases = new String[] {
            "GPL-2.0-only OR GPL-2.0-only WITH Classpath-exception-2.0",
            "GPL-2.0-only WITH Classpath-exception-2.0 OR GPL-2.0-only"};
        for (final var withExceptionCase: withExceptionCases) {
            final var first = LicenseExpression.normalize(parseLicenseExpression(withExceptionCase)).get(0);
            Assertions.assertThat(first.exception()).isNull();
        }
    }

    private LicenseExpression parseLicenseExpression(String expression) {
        final var lexer = new LicenseExpressionLexer(CharStreams.fromString(expression));
        final var parser = new LicenseExpressionParser(new CommonTokenStream(lexer));
        return new DefaultLicenseExpressionVisitor().visit(parser.compoundExpression());
    }

}
