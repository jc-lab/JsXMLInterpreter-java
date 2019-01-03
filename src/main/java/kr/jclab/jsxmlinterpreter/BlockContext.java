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

import java.util.Map;

public interface BlockContext {
    enum Flag {
        None(0),
        Breakable(0x10000);

        int value;
        Flag(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static boolean check(int value, Flag flag) {
            return (value & flag.getValue()) != 0;
        }
    }

    BlockContext getScopeTopBlockContext();

    BlockContext getParentBlockContext();

    int getFlags();

    void setRetval(Object retval);

    Object getRetval();

    VarContainer createLocalVar(String name, VarType varType);

    Object getVar(String name);

    boolean containsVar(String name);

    Map<String, VarContainer> getVarMap();

    VarContainer getVarContainer(String name, boolean createIfNotExists, String varType);

    boolean isBreaked();

    void setBreak();

    boolean isReturned();
}
