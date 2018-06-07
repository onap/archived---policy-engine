
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

*************************
Feature: State Management 
*************************

.. contents::
    :depth: 2

Summary
^^^^^^^
The State Management Feature provides:

- Node-level health monitoring
- Monitoring the health of dependency nodes - nodes on which a particular node is dependent
- Ability to lock/unlock a node and suspend or resume all application processing
- Ability to suspend application processing on a node that is disabled or in a standby state
- Interworking/Coordination of state values
- Support for ITU X.731 states and state transitions for:
        - Administrative State
        - Operational State
        - Availability Status
        - Standby Status

Usage
^^^^^

Enabling and Disabling Feature State Management
-----------------------------------------------

The State Management Feature is enabled from the command line when logged in as policy after configuring the feature properties file (see Description Details section).  From the command line:

- > features status - Lists the status of features
- > features enable state-management - Enables the State Management Feature
- > features disable state-management - Disables the State Management Feature

The Drools PDP must be stopped prior to enabling/disabling features and then restarted after the features have been enabled/disabled.

    .. code-block:: bash
       :caption: Enabling State Management Feature

        policy@hyperion-4:/opt/app/policy$ policy stop
        [drools-pdp-controllers]
         L []: Stopping Policy Management... Policy Management (pid=354) is stopping... Policy Management has stopped.
        policy@hyperion-4:/opt/app/policy$ features enable state-management
        name                      version         status
        ----                      -------         ------
        controlloop-utils         1.1.0-SNAPSHOT  disabled
        healthcheck               1.1.0-SNAPSHOT  disabled
        test-transaction          1.1.0-SNAPSHOT  disabled
        eelf                      1.1.0-SNAPSHOT  disabled
        state-management          1.1.0-SNAPSHOT  enabled
        active-standby-management 1.1.0-SNAPSHOT  disabled
        session-persistence       1.1.0-SNAPSHOT  disabled

Description Details
^^^^^^^^^^^^^^^^^^^

State Model
-----------

The state model follows the ITU X.731 standard for state management.  The supported state values are:
    **Administrative State:**
        - Locked - All application transaction processing is prohibited
        - Unlocked - Application transaction processing is allowed
    
    **Administrative State Transitions:**
        - The transition from Unlocked to Locked state is triggered with a Lock operation
        - The transition from the Locked to Unlocked state is triggered with an Unlock operation

    **Operational State:**
        - Enabled - The node is healthy and able to process application transactions
        - Disabled - The node is not healthy and not able to process application transactions    

    **Operational State Transitions:**
        - The transition from Enabled to Disabled is triggered with a disableFailed or disableDependency operation
        - The transition from Disabled to Enabled is triggered with an enableNotFailed and enableNoDependency operation
    
    **Availability Status:**
        - Null - The Operational State is Enabled
        - Failed - The Operational State is Disabled because the node is no longer healthy
        - Dependency - The Operational State is Disabled because all members of a dependency group are disabled
        - Dependency.Failed - The Operational State is Disabled because the node is no longer healthy and all members of a dependency group are disabled
    
    **Availability Status Transitions:**
        - The transition from Null to Failed is triggered with a disableFailed operation
        - The transtion from Null to Dependency is triggered with a disableDependency operation
        - The transition from Failed to Dependency.Failed is triggered with a disableDependency operation
        - The transition from Dependency to Dependency.Failed is triggered with a disableFailed operation
        - The transition from Dependency.Failed to Failed is triggered with an enableNoDependency operation
        - The transition from Dependency.Failed to Dependency is triggered with an enableNotFailed operation
        - The transition from Failed to Null is triggered with an enableNotFailed operation
        - The transition from Dependency to Null is triggered with an enableNoDependency operation
    
    **Standby Status:**
        - Null - The node does not support active-standby behavior
        - ProvidingService - The node is actively providing application transaction service
        - HotStandby - The node is capable of providing application transaction service, but is currently waiting to be promoted
        - ColdStandby - The node is not capable of providing application service because of a failure
    
    **Standby Status Transitions:**
        - The transition from Null to HotStandby is triggered by a demote operation when the Operational State is Enabled
        - The transition for Null to ColdStandby is triggered is a demote operation when the Operational State is Disabled
        - The transition from ColdStandby to HotStandby is triggered by a transition of the Operational State from Disabled to Enabled
        - The transition from HotStandby to ColdStandby is triggered by a transition of the Operational State from Enabled to Disabled
        - The transition from ProvidingService to ColdStandby is triggered by a transition of the Operational State from Enabled to Disabled
        - The transition from HotStandby to ProvidingService is triggered by a Promote operation
        - The transition from ProvidingService to HotStandby is triggered by a Demote operation

Database
--------

The State Management feature creates a StateManagement database having three tables:

    **StateManagementEntity** - This table has the following columns:
        - **id** - Automatically created unique identifier
        - **resourceName** - The unique identifier for a node
        - **adminState** - The Administrative State
        - **opState** - The Operational State
        - **availStatus** - The Availability Status
        - **standbyStatus** - The Standby Status
        - **created_Date** - The timestamp the resource entry was created
        - **modifiedDate** - The timestamp the resource entry was last modified

    **ForwardProgressEntity** - This table has the following columns:
        - **forwardProgressId** - Automatically created unique identifier
        - **resourceName** - The unique identifier for a node
        - **fpc_count** - A forward progress counter which is periodically incremented if the node is healthy
        - **created_date** - The timestamp the resource entry was created
        - **last_updated** - The timestamp the resource entry was last updated
    
    **ResourceRegistrationEntity** - This table has the following columns:
        - **ResourceRegistrationId** - Automatically created unique identifier
        - **resourceName** - The unique identifier for a node
        - **resourceUrl** - The JMX URL used to check the health of a node
        - **site** - The name of the site in which the resource resides
        - **nodeType** - The type of the node (i.e, pdp_xacml, pdp_drools, pap, pap_admin, logparser, brms_gateway, astra_gateway, elk_server, pypdp)
        - **created_date** - The timestamp the resource entry was created
        - **last_updated** - The timestamp the resource entry was last updated

Node Health Monitoring
----------------------

**Application Monitoring**
    
    Application monitoring can be implemented using the *startTransaction()* and *endTransaction()* methods.  Whenever a transaction is started, the *startTransaction()* method is called.  If the node is locked, disabled or in a hot/cold standby state, the method will throw an exception.  Otherwise, it resets the timer which triggers the default *testTransaction()* method. 
    
    When a transaction completes, calling *endTransaction()* increments the forward process counter in the *ForwardProgressEntity* DB table.  As long as this counter is updating, the integrity monitor will assume the node is healthy/sane.
    
    If the *startTransaction()* method is not called within a provisioned period of time, a timer will expire which calls the *testTransaction()* method.  The default implementation of this method simply increments the forward progress counter.  The *testTransaction()* method may be overwritten to perform a more meaningful test of system sanity, if desired.
    
    If the forward progress counter stops incrementing, the integrity monitoring routine will assume the node application has lost sanity and it will trigger a *statechange* (disableFailed) to cause the operational state to become disabled and the availability status attribute to become failed.  Once the forward progress counter again begins incrementing, the operational state will return to enabled.

**Application Monitoring with AllSeemsWell**

    The IntegrityMonitor class provides a facility for applications to directly control updates of the forwardprogressentity table.  As previously described, *startTransaction()* and *endTransaction()* are provided to monitor the forward progress of transactions.  This, however, does not monitor things such as internal threads that may be blocked or die.  An example is the feature-state-management *DroolsPdpElectionHandler.run()* method. 

    The *run()* method is monitored by a timer task, *checkWaitTimer()*.  If the *run()* method is stalled an extended period of time, the *checkWaitTimer()* method will call *StateManagementFeature.allSeemsWell(<className>, <AllSeemsWell State>, <String message>)* with the AllSeemsWell state of Boolean.FALSE.

    The IntegrityMonitor instance owned by StateManagementFeature will then store an entry in the allSeemsWellMap and block updates of the forwardprogressentity table.  This in turn, will cause the Drools PDP operational state to be set to “disabled” and availability status to be set to “failed”.  

    Once the blocking condition is cleared, the *checkWaiTimer()* will again call the *allSeemsWell()* method and include an AllSeemsWell state of Boolean.True. This will cause the IntegrityMonitor to remove the entry for that className from the allSeemsWellMap and allow updating of the forwardprogressentity table, so long as there are no other entries in the map.

**Dependency Monitoring**

    When a Drools PDP (or other node using the *IntegrityMonitor* policy/common module) is dependent upon other nodes to perform its function, those other nodes can be defined as dependencies in the properties file. In order for the dependency algorithm to function, the other nodes must also be running the *IntegrityMonitor*.  Periodically the Drools PDP will check the state of dependencies.  If all of a node type have failed, the Drools PDP will declare that it can no longer function and change the operational state to disabled and the availability status to dependency.

    In addition to other policy node types, there is a *subsystemTest()* method that is periodically called by the *IntegrityMonitor*.  In Drools PDP, *subsystemTest* has been overwritten to execute an audit of the Database and of the Maven Repository.  If the audit is unable to verify the function of either the DB or the Maven Repository, he Drools PDP will declare that it can no longer function and change the operational state to disabled and the availability status to dependency.

    When a failed dependency returns to normal operation, the *IntegrityMontor* will change the operational state to enabled and availability status to null.

**External Health Monitoring Interface**

    The Drools PDP has a http test interface which, when called, will return 200 if all seems well and 500 otherwise.  The test interface URL is defined in the properties file.


Site Manager
------------

The Site Manager is not deployed with the Drools PDP, but it is available in the policy/common repository in the site-manager directory.   
The Site Manager provides a lock/unlock interface for nodes and a way to display node information and status.

The following is from the README file included with the Site Manager.

    .. code-block:: bash
       :caption: Site Manager README extract

        Before using 'siteManager', the file 'siteManager.properties' needs to be 
        edited to configure the parameters used to access the database:
        
            javax.persistence.jdbc.driver - typically 'org.mariadb.jdbc.Driver'
        
            javax.persistence.jdbc.url - URL referring to the database,
                which typically has the form: 'jdbc:mariadb://<host>:<port>/<db>'
                ('<db>' is probably 'xacml' in this case)
        
            javax.persistence.jdbc.user - the user id for accessing the database
        
            javax.persistence.jdbc.password - password for accessing the database
        
        Once the properties file has been updated, the 'siteManager' script can be
        invoked as follows:
        
            siteManager show [ -s <site> | -r <resourceName> ] :
                display node information (Site, NodeType, ResourceName, AdminState, 
                                          OpState, AvailStatus, StandbyStatus)
        
            siteManager setAdminState { -s <site> | -r <resourceName> } <new-state> :
                update admin state on selected nodes
        
            siteManager lock { -s <site> | -r <resourceName> } :
                lock selected nodes
        
            siteManager unlock { -s <site> | -r <resourceName> } :
                unlock selected nodes
        
Note that the 'siteManager' script assumes that the script, 
'site-manager-${project.version}.jar' file and 'siteManager.properties' file
are all in the same directory. If the files are separated, the 'siteManager'
script will need to be modified so it can locate the jar and properties files.


Properties
----------

The feature-state-mangement.properties file controls the function of the State Management Feature.  In general, the properties have adequate descriptions in the file. Parameters which must be replaced prior to usage are indicated thus: ${{parameter to be replaced}}.

    .. code-block:: bash
       :caption: feature-state-mangement.properties 

        # DB properties
        javax.persistence.jdbc.driver=org.mariadb.jdbc.Driver
        javax.persistence.jdbc.url=jdbc:mariadb://${{SQL_HOST}}:3306/statemanagement
        javax.persistence.jdbc.user=${{SQL_USER}}
        javax.persistence.jdbc.password=${{SQL_PASSWORD}}
        
        # DroolsPDPIntegrityMonitor Properties
        # Test interface host and port defaults may be overwritten here
        http.server.services.TEST.host=0.0.0.0
        http.server.services.TEST.port=9981
        #These properties will default to the following if no other values are provided:
        # http.server.services.TEST.restClasses=org.onap.policy.drools.statemanagement.IntegrityMonitorRestManager
        # http.server.services.TEST.managed=false
        # http.server.services.TEST.swagger=true
        
        #IntegrityMonitor Properties
        
        # Must be unique across the system
        resource.name=pdp1
        # Name of the site in which this node is hosted 
        site_name=site1
        # Forward Progress Monitor update interval seconds
        fp_monitor_interval=30
        # Failed counter threshold before failover 
        failed_counter_threshold=3
        # Interval between test transactions when no traffic seconds
        test_trans_interval=10
        # Interval between writes of the FPC to the DB seconds 
        write_fpc_interval=5
        # Node type Note: Make sure you don't leave any trailing spaces, or you'll get an 'invalid node type' error! 
        node_type=pdp_drools
        # Dependency groups are groups of resources upon which a node operational state is dependent upon. 
        # Each group is a comma-separated list of resource names and groups are separated by a semicolon.  For example:
        # dependency_groups=site_1.astra_1,site_1.astra_2;site_1.brms_1,site_1.brms_2;site_1.logparser_1;site_1.pypdp_1
        dependency_groups=
        # When set to true, dependent health checks are performed by using JMX to invoke test() on the dependent.
        # The default false is to use state checks for health.
        test_via_jmx=true
        # This is the max number of seconds beyond which a non incrementing FPC is considered a failure
        max_fpc_update_interval=120
        # Run the state audit every 60 seconds (60000 ms).  The state audit finds stale DB entries in the 
        # forwardprogressentity table and marks the node as disabled/failed in the statemanagemententity 
        # table. NOTE! It will only run on nodes that have a standbystatus = providingservice.
        # A value of <= 0 will turn off the state audit.
        state_audit_interval_ms=60000
        # The refresh state audit is run every (default) 10 minutes (600000 ms) to clean up any state corruption in the 
        # DB statemanagemententity table. It only refreshes the DB state entry for the local node.  That is, it does not
        # refresh the state of any other nodes.  A value <= 0 will turn the audit off. Any other value will override 
        # the default of 600000 ms.
        refresh_state_audit_interval_ms=600000
        
        
        # Repository audit properties
        # Assume it's the releaseRepository that needs to be audited,
        # because that's the one BRMGW will publish to.
        repository.audit.id=${{releaseRepositoryID}}
        repository.audit.url=${{releaseRepositoryUrl}}
        repository.audit.username=${{repositoryUsername}}
        repository.audit.password=${{repositoryPassword}}
        repository2.audit.id=${{releaseRepository2ID}}
        repository2.audit.url=${{releaseRepository2Url}}
        repository2.audit.username=${{repositoryUsername2}}
        repository2.audit.password=${{repositoryPassword2}}
        
        # Repository Audit Properties
        # Flag to control the execution of the subsystemTest for the Nexus Maven repository
        repository.audit.is.active=false
        repository.audit.ignore.errors=true
        repository.audit.interval_sec=86400
        repository.audit.failure.threshold=3
        
        # DB Audit Properties
        # Flag to control the execution of the subsystemTest for the Database
        db.audit.is.active=false


End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Feature+State+Management


