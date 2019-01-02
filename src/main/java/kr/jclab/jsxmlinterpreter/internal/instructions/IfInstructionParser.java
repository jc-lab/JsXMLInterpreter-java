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

import kr.jclab.jsxmlinterpreter.InvalidInstructionException;
import kr.jclab.jsxmlinterpreter.JsXMLInterpreterParser;
import kr.jclab.jsxmlinterpreter.ParseException;
import kr.jclab.jsxmlinterpreter.parser.InstructionParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class IfInstructionParser implements InstructionParser {
    @Override
    public String getTagName() {
        return "if";
    }

    @Override
    public ParseResult parseInstruction(JsXMLInterpreterParser parser, Element codeElement) throws ParseException {
        ParseResult parseResult = new ParseResult();
        IfInstruction.Builder builder;
        String condition = JsXMLInterpreterParser.getElementAttribute(codeElement, "condition");
        if("if".equalsIgnoreCase(codeElement.getTagName()) || "elseif".equalsIgnoreCase(codeElement.getTagName())) {
            if (condition == null || condition.length() <= 0) {
                throw new InvalidInstructionException("no have a condition");
            }
        }
        builder = new IfInstruction.Builder(parser.createExpression(condition));
        if("if".equalsIgnoreCase(codeElement.getTagName())) {
            boolean breakFlag = false;
            for (Node nextNode = codeElement.getNextSibling(); nextNode != null; nextNode = nextNode.getNextSibling()) {
                if (nextNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element nextElement = (Element) nextNode;
                    String tagName = nextElement.getTagName();
                    if(breakFlag) {
                        parseResult.nextElement = nextElement;
                        break;
                    }
                    if ("elseif".equalsIgnoreCase(tagName)) {
                        ParseResult subParseResult = this.parseInstruction(parser, nextElement);
                        builder.elseIfInstruction((IfInstruction)subParseResult.instruction);
                        parseResult.rewriteNextElement = true;
                    } else if ("else".equalsIgnoreCase(tagName)) {
                        builder.falseInstruction(parser.parseBlockElement(nextElement));
                        parseResult.rewriteNextElement = true;
                        breakFlag = true;
                    } else {
                        parseResult.nextElement = nextElement;
                        break;
                    }
                }
            }
        }
        builder.trueInstruction(parser.parseBlockElement(codeElement));

        parseResult.instruction = builder.build();
        return parseResult;
    }
}
