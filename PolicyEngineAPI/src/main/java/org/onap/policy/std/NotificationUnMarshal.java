/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.onap.policy.api.LoadedPolicy;
import org.onap.policy.api.RemovedPolicy;
import org.onap.policy.api.UpdateType;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NotificationUnMarshal {

	public static StdPDPNotification notificationJSON(String json) throws IOException{
		ObjectMapper mapper = new ObjectMapper();
		StdPDPNotification notification = mapper.readValue(json, StdPDPNotification.class);
		if(notification!=null&&notification.getLoadedPolicies()!=null){
		    Collection<StdLoadedPolicy> stdLoadedPolicies = new ArrayList<>();
		    for(LoadedPolicy loadedPolicy: notification.getLoadedPolicies()){
		        StdLoadedPolicy stdLoadedPolicy = (StdLoadedPolicy) loadedPolicy;
		        if(notification.getRemovedPolicies()!=null){
		            Boolean updated = false;
		            for(RemovedPolicy removedPolicy: notification.getRemovedPolicies()){
		                String regex = ".(\\d)*.xml";
		                if(removedPolicy.getPolicyName().replaceAll(regex, "").equals(stdLoadedPolicy.getPolicyName().replaceAll(regex, ""))){
		                    updated  = true;
		                    break;
		                }
		            }
		            if(updated){
		                stdLoadedPolicy.setUpdateType(UpdateType.UPDATE);
		            }else{
		                stdLoadedPolicy.setUpdateType(UpdateType.NEW);
		            }
		        }else{
		            stdLoadedPolicy.setUpdateType(UpdateType.NEW);
		        }
		        stdLoadedPolicies.add(stdLoadedPolicy);
		    }
		    notification.setLoadedPolicies(stdLoadedPolicies);
		}
		return notification;
	}
}
