.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

*********************
HTTPS and AAF Support
*********************

.. contents::
    :depth: 3

The core Policy components part of Beijing release have been migrated from HTTP to HTTPS.  Server certificates were derived from the AAF Root CA.

AAF is supported for externally facing entry points into the Policy subsystem.   These are:

* PDP-D supports AAF for its telemetry and healthcheck APIs.
* PDP-X supports AAF for its external policy APIs.  It is currently disabled as some of clients are not AAF-capable, and this is a global setting.
* Console (for Browser Portal redirects) supports AAF when accessed through Portal.

+--------+------+------------+-----+-----+---------------------------------+
| Policy | Role | Remote     |HTTPS| AAF | Notes                           |
+========+======+============+=====+=====+=================================+
| pdp-d  |server| \*         |true |true |Healthchek and Telemetry APIs    |
+--------+------+------------+-----+-----+---------------------------------+
| pdp-d  |client| aaf        |true |true |Two-way TLS                      |
+--------+------+------------+-----+-----+---------------------------------+
| pdp-d  |client| aai        |true |true |Runtime Control Loop Execution   |
+--------+------+------------+-----+-----+---------------------------------+
| pdp-d  |client| dmaap      |true |false|Runtime Control Loop Execution   |
+--------+------+------------+-----+-----+---------------------------------+
| pdp-d  |client| so         |false|false|Not supported in so              |
+--------+------+------------+-----+-----+---------------------------------+
| pdp-d  |client| vfc        |false|false|Not supported in vfc             |
+--------+------+------------+-----+-----+---------------------------------+
| pdp-x  |server| \*         |true |false|Not all clients are AAF-capable  |
+--------+------+------------+-----+-----+---------------------------------+
| pap    |server| \*         |true |false|Not all clients are AAF-capable  |
+--------+------+------------+-----+-----+---------------------------------+
| console|server| portal     |true |true |Redirected from portal           |
+--------+------+------------+-----+-----+---------------------------------+
| brmsgw |client| dmaap      |true |false|Runtime Control Loop Execution   |
+--------+------+------------+-----+-----+---------------------------------+

AAF Configuration
^^^^^^^^^^^^^^^^^

The default demo ONAP installation comes up bootstrapped with the following AAF data with regards to Policy.

.. code-block:: bash
   :caption: Bootstrapped AAF configuration

    Basic Permissions:
        org.onap.policy.access         *                        *
        org.onap.policy.access         *                        read
        org.onap.policy.certman        local                    request,ignoreIPs,showpass

    Portal Permissions (for UI purposes, administered by Portal team):
        org.onap.policy.menu           menu_admin               *
        org.onap.policy.menu           menu_ajax                *
        org.onap.policy.menu           menu_concept             *
        org.onap.policy.menu           menu_customer            *
        org.onap.policy.menu           menu_customer_create     *
        org.onap.policy.menu           menu_doclib              *
        org.onap.policy.menu           menu_feedback            *
        org.onap.policy.menu           menu_help                *
        org.onap.policy.menu           menu_home                *
        org.onap.policy.menu           menu_itracker            *
        org.onap.policy.menu           menu_job                 *
        org.onap.policy.menu           menu_job_create          *
        org.onap.policy.menu           menu_job_designer        *
        org.onap.policy.menu           menu_logout              *
        org.onap.policy.menu           menu_map                 *
        org.onap.policy.menu           menu_notes               *
        org.onap.policy.menu           menu_policy              *
        org.onap.policy.menu           menu_process             *
        org.onap.policy.menu           menu_profile             *
        org.onap.policy.menu           menu_profile_create      *
        org.onap.policy.menu           menu_profile_import      *
        org.onap.policy.menu           menu_reports             *
        org.onap.policy.menu           menu_sample              *
        org.onap.policy.menu           menu_tab                 *
        org.onap.policy.menu           menu_task                *
        org.onap.policy.menu           menu_task_search         *
        org.onap.policy.menu           menu_test                *
        org.onap.policy.url            doclib                   *
        org.onap.policy.url            doclib_admin             *
        org.onap.policy.url            login                    *
        org.onap.policy.url            policy_admin             *
        org.onap.policy.url            policy_dashboard         *
        org.onap.policy.url            policy_dictionary        *
        org.onap.policy.url            policy_editor            *
        org.onap.policy.url            policy_pdp               *
        org.onap.policy.url            policy_push              *
        org.onap.policy.url            policy_roles             *
        org.onap.policy.url            view_reports             *

    PDP-D Permissions for Telemetry REST API access:
        org.onap.policy.pdpd.healthcheck               *  get
        org.onap.policy.pdpd.healthcheck.configuration *  get
        org.onap.policy.pdpd.telemetry                 *  delete
        org.onap.policy.pdpd.telemetry                 *  get
        org.onap.policy.pdpd.telemetry                 *  post
        org.onap.policy.pdpd.telemetry                 *  put

    PDP-X Permissions for XACML REST APIs:
        org.onap.policy.pdpx.config                    *                        *
        org.onap.policy.pdpx.createDictionary          *                        *
        org.onap.policy.pdpx.createPolicy              *                        *
        org.onap.policy.pdpx.decision                  *                        *
        org.onap.policy.pdpx.getConfig                 *                        *
        org.onap.policy.pdpx.getConfigByPolicyName     *                        *
        org.onap.policy.pdpx.getDecision               *                        *
        org.onap.policy.pdpx.getDictionary             *                        *
        org.onap.policy.pdpx.getMetrics                *                        *
        org.onap.policy.pdpx.list                      *                        *
        org.onap.policy.pdpx.listConfig                *                        *
        org.onap.policy.pdpx.listPolicy                *                        *
        org.onap.policy.pdpx.policyEngineImport        *                        *
        org.onap.policy.pdpx.pushPolicy                *                        *
        org.onap.policy.pdpx.sendEvent                 *                        *
        org.onap.policy.pdpx.updateDictionary          *                        *
        org.onap.policy.pdpx.updatePolicy              *                        *

    Basic Namespace Admin Roles:
        org.onap.policy.admin
        org.onap.policy.owner
        org.onap.policy.seeCerts

    Portal Roles for UI:
        org.onap.policy.Account_Administrator
        org.onap.policy.Policy_Admin
        org.onap.policy.Policy_Editor
        org.onap.policy.Policy_Guest
        org.onap.policy.Policy_Super_Admin
        org.onap.policy.Policy_Super_Guest
        org.onap.policy.Standard_User
        org.onap.policy.System_Administrator

    PDP-D Roles:
        org.onap.policy.pdpd.admin
        org.onap.policy.pdpd.monitor

    PDP-X Roles:
        org.onap.policy.pdpx.admin
        org.onap.policy.pdpx.monitor

    Users:
        demo@people.osaaf.org
        policy@policy.onap.org


demo@people.osaaf.org and policy@policy.onap.org are properly configured with AAF in n a default ONAP installation.  These are:


.. code-block:: bash
   :caption: Default permissions for demo and policy accounts.

   List Permissions by User[policy@policy.onap.org]
   --------------------------------------------------------------------------------
   PERM Type                      Instance                       Action
   --------------------------------------------------------------------------------
   org.onap.policy.access         *                              *
   org.onap.policy.access         *                              read
   org.onap.policy.certman        local                          request,ignoreIPs,showpass
   org.onap.policy.pdpd.healthcheck *                            get
   org.onap.policy.pdpd.healthcheck.configuration *              get
   org.onap.policy.pdpd.telemetry *                              delete
   org.onap.policy.pdpd.telemetry *                              get
   org.onap.policy.pdpd.telemetry *                              post
   org.onap.policy.pdpd.telemetry *                              put
   org.onap.policy.pdpx.createDictionary *                       *
   org.onap.policy.pdpx.createPolicy *                           *
   org.onap.policy.pdpx.decision  *                              *
   org.onap.policy.pdpx.getConfig *                              *
   org.onap.policy.pdpx.getConfigByPolicyName *                  *
   org.onap.policy.pdpx.getDecision *                            *
   org.onap.policy.pdpx.getDictionary *                          *
   org.onap.policy.pdpx.getMetrics *                             *
   org.onap.policy.pdpx.list      *                              *
   org.onap.policy.pdpx.listConfig *                             *
   org.onap.policy.pdpx.listPolicy *                             *
   org.onap.policy.pdpx.policyEngineImport *                     *
   org.onap.policy.pdpx.pushPolicy         *                     *
   org.onap.policy.pdpx.sendEvent *                              *
   org.onap.policy.pdpx.updateDictionary *                       *
   org.onap.policy.pdpx.updatePolicy *                           *

   List Permissions by User[demo@people.osaaf.org]
   --------------------------------------------------------------------------------
   PERM Type                      Instance                       Action
   --------------------------------------------------------------------------------
   org.onap.policy.access
   org.onap.policy.access         *                              read
   org.onap.policy.menu           menu_admin                     *
   org.onap.policy.menu           menu_ajax                      *
   org.onap.policy.menu           menu_customer                  *
   org.onap.policy.menu           menu_customer_create           *
   org.onap.policy.menu           menu_feedback                  *
   org.onap.policy.menu           menu_help                      *
   org.onap.policy.menu           menu_home                      *
   org.onap.policy.menu           menu_itracker                  *
   org.onap.policy.menu           menu_job                       *
   org.onap.policy.menu           menu_job_create                *
   org.onap.policy.menu           menu_logout                    *
   org.onap.policy.menu           menu_notes                     *
   org.onap.policy.menu           menu_process                   *
   org.onap.policy.menu           menu_profile                   *
   org.onap.policy.menu           menu_profile_create            *
   org.onap.policy.menu           menu_profile_import            *
   org.onap.policy.menu           menu_reports                   *
   org.onap.policy.menu           menu_sample                    *
   org.onap.policy.menu           menu_tab                       *
   org.onap.policy.menu           menu_test                      *
   org.onap.policy.pdpd.healthcheck *                            get
   org.onap.policy.pdpd.healthcheck.configuration *              get
   org.onap.policy.pdpd.telemetry *                              delete
   org.onap.policy.pdpd.telemetry *                              get
   org.onap.policy.pdpd.telemetry *                              post
   org.onap.policy.pdpd.telemetry *                              put
   org.onap.policy.pdpx.config    *                              *
   org.onap.policy.pdpx.createDictionary *                       *
   org.onap.policy.pdpx.createPolicy *                           *
   org.onap.policy.pdpx.decision  *                              *
   org.onap.policy.pdpx.getConfig *                              *
   org.onap.policy.pdpx.getConfigByPolicyName *                  *
   org.onap.policy.pdpx.getDecision *                            *
   org.onap.policy.pdpx.getDictionary *                          *
   org.onap.policy.pdpx.getMetrics *                             *
   org.onap.policy.pdpx.list       *                             *
   org.onap.policy.pdpx.listConfig *                             *
   org.onap.policy.pdpx.listPolicy *                             *
   org.onap.policy.pdpx.policyEngineImport *                     *
   org.onap.policy.pdpx.pushPolicy *                             *
   org.onap.policy.pdpx.sendEvent *                              *
   org.onap.policy.pdpx.updateDictionary *                       *
   org.onap.policy.pdpx.updatePolicy *                           *
   org.onap.policy.url            doclib                         *
   org.onap.policy.url            doclib_admin                   *
   org.onap.policy.url            login                          *

Disabling AAF
^^^^^^^^^^^^^

AAF is enabled by default in PDP-D installations.  Set the AAF installation variable to false to disable it.

+---------------+-------------------------+----------+---------------------------+
| Repository    | Install File            | Variable | Notes                     |
+===============+=========================+==========+===========================+
| policy/docker | config/drools/base.conf | AAF      | Heat Installation         |
+---------------+-------------------------+----------+---------------------------+
| oom           | config/drools/base.conf | AAF      | OOM Installation          |
+---------------+-------------------------+----------+---------------------------+

AAF can also be disabled at runtime within the PDP-D container by modifying the following files.

+----------------------------------------------------+-----------------------------------------+
| File                                               | Property                                |
+====================================================+=========================================+
| $POLICY_HOME/config/policy-engine.properties       | http.server.services.SECURED-CONFIG.aaf |
+----------------------------------------------------+-----------------------------------------+
| $POLICY_HOME/config/feature-healthcheck.properties | http.server.services.HEALTHCHECK.aaf    |
+----------------------------------------------------+-----------------------------------------+

After modifying these files, restart the container with "policy stop; policy start"



End of Document
