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

import java.util.HashMap;
import java.util.Map;

public class DefaultBlockContext implements BlockContext {
    /**
     * top block
     */
    private BlockContext parentBlockContext;

    /**
     * variables in block
     */
    private Map<String, VarContainer> vars;

    /**
     * return value
     */
    private Object retval = null;

    /**
     * @see {@link BlockContext.Flag}
     */
    private int flags;

    private boolean breaked = false;

    public DefaultBlockContext() {
        this(null);
    }

    public DefaultBlockContext(BlockContext parentBlockContext) {
        this.vars = new HashMap<>();
        this.parentBlockContext = parentBlockContext;
        this.flags = 0;
        if(parentBlockContext != null) {
            if(Flag.check(parentBlockContext.getFlags(), Flag.Breakable)) {
                this.flags |= Flag.Breakable.getValue();
            }
            for(Map.Entry<String, VarContainer> entry : parentBlockContext.getVarMap().entrySet()) {
                this.vars.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public BlockContext getScopeTopBlockContext() {
        BlockContext curNode = this;
        BlockContext target = curNode;
        while(curNode != null) {
            target = curNode;
            curNode = curNode.getParentBlockContext();
        }
        return target;
    }

    @Override
    public BlockContext getParentBlockContext() {
        return this.parentBlockContext;
    }

    @Override
    public int getFlags() {
        return this.flags;
    }

    public void setFlag(Flag flag) {
        this.flags |= flag.getValue();
    }

    @Override
    public void setRetval(Object retval) {
        this.retval = retval;
        if(this.parentBlockContext != null) {
            this.parentBlockContext.setRetval(retval);
        }
    }

    @Override
    public Object getRetval() {
        return retval;
    }

    @Override
    public VarContainer createLocalVar(String name, VarType varType) {
        VarContainer managedVariable = new VarContainer(varType);
        this.vars.put(name, managedVariable);
        return managedVariable;
    }

    @Override
    public Object getVar(String name) {
        VarContainer managedVariable = this.vars.get(name);
        if(managedVariable == null)
            return null;
        return managedVariable.getValue();
    }

    @Override
    public boolean containsVar(String name) {
        return this.vars.containsKey(name);
    }

    @Override
    public Map<String, VarContainer> getVarMap() {
        return this.vars;
    }

    @Override
    public VarContainer getVarContainer(String name, boolean createIfNotExists, String varType) {
        VarContainer varContainer = this.vars.get(name);
        if(varContainer == null && createIfNotExists) {
            varContainer = new VarContainer(VarContainer.findVarType(varType));
            this.vars.put(name, varContainer);
        }
        return varContainer;
    }


    @Override
    public boolean isBreaked() {
        return this.breaked;
    }

    @Override
    public void setBreak() {
        this.breaked = true;
    }

    @Override
    public boolean isReturned() {
        return this.retval != null;
    }
}
