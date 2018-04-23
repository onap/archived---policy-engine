
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

***************
Policy Cookbook
***************

Policy VM/Docker Recipes
^^^^^^^^^^^^^^^^^^^^^^^^

    .. code-block:: bash
       :caption: Get latest images in an already setup policy VM
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
       :caption: Access the PDP-D container as a policy user
       :linenos:

        docker exec -it drools bash


    .. code-block:: bash
       :caption: Access the PDP-X container as a policy user
       :linenos:

        docker exec -it -u 0 pdp su - policy


    .. code-block:: bash
       :caption: Access the BRMSGW container as a policy user
       :linenos:

        docker exec -it -u 0 brmsgw su - policy


    .. code-block:: bash
       :caption: Access PAP container as a policy user
       :linenos:

        docker exec -it -u 0 pap su - policy


    .. code-block:: bash
       :caption: Access the CONSOLE container as a policy user
       :linenos:

        docker exec -it -u 0 console su - policy


    .. code-block:: bash
       :caption: Manual Healthcheck invokation
       :linenos:

        # Assuming the healthcheck service credentials have not been changed
        # post-installation within the drools container
         
        source /opt/app/policy/config/feature-healthcheck.conf.environment
        curl --silent --user "${HEALTHCHECK_USER}:${HEALTHCHECK_PASSWORD}" 
             -X GET http://localhost:6969/healthcheck | python -m json.tool


PDP-D Recipes ("drools" container)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

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

        # Assuming the healthcheck service credentials have not been changed
        # post-installation within the drools container
 
        source ${POLICY_HOME}/config/feature-healthcheck.conf
        curl --silent --user "${HEALTHCHECK_USER}:${HEALTHCHECK_PASSWORD}" 
             -X GET http://localhost:6969/healthcheck | python -m json.tool

End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Policy+Cookbook



