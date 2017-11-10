
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

*************************
PAP Software Architecture
*************************

.. contents::
    :depth: 3

Overview
^^^^^^^^
PAP (Policy Administration Point) is the component of POLICY that is used to administer both Drools and XACML related policies. The core underlying technology used is XACML and the component itself belongs to the XACML architecture family. This component, as the name suggests, manages the configurations for PDP's and is responsible for the Create, Update and Delete operations of policies.

Context
^^^^^^^
The purpose of PAP is to: 

- Manage groups. create, delete or edit a group. By default "*default*" is created at startup.  
- Manage PDP's. PDP's are assigned to single group at any given point of time. They can be changed or even removed. By default, at the time of startup PDP's are registered to the default group. 
- Manage Policies. Create, update and remove policies which are stored in the database.
- Assign Policies to Group. Policies can be added or removed from group(s). 
- Group changes updates all PDP's present in the group. 
- Manage PIP (Policy Information Point) configuration to groups.  
- Provide RESTful API interface for PDP's to communicate with PAP to register and be able to update their properties. 
- Provide RESTful API interface for PDP/ POLICY-SDK-APP (policy GUI) to call the PAP and utilize its services. Clients would either use the GUI or PDP API to communicate with PAP.

PAP Application Software
^^^^^^^^^^^^^^^^^^^^^^^^
- PAP application software can run as a standalone application.  But, to utilize its full features it needs at-least one running PDP instance connected to the PAP. The urls can be defined in the properties file of PDP component.  
- This application can run as single container, or can support multiple instances of PAP's running. Each instance runs the same software. The differences are defined in the properties file used by the PAP software. 
- The software is maintained using this project.
	ONAP-PAP-REST → https://git.onap.org/policy/engine/tree/ONAP-PAP-REST
- Tomcat 8 is used as the web server to host PAP. 
- A PDP update thread is used to update all PDP's upon any change to the group. 
- MariaDB is used as the database behind to store the policies. 
- File system and Properties file are used to cache and store the group information data.
- Elastic search database is used to store the policy data for index and search.

Core components
^^^^^^^^^^^^^^^
- XACMLPapServlet is the core servlet module handling the RESTful calls from PDP and Policy GUI. Apart from the servlet, PAP co hosts spring rest controller to host special API calls to serve Policy GUI. 
- Each policy type is comprised as a different component type and are all under components package. In order to add any new extension to the Policy, it can be done by extending Policy. 
- Spring controllers are present under the controller package which have the controller logic for different dictionaries as well as policy creation controller. 
- Threads are used to update PDP's about any new updates in the group. There are also other threads which are used to monitor the status of PDP's.

Configuration 
^^^^^^^^^^^^^
- All configurations related to the PAP are presesnt in xacml.pap.properties file. This can be changed if required in the docker setup or policy installation setup. 
- pip.properties file is used as the PIP properties file. At the time of writing this document the PIP configuration can be changed via this file and applies for all groups. 

.. seealso:: The XACML PAP implementation mainly references the AT&T XACML https://github.com/att/XACML project.


End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/PAP+Software+Architecture


