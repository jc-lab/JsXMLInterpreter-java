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
package kr.jclab.jsxmlinterpreter.instruction;

import kr.jclab.jsxmlinterpreter.BlockContext;
import kr.jclab.jsxmlinterpreter.ExecuteRuntimeException;
import kr.jclab.jsxmlinterpreter.DefaultBlockContext;
import kr.jclab.jsxmlinterpreter.JsXMLInterpreter;
import kr.jclab.jsxmlinterpreter.internal.VarContainer;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;

import java.util.Map;

public abstract class AbstractInstruction implements Instruction {
    protected AbstractInstruction nextInstruction;

    public void setNextInstruction(AbstractInstruction instruction) {
        this.nextInstruction = instruction;
    }

    public static JexlContext createJexlContext(BlockContext blockContext) {
        JexlContext context = new MapContext();
        for(Map.Entry<String, VarContainer> var : blockContext.getVarMap().entrySet()) {
            context.set(var.getKey(), var.getValue().getValue());
        }
        return context;
    }

    public static void executeSubBlock(JsXMLInterpreter interpreter, BlockContext blockContext, Instruction instruction) throws ExecuteRuntimeException {
        Instruction curInst = instruction;
        while(curInst != null) {
            curInst = curInst.doInstruction(interpreter, blockContext);
            if(blockContext.isReturned() || blockContext.isBreaked())
                break;
        }
    }

    public static void executeNewSubBlock(JsXMLInterpreter interpreter, BlockContext blockContext, Instruction instruction) throws ExecuteRuntimeException {
        BlockContext subBlockContext = new DefaultBlockContext(blockContext);
        executeSubBlock(interpreter, subBlockContext, instruction);
    }
}
