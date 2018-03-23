
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

******************************
Modifying the Release Template
******************************

.. contents::
    :depth: 3


This tutorial is intended for Policy Drools Applications developers who would like to test their code using the PDP-D in Eclipse instead of installing in a lab. The example for this tutorial will walk through making a change to the Amsterdam Control Loop Template, building the archetype project, and instantiating a controller with the PDP-D.

Installing the Archetype Project in Eclipse
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 

**STEP 1:** First, open the drools-pdp project in Eclipse and navigate to File → New → Project...

    .. image:: mat_new_project.JPG

**STEP 2:** Navigate to Maven → Maven Project

    .. image:: mat_maven_project.JPG

**STEP 3:** For demo purposes, the location of the project will be put in Documents/demo. Hit next to proceed.

    .. image:: mat_project_location.JPG

**STEP 4:** Click  "Configure" near the top right.

    .. image:: mat_configure.JPG

**STEP 5:** Click "Add Remote Catalog..."

    .. image:: mat_add_local_catalog.JPG

**STEP 6:** Add the ONAP Staging repository archetype-catalog.xml with a description if desired. Click "OK" then "Apply", then "OK".

    .. image:: mat_nexus_catalog.JPG

**STEP 7:** The ONAP staging archetypes are now an option:

    .. image:: mat_archetypes.JPG

**STEP 8:** Highlight the option with the Artifact Id "archetype-cl-amsterdam" and hit next. The following screen allows the user to modify some of the configurable parameters of the control loop. For this demo the default parameters that are already populated will be used. Fill out the Groud Id, Artifact Id, Version, and Package and hit "Finish". For the demo the following is used:

    .. image:: mat_archetype_params.JPG

**STEP 9:** Depending on the IDE in use, an error may be generated about handling the kie-maven-plugin:6.5.0.Final:build plugin. This can be ignored, click "Finish" and then "OK" if a warning pops up about having build errors.

    .. image:: mat_error.JPG

**STEP 10:** Amsterdam can now be seen in the Project Explorer:

    .. image:: mat_amsterdam_project.JPG


Modifying the Amsterdam Template
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 

**STEP 1:** Expand the amsterdam project and "src/main/resources". The drl generated from the archetype will be present as follows:

    .. image:: mat_amsterdam_drl.JPG

**STEP 2:** Now a change will be added to the drl from above. For this tutorial, a new logging statement will be added that will show in the console of Eclipse when the changes are tested. We'll add this in the SETUP rule.

    .. image:: mat_hello_world.JPG

**STEP 3:** Right click on the Amsterdam project, hover over "Run As", then click "Maven build".

    .. image:: mat_maven_build.JPG

**STEP 4:** For the goals type "clean install", click "Apply" and then click "Run".

    .. image:: mat_clean_install.JPG

    .. image:: mat_build_success.JPG

Running the PDP-D with the Amsterdam Controller
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 

**STEP 1:** Copy the controller properties file that was generated from the archetype in amsterdam/src/main/config into policy-management/src/main/config

    .. image:: mat_amsterdam_controller.JPG

**STEP 2:** Go src/main/java and expand the package "org.onap.policy.drools.system". Right click on "Main.java", then hover over "Run As..." and click "Java Application".

    .. image:: mat_run_as.JPG

**STEP 3:** Search through the console for the logging statement "\***** HELLO WORLD \*****". This indicates that the template change worked. Modifications can continue to be made and the Telemetry API can be used to interact with the PDP-D that is running in Eclipse and to test control loop flows.

    .. image:: mat_console_output.JPG



End of Document


.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Modifying+the+Amsterdam+release+template


