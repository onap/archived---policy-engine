
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

***********************************************************************************************
Tutorial: Generating and Testing your own Control Loop Operational Policy in a standalone PDP-D
***********************************************************************************************

.. contents::
    :depth: 3

To generate your own control loop operational policy, use the *create-cl-amsterdam* tool.  The *create-cl-amsterdam* script is located in *${POLICY_HOME}/bin (/opt/app/policy/bin)*.  When the script is run, it will ask for values for a variety of fields.  The fields will have pre-filled out defaults, and for the most part, the defaults are fine to leave in.  The two main fields that should be changed are the Template Control Loop Name and the Control Loop Yaml.

    .. image:: Tut_cl_valuesHighlight.png

Make sure the Yaml’s controlLoopName matches the Template Control Loop Name you pass in. Finally, confirm that the parameters are correct, confirm the directory it will add the policy files in (default is /tmp) and tell the script to create the maven artifact.

    *Confirm the parameters and enter the directory to install in as shown below:*

    .. image:: Tut_cl_confirmAndDirectory.PNG

    *Choose whether to immediately deploy (in this case the directory is /tmp/amsterdam)*

    .. image:: Tut_cl_preDeploy.PNG

When the processing is done, you get the choice of immediately deploying the policy to the local repository, or first examining the rules in the directory it tells you.  If you don’t immediately deploy, you need to use the “*mvn install*” command in the newly created directory to continue.  When all that is done, go to the directory where the rule was placed (the /tmp/amsterdam directory in this case) and copy the *<name>-controller.properties* file to *${POLICY_HOME}/config*.  Turn the engine off and then back on with “*policy stop*” and then “*policy start*”.

    *Location of the properties file*

    .. image:: Tut_cl_propFile.PNG

    *Moving the properties file to ${POLICY_HOME}/config*

    .. image:: Tut_cl_finalStep.PNG

Proceed with testing your new policy as described in the specific tutorials:

• vCPE - `Tutorial: Testing the vCPE use case in a standalone PDP-D <tutorial_vCPE.html>`_
• vDNS - `Tutorial: Testing the vDNS Use Case in a standalone PDP-D <tutorial_vDNS.html>`_
• vFW - `Tutorial: Testing the vFW flow in a standalone PDP-D <tutorial_vFW.html>`_
• VoLTE - `Tutorial: Testing the VOLTE Use Case in a standalone PDP-D <tutorial_VOLTE.html>`_


.. seealso:: To deploy a control loop in Eclipse from the control loop archetype template, refer to `Modifying the Release Template  <modAmsterTemplate.html>`_.


End of Document


.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Tutorial%3A+Generating+and+Testing+your+own+Control+Loop+Operational+Policy+in+a+standalone+PDP-D

