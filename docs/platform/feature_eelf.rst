
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

*************************************************
Feature: EELF (Event and Error Logging Framework) 
*************************************************

.. contents::
    :depth: 3

Summary
^^^^^^^
The EELF feature provides backwards compatibility with R0 logging functionality. It supports the use of EELF/Common Framework style logging at the same time as traditional logging.

.. seealso:: Additional information for EELF logging can be found at `EELF wiki`_.

.. _EELF wiki: https://github.com/att/EELF/wiki


Usage
^^^^^

To utilize the eelf logging capabilities, first stop policy engine and then enable the feature using the "*features*" command.

    .. code-block:: bash
       :caption: Enabling EELF Feature

        policy@hyperion-4:/opt/app/policy$ policy stop
        [drools-pdp-controllers]
         L []: Stopping Policy Management... Policy Management (pid=354) is stopping... Policy Management has stopped.
        policy@hyperion-4:/opt/app/policy$ features enable eelf
        name                      version         status
        ----                      -------         ------
        controlloop-utils         1.1.0-SNAPSHOT  disabled
        healthcheck               1.1.0-SNAPSHOT  disabled
        test-transaction          1.1.0-SNAPSHOT  disabled
        eelf                      1.1.0-SNAPSHOT  enabled
        state-management          1.1.0-SNAPSHOT  disabled
        active-standby-management 1.1.0-SNAPSHOT  disabled
        session-persistence       1.1.0-SNAPSHOT  disabled

The output of the enable command will indicate whether or not the feature was enabled successfully.

Policy engine can then be started as usual.



End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Feature+EELF

