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
import kr.jclab.jsxmlinterpreter.instruction.AbstractInstruction;
import kr.jclab.jsxmlinterpreter.instruction.Instruction;
import kr.jclab.jsxmlinterpreter.internal.VarContainer;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;

import java.util.ArrayList;
import java.util.List;

public class IfInstruction extends AbstractInstruction {
    private Expression expression;
    private AbstractInstruction instructionIfTrue;
    private List<IfInstruction> elseIfInstrictionChain;
    private AbstractInstruction instructionIfFalse;

    public static class Builder {
        private IfInstruction target;

        public Builder(Expression expression) {
            target = new IfInstruction(expression);
        }
        public void trueInstruction(AbstractInstruction instruction) {
            target.instructionIfTrue = instruction;
        }
        public void elseIfInstruction(IfInstruction instruction) {
            target.elseIfInstrictionChain.add(instruction);
        }
        public void falseInstruction(AbstractInstruction instruction) {
            target.instructionIfFalse = instruction;
        }
        public IfInstruction build() {
            return this.target;
        }
    }

    public IfInstruction(Expression expression) {
        this.expression = expression;
        this.instructionIfTrue = null;
        this.elseIfInstrictionChain = new ArrayList<>();
        this.instructionIfFalse = null;
    }

    protected boolean checkCondition(BlockContext blockContext) {
        JexlContext context = createJexlContext(blockContext);
        Object result = this.expression.evaluate(context);
        Boolean condition = Boolean.FALSE;
        if(result != null) {
            try {
                condition = VarContainer.castValue(VarType.V_Bool, result);
            } catch (UnacceptableCommandException e) {
                condition = Boolean.TRUE;
            }
        }
        return condition;
    }

    @Override
    public Instruction doInstruction(JsXMLInterpreter interpreter, BlockContext blockContext) throws ExecuteRuntimeException {
        boolean condition = checkCondition(blockContext);
        if(condition) {
            executeNewSubBlock(interpreter, blockContext, instructionIfTrue);
        }else{
            for(IfInstruction nextIfInst : elseIfInstrictionChain) {
                condition = nextIfInst.checkCondition(blockContext);
                if(condition) {
                    nextIfInst.executeNewSubBlock(interpreter, blockContext, nextIfInst.instructionIfTrue);
                    break;
                }
            }
            if(!condition) {
                executeNewSubBlock(interpreter, blockContext, this.instructionIfFalse);
            }
        }
        return this.nextInstruction;
    }
}
