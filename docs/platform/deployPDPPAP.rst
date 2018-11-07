
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

**********************************************
Testing, Deploying and Debugging the PDP-X/PAP
**********************************************

.. contents::
    :depth: 3

PAP (Policy Administration Point)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 

Accessing and Starting PAP
--------------------------

- To access the PAP docker use 

    .. code-block:: bash

        docker exec -it -u 0 pap su - policy

- All Policy related software are installed under the policy account, the policy root directory is under *${POLICY_HOME}* environment variable and it may be changed on a per installation basis. It is typically set up under the */opt/app/policy* directory but can be changed during installation. All Policy software runs with non-root privileges as *policy* is a regular user account. 

- Once within the PAP Container the running status can be checked using the following policy status command.

    .. code-block:: bash

        policy.sh [--debug] status|start|stop

- To get the current status of Policy use *policy.sh status*

    .. code-block:: bash

        policy@pap:~$ policy.sh status
            pap: UP: running with pid 2114
            console: UP: running with pid 2135
            paplp: UP: running with pid 2155
            3 cron jobs installed.

- To Stop the components use *policy.sh stop*

    .. code-block:: bash
    
        policy@pap:~$ policy.sh stop
            paplp: STOPPING ..
            console: STOPPING ..
            pap: STOPPING ..

- To Start use *policy.sh start* 

    .. code-block:: bash
    
        policy@pap:~$ policy.sh start
            pap: STARTING ..
            console: STARTING ..
            paplp: STARTING ..

Healthcheck
-----------

- The Healthcheck feature resides in the PDP-D that proxies for PAP, PDP-X, etc. To perform the Healthcheck on policy components you can follow the generic procedure documented as below, these commands must be executed within the PDP-D container.

    .. code-block:: bash
    
        # Assuming the healthcheck service credentials have not been changed
        # post-installation within the drools container
    
        source /opt/app/policy/config/feature-healthcheck.conf.environment
    
        curl -k --silent --user "${HEALTHCHECK_USER}:${HEALTHCHECK_PASSWORD}" 
                       -X GET https://localhost:6969/healthcheck | python -m json.tool

- Additional information can be found in the documentation for Testing, Deploying, and debugging on a PDP-D Healthcheck. 

Logs
----

- The main application logs for PAP are located at */var/log/onap/policy/pap/* location. The catalina.out can be found at *$POLICY_HOME/servers/pap/logs/* location.   

* Policy PAP uses EELF logging framework for logging and if needed to be modified can be modified at *$POLICY_HOME/servers/pap/webapps/pap/WEB-INF/classes/logback.xml*.  This change needs a restart of the PAP component in order to be in effect.  

- The logs are divided into separate files and debug logs can be found in *debug.log* and error logs in *error.log* file which are two different files under application logs directory.   

PDP-X (XACML Policy Decision Point)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 

Accessing and Starting PDP
--------------------------

- To access the PDP-X docker : 

    .. code-block:: bash

        docker exec -it -u 0 pdp su - policy

- To start and stop the PDP-X components the same procedure can be followed as documented for PAP. 

    .. code-block:: bash

        policy.sh [--debug] status|start|stop


Swagger UI Testing (Only for HEAT installations)
------------------------------------------------

- The PDP-X provides the swagger UI from which PDP-X REST APIs can be tested and used, this also lets us know the PDP Status. 
- In order to access PDP's swagger UI visit ``https://{PDP_URL}:8081/pdp/swagger-ui.html.``
- NOTE: This feature is only available for HEAT installation as it is disabled in OOM installations. 

Logs
----

- The main application logs for PDP are located at */var/log/onap/policy/pdpx/* location. The catalina.out can be found at *$POLICY_HOME/servers/pdp/logs/* location.   

* Policy PDP uses EELF logging framework for logging and if needed to be modified can be modified at *$POLICY_HOME/servers/pap/webapps/pdp/WEB-INF/classes/logback.xml*.  This change needs a restart of the PDP component in order to be in effect.  

- The logs are divided into separate files and debug logs can be found in *debug.log* and error logs in *error.log* file which are two different files under application logs directory.   


BRMSGW (Business Rule Management System Gateway)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 

Accessing and Starting BRMSGW
-----------------------------

- To access the BRMSGW docker : 

    .. code-block:: bash

        docker exec -it -u 0 brmsgw su - policy

- To start and stop the BRMSGW components the same procedure can be followed as documented for PAP and PDP-X. 

    .. code-block:: bash

        policy.sh [--debug] status|start|stop

Logs
----

- The main application logs for BRMSGW are located at */var/log/onap/policy/policyapi/* location. The logs are divided into separate files; debug logs can be found in *debug.log* and error logs in *error.log* file which are two different files under application logs directory.   

* The brmsgw.log can be found at */var/log/onap/policy/brmsgw* location.   


End of Document

.. SSNote: Wiki page ref.  https://wiki.onap.org/pages/viewpage.action?pageId=16003633

