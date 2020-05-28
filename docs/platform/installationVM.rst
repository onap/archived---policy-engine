.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Standalone Installation in Virtual Machine 
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. contents::
    :depth: 2

Overview
---------

This document explains the steps required to install ONAP policy framework in a standalone virtual machine. The following steps are explained in detail with required commands.
	* Install policy framework on a Virtual Machine using the integration script.
	* Install the Beijing controller.
	* Install and Push Beijing policy for vCPE, vFirewall, vDNS & VOLTE use cases.
	* Verify the use cases manually.

.. note:: 
	* Either use sudo access to run the commands or login through root user.
	* These steps have been verified for a VM with Ubuntu OS.

**Follow the steps below to install policy framework in a virtual machine using the integration script.**

Docker Installation
-------------------

**Step 1:** Make the etc/hosts entries

	.. code-block:: bash 
	
	    echo $(hostname -I | cut -d\  -f1) $(hostname) | sudo tee -a /etc/hosts

**Step 2:** Make the DNS entries

	.. code-block:: bash 
	
	    echo "nameserver <PrimaryDNSIPIP>" >> /etc/resolvconf/resolv.conf.d/head
	    echo "nameserver <SecondaryDNSIP>" >> /etc/resolvconf/resolv.conf.d/head
	    resolvconf -u

**Step 3:** Update the ubuntu software installer

	.. code-block:: bash 
	
	    apt-get update

**Step 4:** Check and Install Java

	.. code-block:: bash 
	
	    apt-get install -y openjdk-8-jdk
	    java -version

	Ensure that the Java version that is executing is *OpenJDK version 8*

**Step 5:** Check and Install GIT

	.. code-block:: bash 
	
	    apt-get install git 
	    git -version

**Step 6:** Check and Install Maven

	.. code-block:: bash 
	
	    apt-get install maven
	    mvn -version

**Step 7:** Check and Install docker and docker-compose

	.. code-block:: bash 
	
	    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
	    add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
	    apt-get update
	    apt-cache policy docker-ce
	    apt-get install -y docker-ce
	    systemctl status docker
	    docker ps
	    apt install docker-compose
	    docker-compose

**Step 8:** Check the MTU size of the eth0 interface on your VM, it is 9126 in the example below

	.. code-block:: bash 
	
	    ifconfig
	    eth0      Link encap:Ethernet  HWaddr 02:42:ac:12:00:07  
	              inet addr:172.18.0.7  Bcast:0.0.0.0  Mask:255.255.0.0
	              inet6 addr: fe80::42:acff:fe12:7/64 Scope:Link
	              UP BROADCAST RUNNING MULTICAST  MTU:9126  Metric:1
	              RX packets:44955 errors:0 dropped:0 overruns:0 frame:0
	              TX packets:75017 errors:0 dropped:0 overruns:0 carrier:0
	              collisions:0 txqueuelen:0 
	              RX bytes:8712526 (8.7 MB)  TX bytes:7079733 (7.0 MB)

**Step 9:** Configure the DNS settings in the Docker daemon configuration file /etc/docker/daemon.json

	* add or edit the first line below for your DNS settings *<PrimaryDNSIP>* and *<SecondaryDNSIP>*
	* add or edit the second line below for your MTU size *<MTUSize>*, taken from the command in step 8 above

	.. code-block:: bash 
	
	    add "dns": ["<PrimaryDNSIP>", "<SecondaryDNSIP>"]
	    add "mtu": <MTUSize>

**Step 10:** Restart the docker service

	.. code-block:: bash 
	
	    service docker restart

**Step 11:** Change the permissions of the Docker Daemon configuration file

	.. code-block:: bash 
	
	    chmod 565 /etc/docker/daemon.json

**Step 12:** Configure the DNS settings in the Docker configuration file */etc/default/docker* 

	* add or edit the line below for your DNS settings *<PrimaryDNSIP>* and *<SecondaryDNSIP>* 

	.. code-block:: bash 
	
	    add DOCKER_OPTS="--dns <PrimaryDNSIP> --dns <SecondaryDNSIP>"

**Step 13:** Change the permissions of the Docker socket file

	.. code-block:: bash 
	
	    chmod 565 /var/run/docker.sock

**Step 14:** Check the status of the Docker service and ensure it is running correctly

	.. code-block:: bash 
	
	    service docker status
	    docker ps

Install the ONAP Policy Framework
---------------------------------

**Step 1:** Clone the integration git repository

	.. code-block:: bash 
	
	    git clone --depth 1 https://gerrit.onap.org/r/integration

**Step 2:** Change to the policy integration script location

	.. code-block:: bash 
	
	    cd integration/test/csit/scripts/policy/

**Step 3:** Edit the Policy integration script script1.sh

	.. code-block:: bash 
	
	    # - set the MTU value <MTUValue> to the same value as read in step 8 above
	    # - set the value of PRELOAD_POLICIES to true
	    # - change the name of the docker compose Yaml file to remove the -integration part of the file name from
	    #       "docker-compose -f docker-compose-integration.yml up -d"
	    #   to  
	    #       "docker-compose -f docker-compose.yml up -d"

	    export MTU=<MTUValue>
	    export PRELOAD_POLICIES=true
	    docker-compose -f docker-compose.yml up -d

**Step 4:** Run the integration script for automated installation

	.. code-block:: bash 
	
	    ./script1.sh | tee /tmp/log.txt

	Note: It may take up to 60 minutes for the installation to complete. You can view installation logs in /tmp/log.txt


**Step 5:** Verify the installation

	.. code-block:: bash 
	
	    echo $(hostname -I | cut -d\  -f1) $(hostname) | sudo tee -a /etc/hosts
	    docker ps
	    
	    CONTAINER ID        IMAGE                      COMMAND                  CREATED             STATUS              PORTS                                            NAMES
	    2d04434c5354        onap/policy-drools         "/bin/sh -c ./do-sta…"   2 days ago          Up 2 days           0.0.0.0:6969->6969/tcp, 0.0.0.0:9696->9696/tcp   drools
	    52a910a3678b        onap/policy-pe             "bash ./do-start.sh …"   2 days ago          Up 2 days           0.0.0.0:8081->8081/tcp                           pdp
	    aa9bb20efe59        onap/policy-pe             "bash ./do-start.sh …"   2 days ago          Up 2 days                                                            brmsgw
	    7cdf4919044b        onap/policy-pe             "bash ./do-start.sh …"   2 days ago          Up 2 days           0.0.0.0:8443->8443/tcp, 0.0.0.0:9091->9091/tcp   pap
	    394854eab2bc        sonatype/nexus:2.14.8-01   "/bin/sh -c '${JAVA_…"   2 days ago          Up 2 days           0.0.0.0:9081->8081/tcp                           nexus
	    fd48c851b6be        mariadb:10.0.34            "docker-entrypoint.s…"   2 days ago          Up 2 days           0.0.0.0:3306->3306/tcp                           mariadb


Installation of Controllers and Policies
----------------------------------------

You may now install a controller and policies on the ONAP Policy Framework. Follow either of the HowTos below to install either the Amsterdam or Beijing controller and policies.

    * `Installation of Amsterdam Controller and vCPE Policy <installAmsterController.html>`_
    * `Installation of Beijing Controller and Policies <installBeijingController.html>`_


Useful Commands
---------------

The following command returns a JSON document containing the configuration information from the PDP.

.. code-block:: bash 
   :caption: To return a JSON document containing the configuration information from the PDP
	
    curl -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'ClientAuth: cHl0aG9uOnRlc3Q=' -H 'Authorization: Basic dGVzdHBkcDphbHBoYTEyMw==' -H 'Environment: TEST' -X POST -d '{"policyName": ".*"}' http://localhost:8081/pdp/api/getConfig | python -m json.tool


Run following command to check PDP, PAP and PDP-D Health status

.. code-block:: bash 
   :caption: To check PDP, PAP and PDP-D Health status
	
    http -a 'healthcheck:zb!XztG34' :6969/healthcheck 
     
    HTTP/1.1 200 OK
    Content-Length: 276
    Content-Type: application/json
    Date: Tue, 17 Apr 2018 10:51:14 GMT
    Server: Jetty(9.3.20.v20170531)
    {  
       "details":[  
          {  
             "code":200,
             "healthy":true,
             "message":"alive",
             "name":"PDP-D",
             "url":"self"
          },
          {  
             "code":200,
             "healthy":true,
             "message":"",
             "name":"PAP",
             "url":"http://pap:9091/pap/test"
          },
          {  
             "code":200,
             "healthy":true,
             "message":"",
             "name":"PDP",
             "url":"http://pdp:8081/pdp/test"
          }
       ],
       "healthy":true
    }

Run following command to make sure all topics are created

.. code-block:: bash 
   :caption: To check all topics are created
	
    curl --silent --user @1b3rt:31nst31n -X GET http://localhost:9696/policy/pdp/engine/topics/sources | python -m json.tool


.. _Standalone installation in Virtual Machine: https://wiki.onap.org/display/DW/ONAP+Policy+Framework%3A+Standalone+installation+in+Virtual+Machine



End of Document

