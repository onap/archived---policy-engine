.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Standalone Quick Start Installation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. contents::
    :depth: 2

The installation of ONAP Policy is **automated** by design and can be done via Docker as a standalone system.  
Various tools, including healthcheck, logs, and Swagger can be used to ensure proper operation.

This article explains how to build the ONAP Policy Framework and get it running in Docker as a standalone system. 
This article assumes that:

* You are using a *\*nix* operating system such as linux or macOS.
* You are using a directory called *git* off your home directory *(~/git)* for your git repositories
* Your local maven repository is in the location *~/.m2/repository*
* You have added settings to access the ONAP Nexus to your M2 configuration, see `Maven Settings Example <https://wiki.onap.org/display/DW/Setting+Up+Your+Development+Environment>`_ (bottom of the linked page)

The procedure documented in this article has been verified to work on a MacBook laptop running macOS Sierra Version 10.12.6 and a HP Z600 desktop running Ubuntu 16.04.3 LTS.

Cloning the ONAP repositories
-----------------------------

Run a script such as the script below to clone the required modules from the `ONAP git repository <https://gerrit.onap.org/r/#/admin/projects/?filter=policy>`_. This script clones the ONAP policy code and also clones some modules that ONAP Policy is dependent on.

ONAP Policy requires all the *policy* modules from the ONAP repository. It also requires the ONAP Parent *oparent* module and the ONAP ECOMP SDK *ecompsdkos* module.


.. code-block:: bash
   :caption: Typical ONAP Policy Framework Clone Script
   :linenos:

    #!/usr/bin/env bash
     
    ## script name for output
    MOD_SCRIPT_NAME=`basename $0`
     
    ## the ONAP clone directory, defaults to "onap"
    clone_dir="onap"
     
    ## the ONAP repos to clone
    onap_repos="\
    policy/parent \
    policy/common \
    policy/docker \
    policy/drools-applications \
    policy/drools-pdp \
    policy/engine \
    policy/apex-pdp \
    policy/distribution"
     
    ##
    ## Help screen and exit condition (i.e. too few arguments)
    ##
    Help()
    {
        echo ""
        echo "$MOD_SCRIPT_NAME - clones all required ONAP git repositories"
        echo ""
        echo "       Usage:  $MOD_SCRIPT_NAME [-options]"
        echo ""
        echo "       Options"
        echo "         -d          - the ONAP clone directory, defaults to '.'"
        echo "         -h          - this help screen"
        echo ""
        exit 255;
    }
     
    ##
    ## read command line
    ##
    while [ $# -gt 0 ]
    do
        case $1 in
            #-d ONAP clone directory
            -d)
                shift
                if [ -z "$1" ]; then
                    echo "$MOD_SCRIPT_NAME: no clone directory"
                    exit 1
                fi
                clone_dir=$1
                shift
            ;;
     
            #-h prints help and exists
            -h)
                Help;exit 0;;
     
            *)    echo "$MOD_SCRIPT_NAME: undefined CLI option - $1"; exit 255;;
        esac
    done
     
    if [ -f "$clone_dir" ]; then
        echo "$MOD_SCRIPT_NAME: requested clone directory '$clone_dir' exists as file"
        exit 2
    fi
    if [ -d "$clone_dir" ]; then
        echo "$MOD_SCRIPT_NAME: requested clone directory '$clone_dir' exists as directory"
        exit 2
    fi
     
    mkdir $clone_dir
    if [ $? != 0 ]
    then
        echo cannot clone ONAP repositories, could not create directory '"'$clone_dir'"'
        exit 3
    fi
     
    for repo in $onap_repos
    do
        repoDir=`dirname "$repo"`
        repoName=`basename "$repo"`
     
        if [ ! -z $dirName ]
        then
            mkdir "$clone_dir/$repoDir"
            if [ $? != 0 ]
            then
                echo cannot clone ONAP repositories, could not create directory '"'$clone_dir/repoDir'"'
                exit 4
            fi
        fi
     
        git clone --depth 1 https://gerrit.onap.org/r/${repo} $clone_dir/$repo
    done
     
    echo ONAP has been cloned into '"'$clone_dir'"'


Execution of the script above results in the following directory hierarchy in your *~/git* directory:

    *  ~/git/onap
    *  ~/git/onap/policy
    *  ~/git/onap/policy/parent
    *  ~/git/onap/policy/common
    *  ~/git/onap/policy/docker
    *  ~/git/onap/policy/drools-applications
    *  ~/git/onap/policy/drools-pdp
    *  ~/git/onap/policy/engine
    *  ~/git/onap/policy/apex-pdp
    *  ~/git/onap/policy/distribution


Building ONAP
-------------

**Step 1:** Optionally, for a completely clean build, remove the ONAP built modules from your local repository.

	.. code-block:: bash 
	
	    rm -fr ~/.m2/repository/org/onap
	    rm -fr ~/.m2/repository/org/openecomp
	    rm -fr ~/.m2/repisotory/com/att


**Step 2:**  A pom such as the one below can be used to build the ONAP Policy Framework modules. Create the *pom.xml* file in the directory *~/git/onap/policy*.

.. code-block:: xml 
   :caption: Typical pom.xml to build the ONAP Policy Framework
   :linenos:

    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
        <groupId>org.onap</groupId>
        <artifactId>onap-policy</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <packaging>pom</packaging>
        <name>${project.artifactId}</name>
        <inceptionYear>2017</inceptionYear>
        <organization>
            <name>ONAP</name>
        </organization>
     
        <modules>
            <module>parent</module>
            <module>common</module>
            <module>drools-pdp</module>
            <module>drools-applications</module>
            <module>engine</module>
            <module>apex-pdp</module>
            <module>distribution</module>
        </modules>
    </project>


**Step 3:** You can now build the ONAP framework

	.. code-block:: bash 

	   cd ~/git/onap
	   mvn clean install 
 

Building the ONAP Policy Framework Docker Images
------------------------------------------------
The instructions here are based on the instructions in the file *~/git/onap/policy/docker/README.md*.


**Step 1:** Build the policy engine docker image:

	.. code-block:: bash 

	    cd ~/git/onap/policy/engine/packages/docker/target
	    docker build -t onap/policy-pe policy-pe


**Step 2:** Build the Drools PDP docker image:

	.. code-block:: bash 

	    cd ~/git/onap/policy/drools-pdp/packages/docker/target
	    docker build -t onap/policy-drools policy-drools


**Step 3:** Build the Policy SDC Distribution docker image:

   .. code-block:: bash 

            cd ~/git/onap/policy/distribution/packages
            mvn clean install -Pdocker
       
**Step 4:** Build the Apex PDP docker image:

   .. code-block:: bash 

            cd ~/git/onap/policy/apex-pdp/packages/apex-pdp-docker/target
            docker build -t onap/policy-apex-pdp policy-apex-pdp


Starting the ONAP Policy Framework Docker Images
------------------------------------------------

In order to run the containers, you can use *docker-compose*. This uses the *docker-compose.yml* yaml file to bring up the ONAP Policy Framework. This file is located in the policy/docker repository.

**Step 1:** Make the file config/drools/drools-tweaks.sh executable.

	.. code-block:: bash 

	    chmod +x config/drools/drools-tweaks.sh


**Step 2:** Set the IP address to use to be an IP address of a suitable interface on your machine. Save the IP address into the file *config/pe/ip_addr.txt*.


**Step 3:** Set the environment variable *MTU* to be a suitable MTU size for the application.

	.. code-block:: bash 

	    export MTU=9126


**Step 4:** Determine if you want policies pre-loaded or not. By default, all the configuration and operational policies will be pre-loaded by the docker compose script. If you do not wish for that to happen, then export this variable:

	.. code-block:: bash 

	    export PRELOAD_POLICIES=false


**Step 5:** Run the system using *docker-compose*. Note that on some systems you may have to run the *docker-compose* command as root or using *sudo*. Note that this command takes a number of minutes to execute on a laptop or desktop computer.

	.. code-block:: bash 

	    docker-compose up


**You now have a full standalone ONAP Policy framework up and running!**


Installation of Drools Controllers and Policies
-----------------------------------------------

You may now install a controller and policies on the ONAP Policy Framework. Follow the HowTos below to install the Amsterdam controller and policies.

    * `Installation of Amsterdam Controller and vCPE Policy <installAmsterController.html>`_



.. _Standalone Quick Start : https://wiki.onap.org/display/DW/ONAP+Policy+Framework%3A+Standalone+Quick+Start



End of Document

