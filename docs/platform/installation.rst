.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Installation
------------

.. contents::
    :depth: 3

The installation of ONAP Policy is **automated** by design and can be done via Docker as a standalone system.  
Various tools, including healthcheck, logs, and Swagger can be used to ensure proper operation.

ONAP Policy Framework: Standalone Quick Start
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
This procedure explains how build the ONAP Policy Framework and get it running in Docker as a standalone system. 
This procedure assumes that:

* You are using a *\*nix* operating system such as linux or macOS.
* You are using a directory called *git* off your home directory *(~/git)* for your git repositories
* Your local maven repository is in the location *~/.m2/repository*

The procedure documented here has been verified to work on a MacBook laptop running macOS Sierra Version 10.12.6 and a HP Z600 desktop running Ubuntu 16.04.3 LTS.


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
    oparent \
    ecompsdkos \
    policy/api \
    policy/common \
    policy/docker \
    policy/drools-applications \
    policy/drools-pdp \
    policy/engine \
    policy/gui \
    policy/pap \
    policy/pdp"
    
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
    
        git clone https://gerrit.onap.org/r/${repo} $clone_dir/$repo
    done
    
    echo ONAP has been cloned into '"'$clone_dir'"'

Execution of the script above results in the following directory hierarchy in your *~/git* directory:

    *  ~/git/onap
    *  ~/git/onap/ecompsdkos
    *  ~/git/onap/oparent
    *  ~/git/onap/policy
    *  ~/git/onap/policy/api
    *  ~/git/onap/policy/common
    *  ~/git/onap/policy/docker
    *  ~/git/onap/policy/drools-applications
    *  ~/git/onap/policy/drools-pdp
    *  ~/git/onap/policy/engine
    *  ~/git/onap/policy/gui
    *  ~/git/onap/policy/pap
    *  ~/git/onap/policy/pdp    



Building ONAP
^^^^^^^^^^^^^

**Step 1.** Optionally, for a completely clean build, remove the ONAP built modules from your local repository.

    * rm -fr ~/.m2/repository/org/onap
    * rm -fr ~/.m2/repository/org/openecomp


**Step 2**. A pom such as the one below can be used to build all the ONAP policy modules and their dependencies. Create the *pom.xml* file in the directory *~/git/onap*.

.. code-block:: xml 
   :caption: Typical pom.xml to build the ONAP Policy Framework
   :linenos:

    <project xmlns="http://maven.apache.org/POM/4.0.0" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

      <modelVersion>4.0.0</modelVersion>
      <groupId>org.onap</groupId>
      <artifactId>onap-policy_standalone</artifactId>
      <version>1.0.0-SNAPSHOT</version>
      <packaging>pom</packaging>
      <name>${project.artifactId}</name>
      <inceptionYear>2017</inceptionYear>
      <organization>
        <name>ONAP</name>
      </organization>
    
      <profiles>
        <profile>
          <id>policy-dependencies</id>
          <activation>
        <property>
              <name>policyDeps</name>
        </property>
          </activation>
          <modules>
        <module>oparent</module>
        <module>ecompsdkos/ecomp-sdk</module>
          </modules>
        </profile>
        <profile>
          <id>policy</id>
          <activation>
        <activeByDefault>true</activeByDefault>
          </activation>
          <modules>
        <module>oparent</module>
        <module>ecompsdkos/ecomp-sdk</module>
        <module>policy</module>
          </modules>
        </profile>
      </profiles>
    </project>
    


**Step 3**.  A pom such as the one below can be used to build the ONAP Policy Framework modules. Create the *pom.xml* file in the directory *~/git/onap/policy*

.. code-block:: xml 
   :caption: Typical pom.xml to build the ONAP Policy Framework Policy Modules
   :linenos:

    <project xmlns="http://maven.apache.org/POM/4.0.0" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

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
            <module>common</module>
            <module>engine</module>
            <module>pdp</module>
            <module>pap</module>
            <module>drools-pdp</module>
            <module>drools-applications</module>
            <module>api</module>
            <module>gui</module>
            <module>docker</module>
        </modules>
    </project>

**Step 4**. The build cannot currently find the *org.onap.oparent:version-check-maven-plugin* plugin so, for now, comment that plugin out in the POMs *policy/drools-pdp/pom.xml* and *policy/drools-applications/pom.xml*.

**Step 5**. Build the ONAP dependencies that are required for the ONAP policy framework and which must be built first to be available to the ONAP Policy Framework proper.

    * cd ~/git/onap
    * mvn clean install -DpolicyDeps 

**Step 6**. You can now build the ONAP framework

   *  On *Ubuntu*, just build the Policy Framework tests and all

        - cd ~/git/onap
        - mvn clean install 

   *  On *macOS*, you must build build the ONAP framework with tests turned off first. Then rebuild the framework with tests turned on and all tests will pass. Note: The reason for this behaviour will be explored later. 
    
        - cd ~/git/onap
        - mvn clean install -DskipTests
        - mvn install
 

Building the ONAP Policy Framework Docker Images
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
The instructions here are based on the instructions in the file *~/git/onap/policy/docker/README*.

**Step 1.** Prepare the Docker packages. This will pull the installation zip files needed for building the policy-pe and policy-drools Docker images into the target directory. It will not actually build the docker images; the additional steps below must be followed to actually build the Docker images.

    * cd ~/git/onap/policy/docker
    * mvn prepare-package

**Step 2**. Copy the files under *policy-pe* to *target/policy-pe*.

    * cp policy-pe/* target/policy-pe

**Step 3**. Copy the files under *policy-drools* to *target/policy-drools*.

    * cp policy-drools/* target/policy-drools

**Step 4**. Run the *docker build* command on the following directories in the order below. 
Note that on some systems you may have to run the *docker* command as root or using *sudo*.

    * docker build -t onap/policy/policy-os     policy-os
    * docker build -t onap/policy/policy-db     policy-db
    * docker build -t onap/policy/policy-nexus  policy-nexus
    * docker build -t onap/policy/policy-base   policy-base
    * docker build -t onap/policy/policy-pe     target/policy-pe
    * docker build -t onap/policy/policy-drools target/policy-drools

Starting the ONAP Policy Framework Docker Images
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
In order to run the containers, you can use *docker-compose*. This uses the *docker-compose.yml* yaml file to bring up the ONAP Policy Framework.

**Step 1.** Make the file *config/drools/drools-tweaks.sh* executable

    * chmod +x config/drools/drools-tweaks.sh

**Step 2**. Set the IP address to use to be an IP address of a suitable interface on your machine. Save the IP address into the file *config/pe/ip_addr.txt*.

**Step 3**. Set the environment variable *MTU* to be a suitable MTU size for the application.

    * export MTU=9126

**Step 4**. Run the system using *docker-compose*. Note that on some systems you may have to run the *docker-compose* command as root or using *sudo*. Note that this command takes a number of minutes to execute on a laptop or desktop computer.

    * docker-compose up


Installation Complete
^^^^^^^^^^^^^^^^^^^^^

**You now have a full standalone ONAP Policy framework up and running!**


.. _Standalone Quick Start : https://wiki.onap.org/display/DW/ONAP+Policy+Framework%3A+Standalone+Quick+Start



End of Document

