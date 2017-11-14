
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

************************************
Feature: Session Persistence
************************************

The session persistence feature allows drools kie sessions to be persisted in a database surviving pdp-d restarts.

    .. code-block:: bash
       :caption: Enable session persistence
       :linenos:

        policy stop
        features enable session-persistence

The configuration is located at:

    -  *$POLICY_HOME/config/feature-session-persistence.properties*

Each controller that wants to be started with persistence should contain the following line in its *<controller-name>-controller.properties*

    -  *persistence.type=auto*

    .. code-block:: bash
       :caption: Start the PDP-D using session-persistence
       :linenos:

        db-migrator -o upgrade -s ALL
        policy start

Facts will survive PDP-D restart using the native drools capabilities and introduce a performance overhead.

    .. code-block:: bash
       :caption: Disable the session-persistence feature
       :linenos:

        policy stop
        features disable session-persistence
        sed -i "/persistence.type=auto/d" <controller-name>-controller.properties 
        db-migrator -o erase -s sessionpersistence   # delete all its database data (optional)
        policy start

End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Feature+Session+Persistence


