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

grammar LicenseExpression;

simpleExpression: LICENSE_OR_EXCEPTION_ID OR_LATER_MARK? ('WITH' LICENSE_OR_EXCEPTION_ID)?;
compoundExpression:
    simpleExpression                                # singleLicense
    | compoundExpression ('OR'|'/') compoundExpression    # orExpression
    | '(' compoundExpression ')'                    # parenExpression
    ;

OR_LATER_MARK: '+';
LICENSE_OR_EXCEPTION_ID: [-.A-Za-z0-9]+;
WHITESPACE: [ \t\r\n]+ -> skip;
