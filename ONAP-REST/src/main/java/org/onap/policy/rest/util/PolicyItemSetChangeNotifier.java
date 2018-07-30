/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.rest.util;


import java.io.Serializable;
import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedList;

import org.onap.policy.rest.util.PolicyContainer.ItemSetChangeEvent;
import org.onap.policy.rest.util.PolicyContainer.ItemSetChangeListener;



public class PolicyItemSetChangeNotifier implements PolicyContainer.ItemSetChangeNotifier {
    private static final long serialVersionUID = 1L;
    private Collection<PolicyContainer.ItemSetChangeListener> itemSetChangeListeners = null;
    private PolicyContainer container = null;
    
    public PolicyItemSetChangeNotifier() {
        // Empty constructor
    }
    
    protected void setContainer(PolicyContainer c) {
        this.container = c;
    }

    @Override
    public void addItemSetChangeListener(ItemSetChangeListener listener) {
        if (getItemSetChangeListeners() == null) {
            setItemSetChangeListeners(new LinkedList<PolicyContainer.ItemSetChangeListener>());
        }
        getItemSetChangeListeners().add(listener);	}

    @Override
    public void removeItemSetChangeListener(ItemSetChangeListener listener) {
        if (getItemSetChangeListeners() != null) {
            getItemSetChangeListeners().remove(listener);
        }
    }

    protected static class BaseItemSetChangeEvent extends EventObject implements
        PolicyContainer.ItemSetChangeEvent, Serializable {
        private static final long serialVersionUID = 1L;

        protected BaseItemSetChangeEvent(PolicyContainer source) {
            super(source);
        }

        @Override
        public PolicyContainer getContainer() {
            return (PolicyContainer) getSource();
        }
    }

    protected void setItemSetChangeListeners(
            Collection<PolicyContainer.ItemSetChangeListener> itemSetChangeListeners) {
        this.itemSetChangeListeners = itemSetChangeListeners;
    }
    protected Collection<PolicyContainer.ItemSetChangeListener> getItemSetChangeListeners() {
        return itemSetChangeListeners;
    }
  
    protected void fireItemSetChange() {
        fireItemSetChange(new BaseItemSetChangeEvent(this.container));
    }

    protected void fireItemSetChange(ItemSetChangeEvent event) {
        if (getItemSetChangeListeners() != null) {
            final Object[] l = getItemSetChangeListeners().toArray();
            for (int i = 0; i < l.length; i++) {
                ((PolicyContainer.ItemSetChangeListener) l[i])
                        .containerItemSetChange(event);
            }
        }
    }
}
