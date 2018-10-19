
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

****************************************************
Tutorial: Testing the vFW flow in a standalone PDP-D 
****************************************************

.. contents::
    :depth: 3


High Level Architecture
^^^^^^^^^^^^^^^^^^^^^^^ 
The vFW flow begins with an onset message that is sent from DCAE notifying the PDP-D that an action needs to be taken on a VNF. Once the PDP-D has inserted the onset into drools memory, rules begin to fire to start processing the onset for the vFW policy that exists in drools memory. If the onset is not enriched with A&AI data, Policy will query A&AI for the VNF data otherwise the PDP-D will get the A&AI data needed directly from the onset. Then an A&AI named query is executed on the source VNF entity from the onset to find the target VNF entity that the PDP-D will take action on. Once the target entity is retrieved from A&AI, a Guard query is executed to determine if the action to be taken is allowed. If Guard returns a permit, the PDP-D will then send an APPC ModifyConfig recipe request to modify pg-streams as specified in the request payload. If APPC is successful then the PDP-D will send a final success notification on the POLICY-CL-MGT topic and gracefully end processing the event.

Initial Setup
^^^^^^^^^^^^^ 

For this tutorial, a feature for simulating components involved in the flow outside of Policy will be turned on. Run "*features enable controlloop-utils*".

    .. image:: Tut_vFW_simulators_enabled.JPG

Now start the PDP-D using the command "policy start"

    .. image:: Tut_vFW_policy_start.JPG

Running the Flow
^^^^^^^^^^^^^^^^ 

The telemetry API is used to see what is in memory. There should only be 1 fact, the Params object which is created at initialization time and contains the vFW policy that was created.

    .. code-block:: bash

        curl -k --silent --user @1b3rt:31nst31n -X GET https://localhost:9696/policy/pdp/engine/controllers/amsterdam/drools/facts/amsterdam | python -m json.tool

    .. image:: Tut_vFW_get_facts.JPG

Using the telemetry API, a simulated onset can be injected by the user. For demo purposes, this is the simulated onset that will be used:

    .. image:: Tut_vFW_simulated_onset.JPG

**NOTE:** The onset that gets injected has to have a closedLoopControlName that matches the pushed policy's closedLoopControlName.

Inject the onset using the Telemetry API.

    .. code-block:: bash

        curl -k --silent --user @1b3rt:31nst31n --header "Content-Type: text/plain" --data @dcae.vfw.onset.json -X PUT https://localhost:9696/policy/pdp/engine/topics/sources/ueb/unauthenticated.DCAE_EVENT_OUTPUT/events | python -m json.tool

    .. image:: Tut_vFW_onset_injected.JPG

Now check the facts in memory, there should be 7 objects present. Two timers exist to put a time limit on the operation and on the overall control loop (in the case of retries or policy chaining). The event and it's associated manager and operation manager are also present in memory. A lock on the target entity is inserted to ensure no other events try to take action on the VNF that is currently processing.

    .. image:: Tut_vFW_get_facts_2.JPG

The network log will be used to monitor the activity coming in and out of the PDP-D. This log is located at *$POLICY_HOME/logs/network.log*. This will show the notifications that the PDP-D sends out at different stages of processing. The order of successful processing begins with an ACTIVE notification to show that the onset was acknowledged and the operation is beginning transit.
 
    .. image:: Tut_vFW_policy_active.JPG

Next a query will be sent to A&AI to get information on the VNF specified from the onset. The picture below shows the query going OUT of the PDP-D and the response coming IN.

**NOTE:** Policy does A&AI queries for VNF information when the onset is not enriched with A&AI data. In this example only the generic-vnf.vnf-name was provided so a query to A&AI is necessary to retrieve data that is needed in the APPC request.

    .. image:: Tut_vFW_aai_get.JPG

For the vFW use case, the source entity reported in the onset message may not be the target entity that the APPC operation takes action on. To determine the true target entity, an A&AI named query is performed. The request is shown in the network log.

    .. image:: Tut_vFW_aai_named_query_request.JPG

The response is also displayed in the network log.

    .. image:: Tut_vFW_aai_named_query_response.JPG

Once the target entity is found, the PDP-D consults Guard to determine if this operation should be allowed, a series of operation notifications are sent for starting the Guard query, obtaining a PERMIT or DENY, and beginning the operation.

    .. image:: Tut_vFW_policy_guard_start.JPG

|

    .. image:: Tut_vFW_policy_guard_result.JPG

|

    .. image:: Tut_vFW_policy_operation_start.JPG

Once the operation starts an APPC request is sent out.

    .. image:: Tut_vFW_appc_request.JPG

A simulated APPC response will be injected to the APPC-CL topic, this is the example response used:

    .. image:: Tut_vFW_simulated_appc_response.JPG

Inject the response using the Telemetry API.

    .. code-block:: bash

        curl -k --silent --user @1b3rt:31nst31n --header "Content-Type: text/plain" --data @appc.legacy.success.json -X PUT https://localhost:9696/policy/pdp/engine/topics/sources/ueb/APPC-CL/events | python -m json.tool

    .. image:: Tut_vFW_insert_appc_response.JPG

The network log will show the PDP-D sent an operation success notification.

    .. image:: Tut_vFW_policy_operation_success.JPG

Then a final success notification is sent.

    .. image:: Tut_vFW_policy_final_success.JPG

After processing there should only be 1 fact left in memory.

    .. image:: Tut_vFW_final_memory.JPG




End of Document




.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Tutorial%3A+Testing+the+vFW+flow+in+a+standalone+PDP-D

