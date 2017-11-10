
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

**********************************
Feature: Active/Standby Management 
**********************************

.. contents::
    :depth: 3

Summary
^^^^^^^
When the Feature Session Persistence is enabled, there can only be one active/providing service Drools PDP due to the behavior of Drools persistence. The Active/Standby Management Feature controls the selection of the Drools PDP that is providing service. It utilizes its own database and the State Management Feature database in the election algorithm.  All Drools PDP nodes periodically run the election algorithm and, since they all use the same data, all nodes come to the same conclusion with the "elected" node assuming an active/providingservice state.  Thus, the algorithm is distributed and has no single point of failure - assuming the database is configured for high availability.

When the algorithm selects a Drools PDP to be active/providing service the controllers and topic endpoints are unlocked and allowed to process transactions. When a Drools PDP transitions to a hotstandby or coldstandby state, the controllers and topic endpoints are locked, preventing the Drools PDP from handling transactions.


Usage
^^^^^

Enabling and Disabling Feature State Management
-----------------------------------------------

The Active/Standby Management Feature is enabled from the command line when logged in as policy after configuring the feature properties file (see Description Details section).  From the command line:

- > features status - Lists the status of features
- > features enable active-standby-management - Enables the Active-Standby Management Feature
- > features disable active-standby-management - Disables the Active-Standby Management Feature

The Drools PDP must be stopped prior to enabling/disabling features and then restarted after the features have been enabled/disabled.

    .. code-block:: bash
       :caption: Enabling Active/Standby Management Feature

        policy@hyperion-4:/opt/app/policy$ policy stop
        [drools-pdp-controllers]
         L []: Stopping Policy Management... Policy Management (pid=354) is stopping... Policy Management has stopped.
        policy@hyperion-4:/opt/app/policy$ features enable active-standby-management
        name                      version         status
        ----                      -------         ------
        controlloop-utils         1.1.0-SNAPSHOT  disabled
        healthcheck               1.1.0-SNAPSHOT  disabled
        test-transaction          1.1.0-SNAPSHOT  disabled
        eelf                      1.1.0-SNAPSHOT  disabled
        state-management          1.1.0-SNAPSHOT  disabled
        active-standby-management 1.1.0-SNAPSHOT  enabled
        session-persistence       1.1.0-SNAPSHOT  disabled


Description Details
^^^^^^^^^^^^^^^^^^^

Election Algorithm
------------------

The election algorithm selects the active/providingservice Drools PDP. The algorithm on each node reads the *standbystatus* from the *StateManagementEntity* table for all other nodes to determine if they are providingservice or in a hotstandby state and able to assume an active status. It uses the *DroolsPdpEntity* table to verify that other node election algorithms are currently functioning and when the other nodes were last designated as the active Drools PDP.

In general terms, the election algorithm periodically gathers the standbystatus and designation status for all the Drools PDPs. If the node which is currently designated as providingservice is "current" in updating its status, no action is required.  If the designated node is either not current or has a standbystatus other than providingservice, it is time to choose another designated *DroolsPDP*.  The algorithm will build a list of all DroolsPDPs that are current and have a *standbystatus* of *hotstandby*.  It will then give preference to DroolsPDPs within the same site, choosing the DroolsPDP with the lowest lexicographic value to the droolsPdpId (resourceName).  If the chosen DroolsPDP is itself, it will promote its standbystatus from hotstandby to providingservice.  If the chosen DroolsPDP is other than itself, it will do nothing.

When the DroolsPDP promotes its *standbystatus* from hotstandby to providing service, a state change notification will occur and the Standby State Change Handler will take appropriate action.


Standby State Change Handler
----------------------------

The Standby State Change Handler (*PMStandbyStateChangeHandler* class) extends the IntegrityMonitor StateChangeNotifier class which implements the Observer class.  When the DroolsPDP is constructed, an instance of the handler is constructed and registered with StateManagement.  Whenever StateManagement implements a state transition, it calls the *handleStateChange()* method of the handler.  If the StandbyStatus transitions to hot or cold standby, the handler makes a call into the lower level management layer to lock the application controllers and topic endpoints, preventing it from handling transactions.  If the StandbyStatus transitions to providingservice, the handler makes a call into the lower level management layer to unlock the application controllers and topic endpoints, allowing it to handle transactions.


Database
--------

The Active/Standby Feature creates a database named activestandbymanagement with a single table, **droolspdpentity**.  The election handler uses that table to determine which DroolsPDP was/is designated as the active DroolsPDP and which DroolsPDP election handlers are healthy enough to periodically update their status. 

The **droolspdpentity** table has the following columns:
    - **pdpId** - The unique indentifier for the DroolsPDP.  It is the same as the resourceName
    - **designated** - Has a value of 1 if the DroolsPDP is designated as active/providingservice.  It has a value of 0 otherwise
    - **priority** - Indicates the priority level of the DroolsPDP for the election handler.  In general, this is ignore and all have the same priority.
    - **updatedDate** - This is the timestamp for the most recent update of the record.
    - **designatedDate** - This is the timestamp that indicates when the designated column was most recently set to a value of 1
    - **site** - This is the name of the site

Properties
----------

The properties are found in the feature-active-standby-management.properties file. In general, the properties are adequately described in the properties file. Parameters which must be replaced prior to usage are indicated thus: ${{parameter to be replaced}}

    .. code-block:: bash
       :caption: feature-active-standby-mangement.properties 

        # DB properties
        javax.persistence.jdbc.driver=org.mariadb.jdbc.Driver
        javax.persistence.jdbc.url=jdbc:mariadb://${{SQL_HOST}}:3306/activestandbymanagement
        javax.persistence.jdbc.user=${{SQL_USER}}
        javax.persistence.jdbc.password=${{SQL_PASSWORD}}
        
        # Must be unique across the system
        resource.name=pdp1
        # Name of the site in which this node is hosted 
        site_name=site1
        
        # Needed by DroolsPdpsElectionHandler
        pdp.checkInterval=1500 # The interval in ms between updates of the updatedDate
        pdp.updateInterval=1000 # The interval in ms between executions of the election handler
        #pdp.timeout=3000
        # Need long timeout, because testTransaction is only run every 10 seconds.
        pdp.timeout=15000
        #how long do we wait for the pdp table to populate on initial startup
        pdp.initialWait=20000


End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/pages/viewpage.action?pageId=16005790


