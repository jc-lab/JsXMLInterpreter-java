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

import kr.jclab.jsxmlinterpreter.BlockContext;
import kr.jclab.jsxmlinterpreter.ExecuteRuntimeException;
import kr.jclab.jsxmlinterpreter.JsXMLInterpreter;
import kr.jclab.jsxmlinterpreter.UnacceptableCommandException;
import kr.jclab.jsxmlinterpreter.instruction.AbstractInstruction;
import kr.jclab.jsxmlinterpreter.instruction.Instruction;

public class BreakInstruction extends AbstractInstruction {

    public BreakInstruction() {
    }

    @Override
    public Instruction doInstruction(JsXMLInterpreter interpreter, BlockContext blockContext) throws ExecuteRuntimeException {
        if(!BlockContext.Flag.check(blockContext.getFlags(), BlockContext.Flag.Breakable)) {
            throw new UnacceptableCommandException("break is not accept in current block");
        }
        blockContext.setBreak();
        return null;
    }
}
