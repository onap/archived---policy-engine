
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

****************
Feature: Pooling
****************

Summary
^^^^^^^

The Pooling feature provides the ability to load-balance work across a “pool” of active-active Drools-PDP hosts.   This particular implementation uses a DMaaP topic for communication between the hosts within the pool.

The pool is adjusted automatically, with no manual intervention when:
    * a new host is brought online
    * a host goes offline, whether gracefully or due to a failure in the host or in the network

Assumptions and Limitations
^^^^^^^^^^^^^^^^^^^^^^^^^^^
    * Session persistence is not required
    * Data may be lost when processing is moved from one host to another
    * The entire pool may shut down if the inter-host DMaaP topic becomes inaccessible

    .. image:: poolingDesign.png


Key Points
^^^^^^^^^^
    * Requests are received on a common DMaaP topic
    * DMaaP distributes the requests randomly to the hosts
    * The request topic should have at least as many partitions as there are hosts
    * Uses a single, internal DMaaP topic for all inter-host communication
    * Allocates buckets to each host
    * Requests are assigned to buckets based on their respective “request IDs”
    * No session persistence
    * No objects copied between hosts
    * Requires feature(s): distributed-locking
    * Precludes feature(s): session-persistence, active-standby, state-management

Example Scenario
^^^^^^^^^^^^^^^^
    1. Incoming DMaaP message is received on a topic — all hosts are listening, but only one random host receives the message
    2. Decode message to determine “request ID” key (message-specific operation)
    3. Hash request ID to determine the bucket number
    4. Look up host associated with hash bucket (most likely remote)
    5. Publish “forward” message to internal DMaaP topic, including remote host, bucket number, DMaaP topic information, and message body
    6. Remote host verifies ownership of bucket, and routes the DMaaP message to its own rule engine for processing

    The figure below shows several different hosts in a pool.  Each host as a copy of the bucket assignments, which specifies which buckets are assigned to which hosts.  Incoming requests are mapped to a bucket, and a bucket is mapped to a host, to which the request is routed.  The host table includes an entry for each active host in the pool, to which one or more buckets are mapped.

    .. image:: poolingPdps.png

Bucket Reassignment
^^^^^^^^^^^^^^^^^^^
    * When a host goes up or down, buckets are rebalanced
    * Attempts to maintain an even distribution
    * Leaves buckets with their current owner, where possible
    * Takes a few buckets from each host to assign to new hosts

    For example, in the diagram below, the left side shows how 32 buckets might be assigned among four different hosts.  When the first host fails, the buckets from host 1 would be reassigned among the remaining hosts, similar to what is shown on the right side of the diagram.  Any requests that were being processed by host 1 will be lost and must be restarted.  However, the buckets that had already been assigned to the remaining hosts are unchanged, thus requests associated with those buckets are not impacted by the loss of host 1.

    .. image:: poolingBuckets.png

Usage
^^^^^

For pooling to be enabled, the distributed-locking feature must be also be enabled.

    .. code-block:: bash
       :caption: Enable Feature Pooling

        policy stop

        features enable distributed-locking
        features enable pooling-dmaap

    The configuration is located at:

    * $POLICY_HOME/config/feature-pooling-dmaap.properties


    .. code-block:: bash
       :caption: Start the PDP-D using pooling

        policy start


    .. code-block:: bash
       :caption: Disable the pooling feature

        policy stop
        features disable pooling-dmaap
        policy start


End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Feature+Pooling


