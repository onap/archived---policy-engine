
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

************************
Using guard in the PDP-D 
************************

.. contents::
    :depth: 3

This guide will help configure and test guard connection from PDP-D to PDP-X. This guide assumes that the PDP-D is installed and running policy properly with other properties being set properly.

Configuration
^^^^^^^^^^^^^ 

Prerequisites
-------------

Stop Policy, open, and verify the config:

- Stop policy with *policy stop*
- Open *$POLICY_HOME/config/controlloop.properties.environment*
- Make sure the *sql.db.host*, *sql.db.username* and *sql.db.password* are set correctly


Guard Properties
----------------

**guard.url** - URL endpoint of the PDP-X which will receive the request.
    - For example, *http://pdp:8081/pdp/api/getDecision* will connect to the localhost PDP-X.
    - This request requires some configuration for PDP-X properties below.
    - For testing this URL before running policy, see Verification below.

**guard.jdbc.url** - URL of the database location to which the operations history will be written.
    - For example, *mariadb://mariadb:3306/onap_sdk*.
    - Note that the port is included.
    - Note that at the end, the database name is used.

**guard.disabled** - For enabling / disabling guard functionality.
    - For example, to enable set it to false.
    - When this is set to true, the previous two properties will be ignored.
    - If guard is enabled, then the following PDP-X properties must also be set.


PDP-X Properties
----------------

For testing these properties before running policy, see Verification below.

**pdpx.host** - URL of the PDP-X
    - For example, pdp can be used when PDP-X is on localhost.

**pdpx.username** - User to authenticate

**pdpx.password** - User Password

**pdpx.environment** - Environment making requests
    - For example, TEST

**pdpx.client.username** - Client to authenticate

**pdpx.client.password** - Client password



Verification
^^^^^^^^^^^^ 

It is recommended to test using CLI tools before running since changing bash command parameters are faster than restarting policy.

Logs Verification
-----------------
Checking the logs is straight forward. Check the *$POLICY_HOME/logs/error.log* file for the word "*callRESTfulPDP*" for any exceptions thrown. If they are thrown then there was a problem with the connection.
You can also check the *$POLICY_HOME/logs/network.log* file for the word "*Indeterminate*" which implies the connection failed or got a non 200 response code.

CLI Verification
----------------

It can be helpful to test the PDP-X connection using bash commands to make sure that the PDP-X properties are correct and the guard.url property is correct before running policy.

**Method 1: httpie - CLI, cURL-like tool for humans**
    
    Using the http command we can make a request directly to PDP-X from the command line. Use the following form:

    .. code-block:: bash
    
        http
         POST pdp:8081/pdp/api/getDecision
         Authorization:<yourAuth> ClientAuth:<yourClientAuth>
         Environment:<environment> Content-Type:application/json < guard_request.json
    
    | where:
    |     *<yourAuth>*       is the string generated from user:pass converted to base64 encoding 
    |                        (a conversion tool is available at https://www.base64encode.org/)
    |     *<yourClientAuth>* is generated the same way but from the client user and pass.
    |     *<environment>*    is the context of the request. For example: TEST
    |     *pdp*              is the host of the PDP-X
    

    The guard_request.json should be in the form of the following:
    
    .. code-block:: json
       :caption: guard_request.json
    
        {
          "decisionAttributes": {
                "actor": "APPC",
                "recipe": "Restart",
                "target": "test13",
                "clname" : "piptest"
            },
          "onapName": "PDPD"
        }

    * This request uses Basic Access Authentication.  
    * This request will need further configuration if you are using a proxy.

    
    You know a successful connection is set when a response containing a “PERMIT” or “DENY” in uppercase is returned as follows:
    
    .. code-block:: json
       :caption: Response
    
        {
          "decision": "PERMIT",
          "details": "Decision Permit. OK!"
        }

**Method 2: curl**

    This method does the same as the http command but uses the alternate command of curl. The command should have the following form:

    .. code-block:: bash 
    
        curl -u <user>:<pass> -H "Content-Type: application/json" -H "ClientAuth:<yourClientAuth>" 
             -H "Environment:<environment>" -X POST -d @guard_req.json pdp:8081/pdp/api/getDecision

    * Note that <user> and <pass> are in plain text, while the other headers follow the same form as in Method 1 above.
    * This request will need further configuration if you are using a proxy
    * The response is the same as in Method 1.


**Note on Proxies**

    * JVM system properties should be set if a proxy is being used to make the connection work with policy.
    * The connection may succeed but have response code 401 or 403 with improper proxy authentication, which leads to "Indeterminate"
    * Additionally, the CLI tools have specific proxy configuration. See their respective manual pages for more info.


End of Document

.. SSNote: Wiki page ref.  https://wiki.onap.org/display/DW/Using+guard+in+the+PDP-D
