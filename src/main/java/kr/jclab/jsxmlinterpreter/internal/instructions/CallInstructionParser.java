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

import kr.jclab.jsxmlinterpreter.JsXMLInterpreter;
import kr.jclab.jsxmlinterpreter.JsXMLInterpreterParser;
import kr.jclab.jsxmlinterpreter.exceptions.ParseException;
import kr.jclab.jsxmlinterpreter.parser.InstructionParser;
import org.w3c.dom.Element;

public class CallInstructionParser implements InstructionParser {
    @Override
    public String getTagName() {
        return "call";
    }

    @Override
    public ParseResult parseInstruction(JsXMLInterpreterParser parser, Element codeElement) throws ParseException {
        String macroName = JsXMLInterpreterParser.getElementAttribute(codeElement, "macro");
        String usercodeName = JsXMLInterpreterParser.getElementAttribute(codeElement, "usercode");
        String key = macroName != null ? macroName : usercodeName;
        return new ParseResult(new CallInstruction(macroName != null ? JsXMLInterpreter.CodeType.Macro : JsXMLInterpreter.CodeType.User, key));
    }
}
