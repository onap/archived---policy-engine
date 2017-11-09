
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

*************************
Feature: Test Transaction
*************************

.. contents::
    :depth: 3

Summary
^^^^^^^

The Test Transaction feature provides a mechanism by which the health of drools policy controllers can be tested.

When enabled, the feature functions by injecting an event object (identified by a UUID) into the drools session of each policy controller that is active in the system. Only an object with this UUID  can trigger the Test Transaction-specific drools logic to execute.

The injection of the event triggers the "TT" rule (see *TestTransactionTemplate.drl* below) to fire. The "TT" rule simply increments a ForwardProgress counter object, thereby confirming that the drools session for this particular controller is active and firing its rules accordingly. This cycle repeats at 20 second intervals.

If it is ever the case that a drools controller does not have the "TT" rule present in its *.drl*, or that the forward progress counter is not incremented, the Test Transaction thread for that particular drools session (i.e. controller) is terminated and a message is logged to *error.log*.

Usage
^^^^^

Prior to being enabled, the following drools rules need to be appended to the rules templates of any use-case that is to be monitored by the feature.

    .. code-block:: java
       :caption: TestTransactionTemplate.drl
       :linenos:

        /* 
         * ============LICENSE_START=======================================================
         * feature-test-transaction
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
        
        package org.onap.policy.drools.rules;
        
        import java.util.EventObject;
        
        declare ForwardProgress
            counter : Long
        end
        
        rule "TT.SETUP"
        when
        then
            ForwardProgress fp = new ForwardProgress();
            fp.setCounter(0L);
            insert(fp);
        end
        
        rule "TT"
        when 
            $fp : ForwardProgress()
            $tt : EventObject(source == "43868e59-d1f3-43c2-bd6f-86f89a61eea5")
        then
            $fp.setCounter($fp.getCounter() + 1);
            retract($tt);
        end
        query "TT.FPC"
            ForwardProgress(counter >= 0, $ttc : counter)
        end

Once the proper artifacts are built and deployed with the addition of the TestTransactionTemplate rules, the feature can then be enabled by entering the following commands:

    .. code-block:: bash
       :caption: PDPD Features Command

        policy@hyperion-4:/opt/app/policy$ policy stop
        [drools-pdp-controllers]
         L []: Stopping Policy Management... Policy Management (pid=354) is stopping... Policy Management has stopped.
        policy@hyperion-4:/opt/app/policy$ features enable test-transaction
        name                      version         status
        ----                      -------         ------
        controlloop-utils         1.1.0-SNAPSHOT  disabled
        healthcheck               1.1.0-SNAPSHOT  disabled
        test-transaction          1.1.0-SNAPSHOT  enabled
        eelf                      1.1.0-SNAPSHOT  disabled
        state-management          1.1.0-SNAPSHOT  disabled
        active-standby-management 1.1.0-SNAPSHOT  disabled
        session-persistence       1.1.0-SNAPSHOT  disabled

The output of the enable command will indicate whether or not the feature was enabled successfully.

Policy engine can then be started as usual.


End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Feature+Test+Transaction
