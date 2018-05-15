
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

****************************
Feature: Distributed Locking
****************************

Summary
^^^^^^^

The Distributed Locking Feature provides locking of resources across a pool of PDP-D hosts.  The list of locks is maintained in a database, where each record includes a resource identifier, an owner identifier, and an expiration time.  Typically, a drools application will unlock the resource when it's operation completes.  However, if it fails to do so, then the resource will be automatically released when the lock expires, thus preventing a resource from becoming permanently locked.

Usage
^^^^^

    .. code-block:: bash
       :caption: Enable Feature Distributed Locking 

        policy stop

        features enable distributed-locking

    The configuration is located at:

    * $POLICY_HOME/config/feature-distributed-locking.properties


    .. code-block:: bash
       :caption: Start the PDP-D using pooling

        policy start


    .. code-block:: bash
       :caption: Disable the Distributed Locking feature

        policy stop
        features disable distributed-locking
        policy start


End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Feature+Distributed+Locking


