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

import kr.jclab.jsxmlinterpreter.*;
import kr.jclab.jsxmlinterpreter.exceptions.ExecuteRuntimeException;
import kr.jclab.jsxmlinterpreter.exceptions.JsXMLInterpreterException;
import kr.jclab.jsxmlinterpreter.instruction.AbstractInstruction;
import kr.jclab.jsxmlinterpreter.instruction.Instruction;

public class CallInstruction extends AbstractInstruction {
    private JsXMLInterpreter.CodeType codeType;
    private String codeKey;

    public CallInstruction(JsXMLInterpreter.CodeType codeType, String codeKey) {
        this.codeType = codeType;
        this.codeKey = codeKey;
    }

    @Override
    public Instruction doInstruction(JsXMLInterpreter interpreter, BlockContext blockContext) throws ExecuteRuntimeException {
        BlockContext subBlockContext = new DefaultBlockContext(blockContext);
        try {
            Object retval = interpreter.call(codeType, codeKey, subBlockContext);
        } catch (JsXMLInterpreterException e) {
            throw new ExecuteRuntimeException(e);
        }
        return this.nextInstruction;
    }
}
