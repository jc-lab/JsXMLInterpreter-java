/*
 * Copyright 2018 JC-Lab. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.jclab.jsxmlinterpreter.internal.instructions;

import kr.jclab.jsxmlinterpreter.JsXMLInterpreterParser;
import kr.jclab.jsxmlinterpreter.ParseException;
import kr.jclab.jsxmlinterpreter.instruction.Instruction;
import kr.jclab.jsxmlinterpreter.parser.InstructionParser;
import org.apache.commons.jexl2.Expression;
import org.w3c.dom.Element;

public class ForeachInstructionParser implements InstructionParser {
    @Override
    public String getTagName() {
        return "foreach";
    }

    @Override
    public ParseResult parseInstruction(JsXMLInterpreterParser parser, Element codeElement) throws ParseException {
        String varRefText = JsXMLInterpreterParser.getElementAttribute(codeElement, "ref");
        String varItemVarName = JsXMLInterpreterParser.getElementAttribute(codeElement, "item");
        String varKeyVarName = JsXMLInterpreterParser.getElementAttribute(codeElement, "key");
        Expression varRefExpr = parser.createExpression(varRefText);
        Instruction statmentInstruction = parser.parseBlockElement(codeElement);

        return new ParseResult(new ForeachInstruction.Builder()
                .ref(varRefText, varRefExpr)
                .itemVarName(varItemVarName)
                .keyVarName(varKeyVarName)
                .statmentInstruction(statmentInstruction)
                .build());
    }
}
