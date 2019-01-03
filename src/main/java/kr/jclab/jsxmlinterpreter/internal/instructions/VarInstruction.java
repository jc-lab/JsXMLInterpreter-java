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
import kr.jclab.jsxmlinterpreter.VarContainer;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;

import java.util.List;
import java.util.Map;

public class VarInstruction extends AbstractInstruction {
    private String varName;
    private String varType;
    private String varRefText;
    private Expression varRefExpr;
    private Object varValue;
    private String varOperation;
    private String varKeyName;
    private String varKeyRefText;
    private Expression varKeyRefExpr;
    private String varTo;

    public static class Builder {
        private VarInstruction target;

        public Builder() {
            this.target = new VarInstruction();
        }

        public Builder varName(String varName) {
            this.target.varName = varName;
            return this;
        }

        public Builder varType(String varType) {
            this.target.varType = varType;
            return this;
        }

        public Builder varRef(String varRefText, Expression varRefExpr) {
            this.target.varRefText = varRefText;
            this.target.varRefExpr = varRefExpr;
            return this;
        }

        public Builder varValue(Object varValue) {
            this.target.varValue = varValue;
            return this;
        }

        public Builder varOperation(String varOperation) {
            this.target.varOperation = varOperation;
            return this;
        }

        public Builder varKeyName(String varKeyName) {
            this.target.varKeyName = varKeyName;
            return this;
        }

        public Builder varKeyRef(String varKeyRefText, Expression varKeyRefExpr) {
            this.target.varKeyRefText = varKeyRefText;
            this.target.varKeyRefExpr = varKeyRefExpr;
            return this;
        }

        public Builder varTo(String varTo) {
            this.target.varTo = varTo;
            return this;
        }

        public VarInstruction build() throws ParseException {
            if(this.target.varName == null) {
                throw new InvalidInstructionException("'name' must be not null");
            }
            if(!this.target.varName.startsWith("$")) {
                throw new InvalidInstructionException("'name' must be start with '$'");
            }
            if(this.target.varOperation != null) {
                if("put".equalsIgnoreCase(this.target.varOperation)) {
                    if(this.target.varKeyName == null && this.target.varKeyRefExpr == null) {
                        throw new InvalidInstructionException("no have a 'key'");
                    }
                }else if("get".equalsIgnoreCase(this.target.varOperation)) {
                    if(this.target.varTo == null || this.target.varTo.isEmpty()) {
                        throw new InvalidInstructionException("no have a 'to'");
                    }
                    if(this.target.varKeyName == null && this.target.varKeyRefExpr == null) {
                        throw new InvalidInstructionException("no have a 'key'");
                    }
                }
            }
            return this.target;
        }
    }

    public VarInstruction() {
        this.varName = null;
        this.varType = null;
        this.varRefText = null;
        this.varRefExpr = null;
        this.varValue = null;
        this.varOperation = null;
        this.varKeyName = null;
        this.varKeyRefText = null;
        this.varKeyRefExpr = null;
        this.varTo = null;
    }

    private String evalVarKey(JexlContext context) {
        if(this.varKeyName != null) {
            return this.varKeyName;
        }else {
            Object object = this.varKeyRefExpr.evaluate(context);
            if (object instanceof VarContainer) {
                return (String) ((VarContainer) object).getValue();
            }
            return (String) object;
        }
    }

    @Override
    public Instruction doInstruction(JsXMLInterpreter interpreter, BlockContext blockContext) throws ExecuteRuntimeException {
        VarContainer varContainer = blockContext.getVarContainer(this.varName, true, this.varType);
        JexlContext context = createJexlContext(blockContext);
        Object valueObject;

        if (this.varValue != null) {
            valueObject = this.varValue;
        } else if(this.varRefExpr != null) {
            valueObject = this.varRefExpr.evaluate(context);
        } else {
            valueObject = VarContainer.newInitialObject(varContainer.getType());
        }

        if(this.varOperation == null) {
            varContainer.setValue(valueObject);
        }else{
            List list = (varContainer.getType() == VarType.V_List) ? (List)varContainer.getValue() : null;
            Map map = (varContainer.getType() == VarType.V_Map) ? (Map) varContainer.getValue() : null;

            if("put".equalsIgnoreCase(this.varOperation)) {
                if(varContainer.getType() != VarType.V_Map) {
                    throw new UnacceptableCommandException("Variable '" + this.varRefText + "' is not map");
                }
                map.put(evalVarKey(context), valueObject);
            }else
            if("add".equalsIgnoreCase(this.varOperation)) {
                if(varContainer.getType() != VarType.V_List) {
                    throw new UnacceptableCommandException("Variable '" + this.varRefText + "' is not list");
                }
                list.add(valueObject);
            }
            if("get".equalsIgnoreCase(this.varOperation)) {
                VarContainer varTo = blockContext.getVarContainer(this.varTo, true, null);
                if(varContainer.getType() == VarType.V_List) {
                    try {
                        Integer index = Integer.valueOf(this.evalVarKey(context));
                        varTo.setValue(list.get(index));
                    } catch (NumberFormatException e) {
                        throw new UnacceptableCommandException("Key '" + (this.varKeyName != null ? this.varKeyName : this.varKeyRefText) + "' is not number");
                    }
                }else if(varContainer.getType() == VarType.V_Map) {
                    varTo.setValue(map.get(evalVarKey(context)));
                }else {
                    throw new UnacceptableCommandException("Variable '" + this.varRefText + "' is not list or map");
                }
            }
        }

        return this.nextInstruction;
    }
}
