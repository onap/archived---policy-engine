
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

************************
Running PDP-D in Eclipse 
************************

.. contents::
    :depth: 3

This tutorial is intended for developers who would like to run the PDP-D in an Eclipse environment. It is assumed that the drools-pdp git project has been imported in an Eclipse workspace.

Starting the PDP-D
^^^^^^^^^^^^^^^^^^ 
For the Amsterdam release, the project directory will look as follows assuming all drools-pdp projects were selected when importing.

    .. image:: RunEcl_drools_pdp_project.png

Right click on policy-management hover over "Run As" and select "Java Application"

    .. image:: RunEcl_run_as.png

Search for "Main" in the pop up and select the Main with the package "org.onap.policy.drools.system" and click "OK".

    .. image:: RunEcl_main.png

The PDP-D will start running; the console will display output.

    .. image:: RunEcl_console_output.png

Interacting with the PDP-D
^^^^^^^^^^^^^^^^^^^^^^^^^^

To interact with the PDP-D, the Telemetry API can be used. A simple GET on the engine will show that the PDP-D is running in Eclipse.

    .. code-block:: bash

        curl -k --silent --user @1b3rt:31nst31n -X GET https://localhost:9696/policy/pdp/engine/ | python -m json.tool

    .. image:: RunEcl_telemetry.png

An HTTP 200 message for the GET request will also appear in the console in Eclipse.

    .. image:: RunEcl_pdpd_200.png


.. seealso:: To create a controller and run a control loop, refer to `Modifying the Release Template  <modAmsterTemplate.html>`_.


End of Document


.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Running+PDP-D+in+Eclipse


