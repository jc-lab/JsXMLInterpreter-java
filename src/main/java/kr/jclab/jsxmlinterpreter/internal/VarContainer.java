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
package kr.jclab.jsxmlinterpreter.internal;

import kr.jclab.jsxmlinterpreter.UnacceptableCommandException;
import kr.jclab.jsxmlinterpreter.VarType;

import java.util.List;
import java.util.Map;

public class VarContainer {
    private VarType type;
    private Object value;

    public VarContainer(VarType type) {
        this(type, null);
    }

    public VarContainer(VarType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public void setValue(Object value) throws UnacceptableCommandException {
        if(value instanceof VarContainer) {
            VarContainer varContainer = (VarContainer)value;
            this.type = varContainer.type;
            this.value = varContainer.value;
        }else {
            this.value = castValue(this.type, value);
        }
    }

    public VarType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public static VarType findVarType(String strType) {
        if(strType == null)
            return VarType.V_None;
        for(VarType item : VarType.values()) {
            if(item.getTypeNames().contains(strType.toLowerCase())) {
                return item;
            }
        }
        return null;
    }

    public static VarType findVarTypeByObject(Object object) {
        if(object instanceof VarContainer) {
            VarContainer varContainer = (VarContainer)object;
            return varContainer.getType();
        }else if(object instanceof Byte) {
            return VarType.V_Byte;
        }else if(object instanceof Short) {
            return VarType.V_Short;
        }else if(object instanceof Integer) {
            return VarType.V_Int;
        }else if(object instanceof Long) {
            return VarType.V_Long;
        }else if(object instanceof Boolean) {
            return VarType.V_Bool;
        }else if(object instanceof Float) {
            return VarType.V_Float;
        }else if(object instanceof Double) {
            return VarType.V_Double;
        }else if(object instanceof String) {
            return VarType.V_String;
        }else if(object instanceof List) {
            return VarType.V_List;
        }else if(object instanceof Map) {
            return VarType.V_Map;
        }
        return VarType.V_None;
    }

    public static boolean verifyValueByType(VarType type, Object value) {
        if(value == null)
            return true;

        return false;
    }

    public static <T> T castValue(VarType varType, Object object) throws UnacceptableCommandException {
        if(object instanceof VarContainer) {
            object = ((VarContainer) object).getValue();
        }

        if(object == null)
            return null;

        switch(varType) {
            case V_Byte:
                if ((object instanceof Float) || (object instanceof Double) || (object instanceof Byte) || (object instanceof Short) || (object instanceof Integer) || (object instanceof Long)) {
                    return (T)((Byte)object);
                }
                break;
            case V_Short:
                if ((object instanceof Float) || (object instanceof Double) || (object instanceof Byte) || (object instanceof Short) || (object instanceof Integer) || (object instanceof Long)) {
                    return (T)((Short)object);
                }
                break;
            case V_Int:
                if ((object instanceof Float) || (object instanceof Double) || (object instanceof Byte) || (object instanceof Short) || (object instanceof Integer) || (object instanceof Long)) {
                    return (T)((Integer)object);
                }
                break;
            case V_Long:
                if ((object instanceof Float) || (object instanceof Double) || (object instanceof Byte) || (object instanceof Short) || (object instanceof Integer) || (object instanceof Long)) {
                    return (T)((Long)object);
                }
                break;
            case V_Bool:
                if(object instanceof Boolean) {
                    return (T)object;
                }else if(object instanceof Byte) {
                    if((Byte)object != 0)
                        return (T)Boolean.TRUE;
                }else if(object instanceof Short) {
                    if((Short)object != 0)
                        return (T)Boolean.TRUE;
                }else if(object instanceof Integer) {
                    if((Integer)object != 0)
                        return (T) Boolean.TRUE;
                }else if(object instanceof Long) {
                    if((Long)object != 0)
                        return (T) Boolean.TRUE;
                }else if(object instanceof String) {
                    return (T)new Boolean(!((String)object).isEmpty());
                }else {
                    return (T) Boolean.TRUE;
                }
                return (T) Boolean.FALSE;
            case V_Float:
            case V_Double:
                if ((object instanceof Float) || (object instanceof Double) || (object instanceof Byte) || (object instanceof Short) || (object instanceof Integer) || (object instanceof Long)) {
                    return (T)object;
                }else if(object instanceof String) {
                    return (T)Float.valueOf((String)object);
                }
            case V_String:
                if(object instanceof String) {
                    return (T)object;
                }else{
                    return (T)String.valueOf(object);
                }
            case V_List:
                if(object instanceof List) {
                    return (T)object;
                }
                break;
            case V_Map:
                if(object instanceof Map) {
                    return (T)object;
                }
                break;
        }
        throw new UnacceptableCommandException("'" + varType.getTypeName() + "' incompatible type with " + object.getClass().getName());
    }

    public static class Builder {
        private VarContainer target;
        private Object tempValue = null;

        public Builder(String type) {
            this.target = new VarContainer(VarContainer.findVarType(type));
        }

        public Builder(VarType type) {
            this.target = new VarContainer(type);
        }

        public Builder value(Object value) {
            this.tempValue = value;
            return this;
        }

        public VarContainer build() throws UnacceptableCommandException {
            this.target.value = castValue(this.target.type, this.tempValue);
            return this.target;
        }
    }
}
