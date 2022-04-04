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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public sealed interface LicenseExpression {
    record Single(License license, LicenseException exception, boolean orLater) implements LicenseExpression {
        @Override
        public String toString() {
            return license.id() +
                (orLater ? "+" : "") +
                (exception != null ? " WITH " + exception : "");
        }
    }

    record Or(LicenseExpression leftLicense, LicenseExpression rightLicense) implements LicenseExpression {
        @Override
        public String toString() {
            return leftLicense.toString() + " OR " + rightLicense.toString();
        }
    }

    static LicenseExpression parse(String expression) {
        final var lexer = new LicenseExpressionLexer(CharStreams.fromString(expression));
        final var parser = new LicenseExpressionParser(new CommonTokenStream(lexer));
        return new DefaultLicenseExpressionVisitor().visit(parser.compoundExpression());
    }

    final class SingleComparator implements Comparator<Single> {
        @Override
        public int compare(Single o1, Single o2) {
            final var compareLicense = o1.license.id().compareTo(o2.license.id());
            if (compareLicense != 0) {
                return compareLicense;
            }

            final var compareOrLater = Boolean.compare(o1.orLater, o2.orLater);
            if (compareOrLater != 0) {
                return compareOrLater;
            }

            final var comparator = Comparator.nullsFirst(Comparator.comparing(LicenseException::id));
            return comparator.compare(o1.exception, o2.exception);
        }
    }

    static List<Single> normalize(LicenseExpression expression) {
        final var result = new ArrayList<Single>();
        final var expressionQueue = new ArrayDeque<LicenseExpression>();
        expressionQueue.add(expression);

        while (!expressionQueue.isEmpty()) {
            final var exp = expressionQueue.removeFirst();
            switch (exp) {
                case Single single -> result.add(single);
                case Or or -> {
                    expressionQueue.addLast(or.leftLicense);
                    expressionQueue.addLast(or.rightLicense);
                }
            }
        }

        result.sort(new SingleComparator());
        return result;
    }
}
