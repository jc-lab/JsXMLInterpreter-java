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
package kr.jclab.jsxmlinterpreter.parser;

import kr.jclab.jsxmlinterpreter.JsXMLInterpreterParser;
import kr.jclab.jsxmlinterpreter.ParseException;
import kr.jclab.jsxmlinterpreter.instruction.AbstractInstruction;
import org.w3c.dom.Element;

public interface InstructionParser {
    class ParseResult {
        public AbstractInstruction instruction = null;
        public Element nextElement = null;
        public boolean rewriteNextElement = false;

        public ParseResult() {
        }

        public ParseResult(AbstractInstruction instruction) {
            this.instruction = instruction;
            this.nextElement = null;
            this.rewriteNextElement = false;
        }

        public ParseResult(AbstractInstruction instruction, Element nextElement, boolean rewriteNextElement) {
            this.instruction = instruction;
            this.nextElement = nextElement;
            this.rewriteNextElement = rewriteNextElement;
        }
    }

    String getTagName();
    ParseResult parseInstruction(JsXMLInterpreterParser parser, Element codeElement) throws ParseException;
}
