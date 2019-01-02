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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum VarType {
    V_None(null),
    V_Byte("byte"),
    V_Short("short"),
    V_Int("int"),
    V_Long("long"),
    V_Bool("bool", "boolean"),
    V_String("string"),
    V_Float("float"),
    V_Double("double"),
    V_List("list"),
    V_Map("map");

    String typeName;
    Set<String> typeNames;
    VarType(String... typeNames) {
        if(typeNames == null) {
            this.typeName = null;
            this.typeNames = new HashSet<>();
        }else {
            this.typeName = typeNames[0];
            this.typeNames = new HashSet<>(Arrays.asList(typeNames));
        }
    }
    public String getTypeName() {
        return this.typeName;
    }
    public Set<String> getTypeNames() {
        return Collections.unmodifiableSet(this.typeNames);
    }
}
