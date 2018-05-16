
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

***************************
PDP-D Software Architecture
***************************

.. contents::
    :depth: 3

Overview
^^^^^^^^

| In ONAP, PDP-D is the Policy component that executes Operational Policies (see `Control Loop Operational Policy`_ ).  It uses `drools`_ as the underlying rule based engine to execute policies.
|
| The PDP-D name may not be reflective of its nature, as it is generic middleware, a maven based drools rules based application container. 
|
| The PDP-D generic application container is maintained in the policy/drools-pdp repository (https://git.onap.org/policy/drools-pdp).
|
| The Control Loop Operational Policies and support libraries applications are maintained in the policy/drools-applications repository (https://git.onap.org/policy/drools-applications/).


PDP-D application container
^^^^^^^^^^^^^^^^^^^^^^^^^^^

| The PDP-D lightweight application container provides generic services to the drools applications (0..n) running on it.  (https://git.onap.org/policy/drools-pdp)

There is the distinction of **core** software and optional **extensions** to the core functionality.

Core Software
-------------

The core critical functionality that always runs in any PDP-D container is maintained in those projects named with the "*policy-*" prefix.   These are:

- policy-utils (utilities)
- policy-core (drools libraries interfaces)
- policy-endpoints (networking)
- policy-management. (management of the platform)

This is the **minimum** set of services that any PDP-D application provides to their executing drools-applications.

The intent is that this functionality is kept stable, fast, and minimal.   As new development is added, the intent is that this functionality is resilient to breakage as side effects to new development.

Extensions
----------

Extensions are optional modules that are commonly known as **features** that extends the PDP-D core functionality with new enhancements.   

**Features** can be enabled or disabled.   When a feature is disabled, it is a *hard disable* with no side effects on the *core functionality*, meaning no additional libraries, configuration, etc. that may have side effects on the core functionality described above.

The current extensions supported are:

- `Feature Test Transaction  <feature_testtransaction.html>`_ (disabled by default)
- `Feature State Management <feature_statemgmt.html>`_ (disabled by default)
- `Feature EELF <feature_eelf.html>`_ (disabled by default)
- `Feature Healthcheck <feature_healthcheck.html>`_ (enabled by default)
- `Feature Session Persistence <feature_sesspersist.html>`_ (disabled by default)
- `Feature Active/Standby Management <feature_activestdbymgmt.html>`_ (disabled by default)
- `Feature Distributed Locking <feature_locking.html>`_ (enabled in OOM installation by default)
- `Feature Pooling <feature_pooling.html>`_ (enabled in OOM installation by default)

.. seealso:: Click on the individual feature links for more information 


.. _Control Loop Operational Policy: https://wiki.onap.org/display/DW/Control+Loop+Operational+Policy
.. _drools: https://www.drools.org


End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/PAP+Software+Architecture


