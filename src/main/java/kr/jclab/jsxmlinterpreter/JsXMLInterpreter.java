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
package kr.jclab.jsxmlinterpreter;

import kr.jclab.jsxmlinterpreter.instruction.AbstractInstruction;
import kr.jclab.jsxmlinterpreter.instruction.Instruction;
import kr.jclab.jsxmlinterpreter.internal.VarContainer;

import java.util.Map;

public class JsXMLInterpreter {
    public enum CodeType {
        Macro(1),
        User(2);

        int value;
        CodeType(int value) {
            this.value = value;
        }
        public int getValue() {
            return this.value;
        }
    }

    protected Map<String, AbstractInstruction> m_methods;

    JsXMLInterpreter(Map<String, AbstractInstruction> methods) {
        m_methods = methods;
    }

    public Object callMacro(String name) throws JsXMLInterpreterException {
        return callMacro(name, null);
    }

    public Object callUsercode(String tagName, String methodName) throws JsXMLInterpreterException {
        return callUsercode(tagName, methodName, null);
    }

    public Object callMacro(String name, BlockContext blockContext) throws JsXMLInterpreterException {
        return call(CodeType.Macro, name, blockContext);
    }

    public Object callUsercode(String tagName, String methodName, BlockContext blockContext) throws JsXMLInterpreterException {
        return call(CodeType.User, tagName + ":" + methodName, blockContext);
    }

    public Object call(CodeType codeType, String key, BlockContext blockContext) throws JsXMLInterpreterException {
        AbstractInstruction firstInstraction = null;

        if(blockContext == null) {
            blockContext = new DefaultBlockContext();
        }

        if(codeType == CodeType.Macro) {
            String methodKey = "MACRO:" + key;
            firstInstraction = m_methods.get(methodKey);
            if(firstInstraction == null) {
                throw new NotDefinedTagException(methodKey);
            }
        }else if(codeType == CodeType.User) {
            String methodKey = "USERCODE:" + key;
            firstInstraction = m_methods.get(methodKey);
            if(firstInstraction == null) {
                throw new NotDefinedTagException(methodKey);
            }
        }else{
            throw new JsXMLInterpreterException("Unknown codeType");
        }

        Instruction curInst = firstInstraction;
        while(curInst != null) {
            curInst = curInst.doInstruction(this, blockContext);
            if(blockContext.isReturned())
                break;
        }

        Object retval = blockContext.getRetval();
        if(retval instanceof VarContainer) {
            return ((VarContainer)retval).getValue();
        }
        return retval;
    }
}
