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

import io.korandoru.probe.spdx.antlr.generated.LicenseExpressionBaseVisitor;
import io.korandoru.probe.spdx.antlr.generated.LicenseExpressionParser;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.ParseTree;

@Slf4j
public class DefaultLicenseExpressionVisitor extends LicenseExpressionBaseVisitor<LicenseExpression> {
    @Override
    public LicenseExpression visitParenExpression(LicenseExpressionParser.ParenExpressionContext ctx) {
        return visit(ctx.compoundExpression());
    }

    @Override
    public LicenseExpression visitSimpleExpression(LicenseExpressionParser.SimpleExpressionContext ctx) {
        final var licenseId = ctx.LICENSE_OR_EXCEPTION_ID(0).getText();
        final var license = RegisteredLicenses.find(licenseId).orElseThrow();
        final var orLater = ctx.OR_LATER_MARK() != null;
        final var exceptionId = Optional.ofNullable(ctx.LICENSE_OR_EXCEPTION_ID(1)).map(ParseTree::getText);
        final var exception = exceptionId.map(id -> RegisteredLicenseExceptions.find(id).orElseThrow());
        return new LicenseExpression.Single(license, exception.orElse(null), orLater);
    }

    @Override
    public LicenseExpression visitSingleLicense(LicenseExpressionParser.SingleLicenseContext ctx) {
        return visit(ctx.simpleExpression());
    }

    @Override
    public LicenseExpression visitOrExpression(LicenseExpressionParser.OrExpressionContext ctx) {
        final var leftLicense = visit(ctx.compoundExpression(0));
        final var rightLicense = visit(ctx.compoundExpression(1));
        return new LicenseExpression.Or(leftLicense, rightLicense);
    }
}
