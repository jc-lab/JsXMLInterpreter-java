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
import kr.jclab.jsxmlinterpreter.exceptions.InvalidInstructionException;
import kr.jclab.jsxmlinterpreter.exceptions.ParseException;
import kr.jclab.jsxmlinterpreter.exceptions.UnacceptableCommandException;
import kr.jclab.jsxmlinterpreter.instruction.AbstractInstruction;
import kr.jclab.jsxmlinterpreter.instruction.Instruction;
import kr.jclab.jsxmlinterpreter.DefaultBlockContext;
import kr.jclab.jsxmlinterpreter.VarContainer;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;

import java.util.List;
import java.util.Map;

public class ForeachInstruction extends AbstractInstruction {
    private String varRefText;
    private Expression varRefExpr;
    private String keyVarName;
    private String itemVarName;

    private Instruction statmentInstruction;

    public static class Builder {
        private ForeachInstruction target;

        public Builder() {
            this.target = new ForeachInstruction();
        }

        public Builder ref(String text, Expression expr) {
            this.target.varRefText = text;
            this.target.varRefExpr = expr;
            return this;
        }

        public Builder keyVarName(String keyVarName) {
            this.target.keyVarName = keyVarName;
            return this;
        }

        public Builder itemVarName(String itemVarName) {
            this.target.itemVarName = itemVarName;
            return this;
        }

        public Builder statmentInstruction(Instruction statmentInstruction) {
            this.target.statmentInstruction = statmentInstruction;
            return this;
        }

        public ForeachInstruction build() throws ParseException {
            if(this.target.varRefExpr == null) {
                throw new InvalidInstructionException("no ref");
            }
            return this.target;
        }
    }

    public ForeachInstruction() {
    }

    @Override
    public Instruction doInstruction(JsXMLInterpreter interpreter, BlockContext blockContext) throws ExecuteRuntimeException {
        VarType varType = VarType.V_None;
        JexlContext context = createJexlContext(blockContext);
        Object object = this.varRefExpr.evaluate(context);

        if(object instanceof VarContainer) {
            VarContainer varContainer = (VarContainer)object;
            varType = varContainer.getType();
            object = varContainer.getValue();
        }else if(object instanceof List) {
            varType = VarType.V_List;
        }else if(object instanceof Map) {
            varType = VarType.V_Map;
        }
        if(varType == VarType.V_None) {
            throw new UnacceptableCommandException("'" + this.varRefText + "' not list or map");
        }

        if(varType == VarType.V_List) {
            List list = (List)object;
            for(Object element : list) {
                DefaultBlockContext subBlockContext = new DefaultBlockContext(blockContext);
                VarContainer varValue = subBlockContext.createLocalVar(itemVarName, VarContainer.findVarTypeByObject(element));
                varValue.setValue(element);
                subBlockContext.setFlag(BlockContext.Flag.Breakable);
                 executeSubBlock(interpreter, subBlockContext, this.statmentInstruction);
            }
        }else if(varType == VarType.V_Map) {
            Map map = (Map)object;
            for(Object element : map.entrySet()) {
                Map.Entry entry = (Map.Entry)element;
                DefaultBlockContext subBlockContext = new DefaultBlockContext(blockContext);
                VarContainer varKey = subBlockContext.createLocalVar(keyVarName, VarType.V_String);
                VarContainer varValue = subBlockContext.createLocalVar(itemVarName, VarType.V_String);
                varKey.setValue(entry.getKey());
                varValue.setValue(entry.getValue());
                subBlockContext.setFlag(BlockContext.Flag.Breakable);
                executeSubBlock(interpreter, subBlockContext, this.statmentInstruction);
            }
        }

        return this.nextInstruction;
    }
}
