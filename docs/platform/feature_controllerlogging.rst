
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

***************************
Feature: Controller Logging
***************************

.. contents::
    :depth: 3

Summary
^^^^^^^
The controller logging feature provides a way to log network topic messages to a separate controller log file for each controller. This allows a clear separation of network traffic between all of the controllers.

Enabling Controller Logging
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Type "features enable controller-logging". The feature will now display as "enabled".

    .. image:: ctrlog_enablefeature.png

When the feature's enable script is executed, it will search the $POLICY_HOME/config directory for any logback files containing the prefix "logback-include-". These logger configuration files are typically provided with a feature that installs a controlloop (ex: controlloop-amsterdam and controlloop-casablanca features). Once these configuration files are found by the enable script, the logback.xml config file will be updated to include the configurations.

    .. image:: ctrlog_logback.png


Controller Logger Configuration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The contents of a logback-include-``*``.xml file follows the same configuration syntax as the logback.xml file. It will contain the configurations for the logger associated with the given controller. 

    .. note:: A controller logger MUST be configured with the same name as the controller (ex: a controller named "casablanca" will have a logger named "casablanca").

    .. image:: ctrlog_config.png


Viewing the Controller Logs
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Once a logger for the controller is configured, start the drools-pdp and navigate to the $POLICY_LOGS directory. A new controller specific network log will be added that contains all the network topic traffic of the controller.

    .. image:: ctrlog_view.png

The original network log remains and will append traffic information from all topics regardless of which controller it is for. To abbreviate and customize messages for the network log, refer to the `Feature MDC Filters <feature_mdcfilters.html>`_ documentation.


End of Document


