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

import kr.jclab.jsxmlinterpreter.exceptions.UnacceptableCommandException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
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

    public static Object newInitialObject(VarType varType) {
        switch (varType) {
            case V_Byte:
                return new Byte((byte)0);
            case V_Short:
                return new Short((short)0);
            case V_Int:
                return new Integer(0);
            case V_Long:
                return new Long((long)0);
            case V_Bool:
                return Boolean.FALSE;
            case V_Float:
                return new Float(0.0);
            case V_Double:
                return new Double(0.0);
            case V_String:
                return new String();
            case V_List:
                return new ArrayList();
            case V_Map:
                return new HashMap();
        }
        return null;
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

        try {
            switch (varType) {
                case V_Byte:
                    if ((object instanceof Float) || (object instanceof Double) || (object instanceof Byte) || (object instanceof Short) || (object instanceof Integer) || (object instanceof Long)) {
                        return (T) ((Byte) object);
                    } else if (object instanceof String) {
                        return (T) Byte.valueOf((String) object);
                    } else if(object instanceof BigInteger) {
                        return (T)(new Integer(((BigInteger)object).intValue()));
                    }
                    break;
                case V_Short:
                    if ((object instanceof Float) || (object instanceof Double) || (object instanceof Byte) || (object instanceof Short) || (object instanceof Integer) || (object instanceof Long)) {
                        return (T) ((Short) object);
                    } else if (object instanceof String) {
                        return (T) Short.valueOf((String) object);
                    } else if(object instanceof BigInteger) {
                        return (T)(new Integer(((BigInteger)object).intValue()));
                    }
                    break;
                case V_Int:
                    if ((object instanceof Float) || (object instanceof Double) || (object instanceof Byte) || (object instanceof Short) || (object instanceof Integer) || (object instanceof Long)) {
                        return (T) ((Integer) object);
                    } else if (object instanceof String) {
                        return (T) Integer.valueOf((String) object);
                    } else if(object instanceof BigInteger) {
                        return (T)(new Integer(((BigInteger)object).intValue()));
                    }
                    break;
                case V_Long:
                    if ((object instanceof Float) || (object instanceof Double) || (object instanceof Byte) || (object instanceof Short) || (object instanceof Integer) || (object instanceof Long)) {
                        return (T) ((Long) object);
                    } else if (object instanceof String) {
                        return (T) Long.valueOf((String) object);
                    } else if(object instanceof BigInteger) {
                        return (T)(new Long(((BigInteger)object).longValue()));
                    }
                    break;
                case V_Bool:
                    if (object instanceof Boolean) {
                        return (T) object;
                    } else if (object instanceof Byte) {
                        if ((Byte) object != 0)
                            return (T) Boolean.TRUE;
                    } else if (object instanceof Short) {
                        if ((Short) object != 0)
                            return (T) Boolean.TRUE;
                    } else if (object instanceof Integer) {
                        if ((Integer) object != 0)
                            return (T) Boolean.TRUE;
                    } else if (object instanceof Long) {
                        if ((Long) object != 0)
                            return (T) Boolean.TRUE;
                    } else if (object instanceof String) {
                        return (T) Boolean.valueOf((String)object);
                    } else if(object instanceof BigInteger) {
                        return (T)(((BigInteger)object).intValue() == 0 ? Boolean.FALSE : Boolean.TRUE);
                    } else {
                        return (T) Boolean.TRUE;
                    }
                    return (T) Boolean.FALSE;
                case V_Float:
                    if (object instanceof String) {
                        return (T) Float.valueOf((String) object);
                    } else if(object instanceof BigInteger) {
                        return (T)(new Float(((BigInteger)object).floatValue()));
                    }
                case V_Double:
                    if (object instanceof String) {
                        return (T) Double.valueOf((String) object);
                    }else if ((object instanceof Float) || (object instanceof Double) || (object instanceof Byte) || (object instanceof Short) || (object instanceof Integer) || (object instanceof Long)) {
                        return (T) object;
                    } else if(object instanceof BigInteger) {
                        return (T)(new Double(((BigInteger)object).doubleValue()));
                    }
                    break;
                case V_String:
                    if (object instanceof String) {
                        return (T) object;
                    } else {
                        return (T) String.valueOf(object);
                    }
                case V_List:
                    if (object instanceof List) {
                        return (T) object;
                    }
                    break;
                case V_Map:
                    if (object instanceof Map) {
                        return (T) object;
                    }
                    break;
            }
        }catch(NumberFormatException numberFormatException) {
            throw new UnacceptableCommandException(numberFormatException);
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
