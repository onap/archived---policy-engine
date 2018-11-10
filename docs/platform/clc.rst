
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

**************************************
Policy Control Loop Coordination - POC
**************************************

.. contents::
    :depth: 3


What is the Control Loop Coordinator?
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
The Control Loop Coordinator (CLC) is a facility embedded within Policy that provides:

* Abstractions enabling an operator to specify how ONAP/ECOMP will coordinate the interactions between a given pair of Control Loop Functions (CLFs); 
* Mechanisms to ensure coordination directives are correctly enforced by ONAP/ECOMP at run-time.
 

How does the CLC work?
^^^^^^^^^^^^^^^^^^^^^^

The table below illustrates conflict between two CLFs at the functional and architectural views and resolution of that conflict via introduction of the CLC.

+-----------+-------------------------+-----------------------+
|  Scenario | Functional View         | Architectural View    |
+===========+=========================+=======================+
| Conflict  | .. image:: func.PNG     | .. image:: arch.PNG   |
|           |    :scale: 33           |    :scale: 33         |
+-----------+-------------------------+-----------------------+
| Resolution| .. image:: clcf.PNG     | .. image:: clca.PNG   |
|           |    :scale: 33           |    :scale: 33         |
+-----------+-------------------------+-----------------------+


How is the CLC implemented?
^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .. _template.demo.clc: https://git.onap.org/policy/drools-applications/tree/controlloop/templates/template.demo.clc

    .. _synthetic_control_loop_one_blocks_synthetic_control_loop_two.xml: https://git.onap.org/policy/drools-applications/tree/controlloop/templates/template.demo.clc/src/test/resources/xacml/synthetic_control_loop_one_blocks_synthetic_control_loop_two.xml

    .. _policy_ControlLoop_SyntheticOne.yaml: https://git.onap.org/policy/drools-applications/tree/controlloop/templates/template.demo.clc/src/test/resources/yaml/policy_ControlLoop_SyntheticOne.yaml

    .. _policy_ControlLoop_SyntheticTwo.yaml: https://git.onap.org/policy/drools-applications/tree/controlloop/templates/template.demo.clc/src/test/resources/yaml/policy_ControlLoop_SyntheticTwo.yaml

    .. _xacml_guard_clc.properties: https://git.onap.org/policy/drools-applications/tree/controlloop/templates/template.demo.clc/src/test/resources/xacml/xacml_guard_clc.properties

    .. _ControlLoopEventManager: https://git.onap.org/policy/drools-applications/tree/controlloop/templates/template.demo.clc/src/main/resources/__closedLoopControlName__.drl#n210
    
Example code is provided at `template.demo.clc`_.  The abstraction implemented for the initial release is simply a XACML policy (e.g., `synthetic_control_loop_one_blocks_synthetic_control_loop_two.xml`_) that matches against one CLF (e.g., `policy_ControlLoop_SyntheticOne.yaml`_) and checks the status of another CLF (e.g., `policy_ControlLoop_SyntheticTwo.yaml`_) via provided PIPs. The following release will provide a much more succinct YAML representation consisting of coordination_directive_type, control_loop_one_id, control_loop_two_id, and, optionally, one or more parameters, the semantics of which are defined by the coordination_directive_type.

The following figure provides a detailed overview of the call flow as implemented:

 .. image:: detailed_clc_flow.PNG
    :scale: 67


How do you run the example?
^^^^^^^^^^^^^^^^^^^^^^^^^^^
From within `template.demo.clc`_ run 

    .. code-block:: bash

     $ mvn test -Dtest=ControlLoopCoordinationTest
 
**NOTE:** When incorporating this example code into your own application, the XACML policies implementing the coordination directives must be included in the XACML .properties file used by the ONAP/ECOMP controller instance (e.g., `xacml_guard_clc.properties`_). The CLF’s drl file must also contain the following modification, to be included after the creation of the `ControlLoopEventManager`_.

    .. code-block:: bash
		
     // Disable target locking
     //
     manager.setUseTargetLock(false);
 

End of Document
