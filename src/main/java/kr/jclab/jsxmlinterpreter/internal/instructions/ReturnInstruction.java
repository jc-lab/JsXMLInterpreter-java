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
import kr.jclab.jsxmlinterpreter.instruction.AbstractInstruction;
import kr.jclab.jsxmlinterpreter.instruction.Instruction;
import kr.jclab.jsxmlinterpreter.internal.VarContainer;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;

import java.util.Map;

public class ReturnInstruction extends AbstractInstruction {
    private String varType;
    private Expression varRef;
    private Object varValue;

    public static class Builder {
        private ReturnInstruction target;

        public Builder() {
            this.target = new ReturnInstruction();
        }

        public Builder varType(String varType) {
            this.target.varType = varType;
            return this;
        }

        public Builder varRef(Expression varRef) {
            this.target.varRef = varRef;
            return this;
        }

        public Builder varValue(Object varValue) {
            this.target.varValue = varValue;
            return this;
        }

        public ReturnInstruction build() {
            return this.target;
        }
    }

    public ReturnInstruction() {
        this.varType = null;
        this.varRef = null;
        this.varValue = null;
    }

    @Override
    public Instruction doInstruction(JsXMLInterpreter interpreter, BlockContext blockContext) throws ExecuteRuntimeException {
        if(this.varRef != null) {
            JexlContext context = createJexlContext(blockContext);
            blockContext.setRetval(this.varRef.evaluate(context));
        }else{
            blockContext.setRetval(new VarContainer.Builder(this.varType)
                    .value(this.varValue)
                    .build());
        }
        return null;
    }
}
