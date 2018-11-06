
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

***************
Policy Cookbook
***************

Openstack Heat Installation - Policy VM/Docker Recipes
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

    .. code-block:: bash
       :caption: Get the latest images in an already setup policy VM
       :linenos:

        /opt/policy_vm_init.sh


    .. code-block:: bash
       :caption: Install/start docker policy containers with no policies preloaded
       :linenos:

        echo "PRELOAD_POLICIES=false" > /opt/policy/.env
        /opt/policy_vm_init.sh


    .. code-block:: bash
       :caption: Install/start docker policy containers with policies preloaded
       :linenos:

        # This is the current default mode of instantiation.
        # These operations are unnecessary unless PRELOAD_POLICIES
        # was previously set to true

        echo "PRELOAD_POLICIES=true" > /opt/policy/.env
        /opt/policy_vm_init.sh


    .. code-block:: bash
       :caption: Access the PDP-D container as the policy user
       :linenos:

        docker exec -it drools bash


    .. code-block:: bash
       :caption: Access the PDP-X container as the policy user
       :linenos:

        docker exec -it -u 0 pdp su - policy


    .. code-block:: bash
       :caption: Access the BRMSGW container as the policy user
       :linenos:

        docker exec -it -u 0 brmsgw su - policy


    .. code-block:: bash
       :caption: Access PAP container as the policy user
       :linenos:

        docker exec -it -u 0 pap su - policy


    .. code-block:: bash
       :caption: Access the CONSOLE container the a policy user
       :linenos:

        docker exec -it -u 0 console su - policy


    .. code-block:: bash
       :caption: Command line Healthcheck invokation
       :linenos:

        source /opt/app/policy/config/feature-healthcheck.conf.environment
        curl --silent --user "${HEALTHCHECK_USER}:${HEALTHCHECK_PASSWORD}"
             -X GET https://localhost:6969/healthcheck | python -m json.tool


OOM Installation - Policy Kubernetes Recipes
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

    .. code-block:: bash
       :caption: List the policy pods
       :linenos:

        kubectl get pods -n onap -o wide | grep policy


    .. code-block:: bash
       :caption: Access the PAP container
       :linenos:

        kubectl exec -it <pap-pod> -c pap -n onap bash


    .. code-block:: bash
       :caption: Access a PDPD-D container
       :linenos:

        # <policy-deployment-prefix> depends on the deployment configuration

        kubectl exec -it <policy-deployment-prefix>-drools-0 -c drools -n onap bash


    .. code-block:: bash
       :caption: Access the PDP container
       :linenos:

        # <policy-deployment-prefix> depends on the deployment configuration

        kubectl exec -it <policy-deployment-prefix>-pdp-0 -c drools -n onap bash


    .. code-block:: bash
       :caption: Push Default Policies
       :linenos:

        kubectl exec -it <pap-pod> -c pap -n onap -- bash -c "export PRELOAD_POLICIES=true; /tmp/policy-install/config/push-policies.sh"


    .. code-block:: bash
       :caption: Standalone Policy Web UI URL access
       :linenos:

        http://<pap-vm>:30219/onap/login.htm


PDP-D Recipes (inside the "drools" container)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

    .. code-block:: bash
       :caption: Stop the PDP-D
       :linenos:

        policy stop


    .. code-block:: bash
       :caption: Start the PDP-D
       :linenos:

        policy start


    .. code-block:: bash
       :caption: Manual Healthcheck Invokation
       :linenos:

        source ${POLICY_HOME}/config/feature-healthcheck.conf
        curl --silent --user "${HEALTHCHECK_USER}:${HEALTHCHECK_PASSWORD}"
             -X GET https://localhost:6969/healthcheck | python -m json.tool


    .. code-block:: bash
       :caption: Start a telemetry shell
       :linenos:

        telemetry


    .. code-block:: bash
       :caption: See all the configured loggers
       :linenos:

       curl -k --silent --user "${ENGINE_MANAGEMENT_USER}:${ENGINE_MANAGEMENT_PASSWORD}"
            https://localhost:9696/policy/pdp/engine/tools/loggers


    .. code-block:: bash
       :caption: See the logging level for a given logger (for example the network logger):
       :linenos:

       curl -k --silent --user"${ENGINE_MANAGEMENT_USER}:${ENGINE_MANAGEMENT_PASSWORD}"
            https://localhost:9696/policy/pdp/engine/tools/loggers/network


    .. code-block:: bash
       :caption: Modify the logging level for a given logger (for example the network logger):
       :linenos:

       curl -k --silent --user"${ENGINE_MANAGEMENT_USER}:${ENGINE_MANAGEMENT_PASSWORD}"
            -X PUT https://localhost:9696/policy/pdp/engine/tools/loggers/network/WARN


PAP Recipes (inside the "pap" container)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

    .. code-block:: bash
       :caption: Bypass Portal Authentication with the Policy Web UI
       :linenos:

        edit: /opt/app/policy/servers/console/webapps/onap/WEB-INF/classes/portal.properties
        comment out: #role_access_centralized = remote
        restart pap: policy.sh stop; policy.sh start;


    .. code-block:: bash
       :caption: Access the Policy Web UI without going through the Portal UI
       :linenos:

       https://<pap-vm>:8443/onap/login.htm  (Heat)
       https://<pap-vm>:30219/onap/login.htm  (Kubernetes))


End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Policy+Cookbook



