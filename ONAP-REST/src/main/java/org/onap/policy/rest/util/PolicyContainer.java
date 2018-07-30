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
import java.util.List;


public interface PolicyContainer extends Serializable{

    public Collection<?> getContainerPropertyIds();
    
    public Collection<?> getItemIds();

    public Class<?> getType(Object propertyId);

    public int size();
  
    public boolean containsId(Object itemId);

    public Object addItem();
    
    public boolean removeItem(Object itemId);

    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue);
   
    public boolean removeContainerProperty(Object propertyId);

    public boolean removeAllItems();

    public interface Ordered extends PolicyContainer {

        public Object nextItemId(Object itemId);

        public Object prevItemId(Object itemId);

        public Object firstItemId();

        public Object lastItemId();

        public boolean isFirstId(Object itemId);

        public boolean isLastId(Object itemId);

        public Object addItemAfter(Object previousItemId);
        
    }

    
    public interface Indexed extends Ordered {

        public int indexOfId(Object itemId);

        public Object getIdByIndex(int index);

        public List<?> getItemIds(int startIndex, int numberOfItems);
        
        public Object addItemAt(int index);

        public interface ItemAddEvent extends ItemSetChangeEvent {

            public Object getFirstItemId();

            public int getFirstIndex();

            public int getAddedItemsCount();
        }

     
        public interface ItemRemoveEvent extends ItemSetChangeEvent {
          
            public Object getFirstItemId();

            public int getFirstIndex();

            public int getRemovedItemsCount();
        }
    }
  
    public interface ItemSetChangeEvent extends Serializable {

        public PolicyContainer getContainer();
    }

    public interface ItemSetChangeListener extends Serializable {

        public void containerItemSetChange(PolicyContainer.ItemSetChangeEvent event);
    }

    public interface ItemSetChangeNotifier extends Serializable {

        public void addItemSetChangeListener(
                PolicyContainer.ItemSetChangeListener listener);

        public void removeItemSetChangeListener(
                PolicyContainer.ItemSetChangeListener listener);
    }
}
