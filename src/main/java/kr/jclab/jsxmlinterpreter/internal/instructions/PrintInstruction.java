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
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;

public class PrintInstruction extends AbstractInstruction {
    private Expression ref = null;
    private String text = null;

    public static class Builder {
        private PrintInstruction target = new PrintInstruction();

        public Builder ref(Expression ref) {
            this.target.ref = ref;
            return this;
        }
        public Builder text(String text) {
            this.target.text = text;
            return this;
        }

        public PrintInstruction build() {
            return this.target;
        }
    }

    @Override
    public Instruction doInstruction(JsXMLInterpreter interpreter, BlockContext blockContext) throws ExecuteRuntimeException {
        if(this.ref != null) {
            JexlContext context = createJexlContext(blockContext);
            System.out.println(this.ref.evaluate(context));
        }else {
            System.out.println(this.text);
        }
        return this.nextInstruction;
    }
}
