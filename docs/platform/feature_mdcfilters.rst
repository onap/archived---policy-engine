
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

********************
Feature: MDC Filters
********************

.. contents::
    :depth: 3

Summary
^^^^^^^
The MDC Filter Feature provides configurable properties for network topics to extract fields from JSON strings and place them in a mapped diagnostic context (MDC).

Network Log Structure Before Feature Enabled
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Before enabling the feature, the network log contains the entire content of each message received on a topic. Below is a sample message from the network log. Note that the topic used for this tutorial is DCAE-CL.

    .. code-block:: bash

        [2019-03-22T16:36:42.942+00:00|DMAAP-source-DCAE-CL][IN|DMAAP|DCAE-CL]
        {"closedLoopControlName":"ControlLoop-vCPE-48f0c2c3-a172-4192-9ae3-052274181b6e","closedLoopAlarmStart":1463679805324,"closedLoopEventClient":"DCAE_INSTANCE_ID.dcae-tca","closedLoopEventStatus":"ONSET","requestID":"664be3d2-6c12-4f4b-a3e7-c349acced200","target_type":"VNF","target":"generic-vnf.vnf-id","AAI":{"vserver.is-closed-loop-disabled":"false","vserver.prov-status":"ACTIVE","generic-vnf.vnf-id":"vCPE_Infrastructure_vGMUX_demo_app"},"from":"DCAE","version":"1.0.2"}

The network log can become voluminous if messages received from various topics carry large messages for various controllers. With the MDC Filter Feature, users can define keywords in JSON messages to extract and structure according to a desired format. This is done through configuring the feature's properties.

Configuring the MDC Filter Feature
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

To configure the feature, the feature must be enabled using the following command:

    .. code-block:: bash

        features enable mdc-filters


    .. image:: mdc_enablefeature.png

Once the feature is enabled, there will be a new properties file in *$POLICY_HOME/config* called **feature-mdc-filters.properties**.

    .. image:: mdc_properties.png

The properties file contains filters to extract key data from messages on the network topics that are saved in an MDC, which can be referenced in logback.xml.  The configuration format is as follows:

    .. code-block:: bash

       <protocol>.<type>.topics.<topic-name>.mdcFilters=<filters>

       Where:
          <protocol> = ueb, dmaap, noop
          <type> = source, sink
          <topic-name> = Name of DMaaP or UEB topic
          <filters> = Comma separated list of key/json-path(s)

The filters consist of an MDC key used by **logback.xml** (see below) and the JSON path(s) to the desired data. The path always begins with '$', which signifies the root of the JSON document. The underlying library, JsonPath, uses a query syntax for searching through a JSON file. The query syntax and some examples can be found at https://github.com/json-path/JsonPath.  An example filter for the *DCAE-CL* is provided below:

    .. code-block:: bash

       dmaap.source.topics.DCAE-CL.mdcFilters=requestID=$.requestID

This filter is specifying that the dmaap source topic *DCAE-CL* will search each message received for requestID by following the path starting at the root ($) and searching for the field *requestID*. If the field is found, it is placed in the MDC with the key "requestID" as signified by the left hand side of the filter before the "=".


Configuring Multiple Filters and Paths
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Multiple fields can be found for a given JSON document by a comma separated list of <mdcKey,jsonPath> pairs. For the previous example, another filter is added by adding a comma and specifying the filter as follows:

    .. code-block:: bash

       dmaap.source.topics.DCAE-CL.mdcFilters=requestID=$.requestID,closedLoopName=$.closedLoopControlName

The feature will now search for both requestID and closedLoopControlName in a JSON message using the specified "$." path notations and put them in the MDC using the keys "requestID" and "closedLoopName" respectively. To further refine the filter, if a topic receives different message structures (ex: a response message structure vs an error message structure) the "|" notation allows multiple paths to a key to be defined. The feature will search through each specified path until a match is found. An example can be found below:

    .. code-block:: bash

       dmaap.source.topics.DCAE-CL.mdcFilters=requestID=$.requestID,closedLoopName=$.closedLoopControlName|$.AAI.closedLoopControlName

Now when the filter is searching for closedLoopControlName it will check the first path "$.closedLoopControlName", if it is not present then it will try the second path "$.AAI.closedLoopControlName". If the user is unsure of the path to a field, JsonPath supports a deep scan by using the ".." notation. This will search the entire JSON document for the field without specifying the path.


Accessing the MDC Values in logback.xml
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Once the feature properties have been defined, logback.xml contains a "abstractNetworkPattern" property that will hold the desired message structure defined by the user. The user has the flexibility to define the message structure however they choose but for this tutorial the following pattern is used:

    .. code-block:: bash

       <property name="abstractNetworkPattern" value="[%d{yyyy-MM-dd'T'HH:mm:ss.SSS+00:00, UTC}] [%X{networkEventType:-NULL}|%X{networkProtocol:-NULL}|%X{networkTopic:-NULL}|%X{requestID:-NULL}|%X{closedLoopName:-NULL}]%n" />

The "value" portion consists of two headers in bracket notation, the first header defines the timestamp while the second header references the keys from the MDC filters defined in the feature properties. The standard logback syntax is used and more information on the syntax can be found here. Note that some of the fields here were not defined in the feature properties file. The feature automatically puts the network infrastructure information in the keys that are prepended with "network". The current supported network infrastructure information is listed below.

   +-------------------+-------------------------------------------------+
   |     Field         |    Values                                       |
   +===================+=================================================+
   | networkEventType  | IN, OUT                                         |
   +-------------------+-------------------------------------------------+
   | networkProtocol   | DMAAP, UEB, NOOP                                |
   +-------------------+-------------------------------------------------+
   | networkTopic      | The name of the topic that received the message |
   +-------------------+-------------------------------------------------+


To reference the keys from the feature properties the syntax "%X{KEY_DEFINED_IN_PROPERTIES}" provides access to the value. An optional addition is to append ":-", which specifies a default value to display in the log if the field was not found in the message received. For this tutorial, a default of "NULL" is displayed for any of the fields that were not found while filtering. The "|" has no special meaning and is just used as a field separator for readability; the user can decorate the log format to their desired visual appeal.

Network Log Structure After Feature Enabled
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Once the feature and logback.xml is configured to the user's desired settings, start the PDP-D by running "policy start". Based on the configurations from the previous sections of this tutorial, the following log message is written to network log when a message is received on the DCAE-CL topic:

    .. code-block:: bash

       [2019-03-22T16:38:23.884+00:00] [IN|DMAAP|DCAE-CL|664be3d2-6c12-4f4b-a3e7-c349acced200|ControlLoop-vCPE-48f0c2c3-a172-4192-9ae3-052274181b6e]

The message has now been filtered to display the network infrastructure information and the extracted data from the JSON message based on the feature properties. In order to view the entire message received from a topic, a complementary feature was developed to display the entire message on a per controller basis while preserving the compact network log.  Refer to the `Feature Controller Logging <feature_controllerlogging.html>`_ documentation for details.



End of Document

