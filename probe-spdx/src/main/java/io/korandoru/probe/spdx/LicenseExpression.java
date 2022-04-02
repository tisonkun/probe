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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public sealed interface LicenseExpression {
    record Single(License license, LicenseException exception, boolean orLater) implements LicenseExpression {}
    record And(LicenseExpression leftLicense, LicenseExpression rightLicense) implements LicenseExpression {}
    record Or(LicenseExpression leftLicense, LicenseExpression rightLicense) implements LicenseExpression {}

    class NormalizationException extends RuntimeException {
        public NormalizationException(String message) {
            super(message);
        }
    }

    final class SingleComparator implements Comparator<Single> {
        @Override
        public int compare(Single o1, Single o2) {
            final var compareLicense = o1.license.licenseId().compareTo(o2.license.licenseId());
            if (compareLicense != 0) {
                return compareLicense;
            }

            final var compareOrLater = Boolean.compare(o1.orLater, o2.orLater);
            if (compareOrLater != 0) {
                return compareOrLater;
            }

            if (o1.exception != null && o2.exception != null) {
                return o1.exception.licenseExceptionId().compareTo(o2.exception.licenseExceptionId());
            } else if (o1.exception != null) {
                return 1;
            } else if (o2.exception != null) {
                return -1;
            } else {
                return  0;
            }
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
                case And ignored -> throw new NormalizationException("Cannot normalize license expressions with AND.");
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
