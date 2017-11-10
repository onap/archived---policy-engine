
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

***************************
PDP-X Software Architecture
***************************

.. contents::
    :depth: 3

Overview
^^^^^^^^
The PDP (Policy Decision Point) is the main decision engine of POLICY. The decisions taken are based on the policy set which has been assigned by PAP.  **PDP** is a part of **XACML** family and, hence, referred to as **PDP-X**. The PDP-X receives XACML requests and returns XACML responses which are either Permit, Deny or Indeterminate.  The software contains wrapper code to wrap the internal XACML structures with a request and response structure that is commonly used in ONAP.

PDP Application container
^^^^^^^^^^^^^^^^^^^^^^^^^
- PDP Application containers run as standalone containers which when requested for decision give appropriate responses. 
- If configured with PAP it would be able to modify the PDP's policy set. In absence of PAP the PDP would utilize its existing set of policies to take decisions. 
- In order to scale up and handle multiple requests, Multiple PDP's can be started and used to serve the requests. 

Core Software
^^^^^^^^^^^^^
- The core software of PDP-X is the divided into two projects:
    - ONAP-PDP (core PDP decision components) 
    - ONAP-PDP-REST (Rest wrapper around PDP to support communications with PAP and take in requests from clients)
 
- ONAP-PDP-REST is the project which has the wrapper code and hosts the Policy APIs, which also acts as a proxy for Policy administration related APIs. 
- ONAP-PDP-REST project comprises of:
    - Servlet code implementation which handles the requests from PAP's and legacy XACML requests from clients. 
    - Spring REST controller implementation handles the Policy API's that are widely used within ONAP.  Swagger documentation is tied with the API code which are available with the PDP container when executed.
    - Notification server which sends notification to the connected clients via Websocket, DMaaP or UEB. Policy Internal components use Websocket as the notification medium. 
    - Runs a thread to communicate with PAP about any updates in policy configurations.
 
- ONAP-PDP project is an extension of XACML implementation of PDP which contains the core XACML function and definition which are used in the policy decision process. 
- Tomcat 8 is used as the web server to host PDP-X. 
- File system and properties file are used to store policy information so that PDP can recover in case of failure. 
- In memory cache is used by PDP to store policy information after startup in order to serve requests quickly. 

Package Overview
^^^^^^^^^^^^^^^^
- ONAP-PDP-REST package structure is discussed here:
    - rest is the main package which contains servlet code. 
    - controller package consists of the spring rest controller which handles the API requests from clients. 
    - service package consists of the service layer code for different policy API services. 
    - notifications package consists of the notification service that is offered by PDP-X. 
    - config package consists of the spring and swagger configurations.
 
- Extensions
    - Any new addition to the service can be added to the service layer and appropriate API call can be added to the controller code. 

Configuration
^^^^^^^^^^^^^
- All configurations related to PDP are present in xacml.pdp.properties file. This can be changed if required in the docker setup or policy installation setup.


.. seealso:: The PDP implementation references the XACML AT&T https://github.com/att/XACML project. 


End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/PDP-X+Software+Architecture


