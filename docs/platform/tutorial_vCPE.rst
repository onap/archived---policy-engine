
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

*********************************************************
Tutorial: Testing the vCPE use case in a standalone PDP-D 
*********************************************************

.. contents::
    :depth: 3


High Level Architecture
^^^^^^^^^^^^^^^^^^^^^^^ 
The vCPE flow begins with an onset message that is sent from DCAE notifying the PDP-D that an action needs to be taken on a VM/VNF. Once the PDP-D has inserted the onset into drools memory, rules begin to fire to start processing the onset for the vCPE policy that exists in drools memory. If the onset is not enriched with A&AI data, Policy will query A&AI for the VM/VNF data otherwise the PDP-D will get the A&AI data needed directly from the onset. A Guard query is then executed to determine if the action to be taken is allowed. If Guard returns a permit, the PDP-D will then send an APPC Restart recipe request to restart the VM/VNF specified in the request. If APPC is successful then the PDP-D will send a operation success notification on the POLICY-CL-MGT topic. The PDP-D waits for an abatement message to come from DCAE before ending the transaction. Once the abatement is received the PDP-D sends a final success notification and gracefully ends processing the event.

Initial Setup
^^^^^^^^^^^^^ 

For this tutorial, a feature for simulating components involved in the flow outside of Policy will be turned on. Run "*features enable controlloop-utils*".

    .. image:: Tut_vCPE_simulators_enabled.JPG

Now start the PDP-D using the command "policy start"

    .. image:: Tut_vCPE_policy_start.JPG

Running the Flow
^^^^^^^^^^^^^^^^ 

The telemetry API is used to see what is in memory. There should only be 1 fact, the Params object which is created at initialization time and contains the vCPE policy that was created.

    .. code-block:: bash

        curl -k --silent --user @1b3rt:31nst31n -X GET https://localhost:9696/policy/pdp/engine/controllers/amsterdam/drools/facts/amsterdam | python -m json.tool


    .. image:: Tut_vCPE_get_facts.JPG

Using the telemetry API, a simulated onset can be injected by the user. For demo purposes, this is the simulated onset that will be used:

    .. image:: Tut_vCPE_simulated_onset.JPG

**NOTE:** The onset that gets injected has to have a closedLoopControlName that matches the pushed policy's closedLoopControlName.

Inject the onset using the Telemetry API.

    .. code-block:: bash

        curl -k --silent --user @1b3rt:31nst31n --header "Content-Type: text/plain" --data @dcae.vcpe.onset.json -X PUT https://localhost:9696/policy/pdp/engine/topics/sources/ueb/unauthenticated.DCAE_EVENT_OUTPUT/events | python -m json.tool

    .. image:: Tut_vCPE_insert_onset.JPG

**NOTE:** The simulated onset is enriched with A&AI data. The PDP-D will not make an A&AI query since the data needed can be extracted from the onset.

Now check the facts in memory, there should be 7 objects present. Two timers exist to put a time limit on the operation and on the overall control loop (in the case of retries or policy chaining). The event and it's associated manager and operation manager are also present in memory. A lock on the target entity is inserted to ensure no other events try to take action on the VM/VNF that is currently processing.

    .. image:: Tut_vCPE_get_facts_2.JPG

The network log will be used to monitor the activity coming in and out of the PDP-D. This log is located at *$POLICY_HOME/logs/network.log*. This will show the notifications that the PDP-D sends out at different stages of processing. The order of successful processing begins with an ACTIVE notification to show that the onset was acknowledged and the operation is beginning transit.
 
    .. image:: Tut_vCPE_policy_active.JPG

Once the event is in the ACTIVE state, the PDP-D consults Guard to determine if this operation should be allowed, a series of operation notifications are sent for starting the Guard query, obtaining a PERMIT or DENY, and beginning the operation.

    .. image:: Tut_vCPE_guard_not_queried.JPG

|

    .. image:: Tut_vCPE_guard_result.JPG

|

    .. image:: Tut_vCPE_policy_operation.JPG

Once the operation starts an APPC request is sent out.

    .. image:: Tut_vCPE_appc_request.JPG

A simulated APPC response will be injected to the APPC-LCM-WRITE topic, this is the example response used:

    .. image:: Tut_vCPE_simulated_appc_response.JPG

Inject the response using the Telemetry API.

    .. code-block:: bash

        curl -k --silent --user @1b3rt:31nst31n --header "Content-Type: text/plain" --data @appc.lcm.success.json -X PUT https://localhost:9696/policy/pdp/engine/topics/sources/ueb/APPC-LCM-WRITE/events | python -m json.tool

    .. image:: Tut_vCPE_inject_appc_response.JPG

The network log will show the PDP-D sent an operation success notification.

    .. image:: Tut_vCPE_policy_operation_success.JPG

For the vCPE use case, once an operation is successful, the PDP-D waits for DCAE to send an abatement message to end processing. The following abatement message will be used:

    .. image:: Tut_vCPE_simulated_abatement.JPG

Inject the abatement message using the Telemetry API.

    .. code-block:: bash

        curl -k --silent --user @1b3rt:31nst31n --header "Content-Type: text/plain" --data @dcae.vcpe.abatement.json -X PUT https://localhost:9696/policy/pdp/engine/topics/sources/ueb/unauthenticated.DCAE_EVENT_OUTPUT/events | python -m json.tool

    .. image:: Tut_vCPE_insert_abatement.JPG

Once the abatement is received, a final success notification is sent from the PDP-D.

    .. image:: Tut_vCPE_policy_final_success.JPG

After processing there should only be 1 fact left in memory.

    .. image:: Tut_vCPE_final_memory.JPG


End of Document


.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Tutorial%3A+Testing+the+vCPE+use+case+in+a+standalone+PDP-D


