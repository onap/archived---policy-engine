
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

****************************
Verifying/Modifying AAI Data
****************************

.. contents::
    :depth: 3

This page highlights key commands used by Policy to look at and modify A&AI data for testing purposes.  Please refer to the A&AI REST API Documentation for more details.

Checking Current Data 
^^^^^^^^^^^^^^^^^^^^^

To get all the vnfs that are in AAI
-----------------------------------

Use this command if you want to get all the vnf's that are provisioned in A&AI.  This is useful if you want to find a couple vnf's you can later query.

    .. code-block:: bash

        curl --silent -k -u "<userName>:<password>" --header "X-FromAppId: <fromApp>" --header "Content-Type: application/json" --header "Accept: application/json" --header "X-TransactionId: <requestID>" -X GET https://aai.api.simpledemo.openecomp.org:8443/aai/v11/network/generic-vnfs | python -m json.tool

To get a specific vnf
---------------------

If you have a **vnf-id**, this command returns the details related to the specific vnf id you are querying.  Policy primarily does this query if the onset has a vnf id  but not the isClosedLoopDisabled field.

    .. code-block:: bash

        curl --silent -k -u "<userName>:<password>" --header "X-FromAppId: <fromApp>" --header "Content-Type: application/json" --header "Accept: application/json" --header "X-TransactionId: <requestID>" -X GET https://aai.api.simpledemo.openecomp.org:8443/aai/v11/network/generic-vnfs/generic-vnf/<vnfID> | python -m json.tool

If you have a **vnf-name**, this command returns the details related to the specific vnf name you are querying.  Policy primarily does this query if the onset has a vnf name but no vnf id.

    .. code-block:: bash

        curl --silent -k -u "<userName>:<password>" --header "X-FromAppId: <fromApp>" --header "Content-Type: application/json" --header "Accept: application/json" --header "X-TransactionId: <requestID>" -X GET https://aai.api.simpledemo.openecomp.org:8443/aai/v11/network/generic-vnfs/generic-vnf?vnf-name=<vnfName> | python -m json.tool

To find all the vservers 
------------------------

Follow these steps to get all of the vservers.  This is useful to get a couple of vservers to query later, either manually or through a closed loop.

**Step 1:**  Execute the following:

    .. code-block:: bash

        curl --silent -k -u "<userName>:<password>" --header "X-FromAppId: <fromApp>" --header "Content-Type: application/json" --header "Accept: application/json" --header "X-TransactionId: <requestID>" -X GET https://aai.api.simpledemo.openecomp.org:8443/aai/v11/cloud-infrastructure/cloud-regions | python -m json.tool

    Take note of all the cloud-owner/cloud-region combinations.  In this example, there are 3 combinations: Skynet/CL-MR1, AMIST/AMCR1, and Rackspace/DFW.

    .. image:: modAAI_getCloudRegions.png

**Step 2:**  Invoke the following command for each combination:

    .. code-block:: bash

        curl --silent -k -u "<userName>:<password>" --header "X-FromAppId: <fromApp>" --header "Content-Type: application/json" --header "Accept: application/json" --header "X-TransactionId: <requestID>" -X GET https://aai.api.simpledemo.openecomp.org:8443/aai/v11/cloud-infrastructure/cloud-regions/cloud-region/<cloudOwner>/<cloudRegion>?depth=all | python -m json.tool

    .. image:: modAAI_getAllVserver.PNG

To get a specific vserver
-------------------------

Use this command to get the details of a  specific vserver based on its vserver name.

    .. code-block:: bash

        curl --silent -k -u "<userName>:<password>" --header "X-FromAppId: <fromApp>" --header "Content-Type: application/json" --header "Accept: application/json" --header "X-TransactionId: <requestID>" -X GET https://aai.api.simpledemo.openecomp.org:8443/aai/v11/nodes/vservers?vserver-name=<vserverName> | python -m json.tool

    .. image:: modAAI_getByVserverName.PNG

Named-Queries
-------------

These commands are used to get more information than can be obtained in a  single other query.  They require more data to be sent in the query, but return information on the related instances of a given vnf or vserver, as well as the information about the vnf/vserver itself.

**For vFW:**

    .. code-block:: bash

        curl --silent -k -u "<userName>:<password>" --header "X-FromAppId: <fromApp>" --header "Content-Type: application/json" --header "Accept: application/json" --header "X-TransactionId: <requestID>" -d "{\"query-parameters\": { \"named-query\": { \"named-query-uuid\": \"a93ac487-409c-4e8c-9e5f-334ae8f99087\" } }, \"instance-filters\":{\"instance-filter\":[ {\"generic-vnf\": { \"vnf-id\": \"<vnfID>\"}}]}}" -X POST https://aai.api.simpledemo.openecomp.org:8443/aai/search/named-query | python -m json.tool

    .. image:: modAAI_namedQueryVnfId.PNG

**For vDNS:**

    .. code-block:: bash

        curl --silent -k -u "<userName>:<password>" --header "X-FromAppId: <fromApp>" --header "Content-Type: application/json" --header "Accept: application/json" --header "X-TransactionId: <requestID>" -d "{\"query-parameters\": { \"named-query\": { \"named-query-uuid\": \"4ff56a54-9e3f-46b7-a337-07a1d3c6b469\" } }, \"instance-filters\":{\"instance-filter\":[ {\"vserver\": { \"vserver-name\": \"<vnfID>\"}}]}}" -X POST https://aai.api.simpledemo.openecomp.org:8443/aai/search/named-query | python -m json.tool

Adding Data to A&AI 
^^^^^^^^^^^^^^^^^^^

Generic-Vnf
-----------

    .. code-block:: bash

        curl --silent -k -u "<username>:<password>" --header "X-FromAppId: POLICY" --header "Content-Type: application/json" --header "Accept: application/json" --header "X-TransactionId: 8611ece5-5786-4e71-b72f-e87ef44029da" -X PUT -H "Content-Type: application/json" --data @addVnf.txt https://aai.api.simpledemo.openecomp.org:8443/aai/v11/network/generic-vnfs/generic-vnf/<vnfID> | python -m json.tool

The addVNF.txt file is just the data you would like to add.  At minimum, the vnf-id, vnf-name, vnf-type and is-closed-loop-disabled fields need to be filled out, and the vnf-id needs to match the one you choose in the url of the curl command.

Vserver
-------

    .. code-block:: bash

        curl --silent -k -u "<username>:<password>" --header "X-FromAppId: POLICY" --header "Content-Type: application/json" --header "Accept: application/json" --header "X-TransactionId: 8611ece5-5786-4e71-b72f-e87ef44029da" -X PUT -H "Content-Type: application/json" --data @addVserver.txt https://aai.api.simpledemo.openecomp.org:8443/aai/v11/cloud-infrastructure/cloud-regions/cloud-region/<cloud-owner>/<cloud-region-id>/tenants/tenant/<tenant-id>/vservers/vserver/<vserver-id>

The addVserver.txt file is the vserver object you would like to add.  It needs values for vserver-id, vserver-name, vserver-selflink, in-maint, and is-close-loop-disabled at minimum.  The values of <cloud-owner>, <cloud-region-id>, and <tenants> depends on the values already in Rackspace, see the section above under finding all Vservers. 

Named Queries
-------------

The data for the named queries is based off of the data in the relationship-list field for both vservers and vnfs.

End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/pages/viewpage.action?pageId=16005849#Verifying/ModifyingAAIData


