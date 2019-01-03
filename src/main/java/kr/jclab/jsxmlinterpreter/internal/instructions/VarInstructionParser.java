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

import kr.jclab.jsxmlinterpreter.exceptions.InvalidInstructionException;
import kr.jclab.jsxmlinterpreter.JsXMLInterpreterParser;
import kr.jclab.jsxmlinterpreter.exceptions.ParseException;
import kr.jclab.jsxmlinterpreter.parser.InstructionParser;
import org.w3c.dom.Element;

public class VarInstructionParser implements InstructionParser {
    @Override
    public String getTagName() {
        return "var";
    }

    @Override
    public ParseResult parseInstruction(JsXMLInterpreterParser parser, Element codeElement) throws ParseException {
        String varName = JsXMLInterpreterParser.getElementAttribute(codeElement, "name");
        String varType = JsXMLInterpreterParser.getElementAttribute(codeElement, "type");
        String varRef = JsXMLInterpreterParser.getElementAttribute(codeElement, "ref");
        String varValue = JsXMLInterpreterParser.getElementAttribute(codeElement, "value");
        String varOperation = JsXMLInterpreterParser.getElementAttribute(codeElement, "operation");
        String varKey = JsXMLInterpreterParser.getElementAttribute(codeElement, "key");
        String varKeyRef = JsXMLInterpreterParser.getElementAttribute(codeElement, "key-ref");
        String varTo = JsXMLInterpreterParser.getElementAttribute(codeElement, "to");

        VarInstruction.Builder builder = new VarInstruction.Builder();

        if(varType != null)
            builder.varType(varType);

        if(varKey != null)
            builder.varKeyName(varKey);
        else if(varKeyRef != null)
            builder.varKeyRef(varKeyRef, parser.createExpression(varKeyRef));

        if(varRef != null)
            builder.varRef(varRef, parser.createExpression(varRef));
        else if(varValue != null)
            builder.varValue(varValue);

        if(varOperation != null)
            builder.varOperation(varOperation);
        if(varTo != null)
            builder.varTo(varTo);

        return new ParseResult(builder
                .varName(varName)
                .build());
    }
}
