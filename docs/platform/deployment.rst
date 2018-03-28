.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

*****************
Policy Deployment
*****************

.. contents::
    :depth: 3

Policy Helm Charts
^^^^^^^^^^^^^^^^^^
The K8S helm charts in ONAP are hierarchical, where the chart of a container contains the charts of dependent containers. The policy helm chart captures the K8S configurations of PAP, PDP-X, Drools, BRMSGW, and nexus containers as shown below.


.. code-block:: bash
   :caption: Policy Helm Chart Structure
   :linenos:

    |-- Chart.yaml              # Captures the meta-information of the PAP chart. For example, name and namespace.
    |-- templates               # Contains templates for the PAP deployment descriptors and PAP secrets.
    |   |-- NOTES.txt           
    |   |-- configmap.yaml      # The configuration-map of the PAP application running in a POD.
    |   |-- deployment.yaml     # The deployment descriptor of the PAP POD.
    |   |-- secrets.yaml        # Captures secret keys related to authentication.
    |   |-- service.yaml        # The deployment descriptor of offered services by PAP.
    |-- values.yaml             # The configurations of the deployment descriptor and secret templates
    |-- requirements.yaml       # Captures information about dependent charts; which is policy-common in this scenario.
    |-- resources               # Captures resources required for deploying helm charts.
    |   |-- config              # Captures the configurations of the PAP application.
    |   |   |-- opt
    |   |   |   |-- policy
    |   |   |       |-- config
    |   |   |           |-- pe
    |   |   |-- pe
    |   |       |-- console.conf            # The configurations of a backend server used in PAP.
    |   |       |-- elk.conf                # The configurations of the elastic search module.
    |   |       |-- mysql.conf              # The Configurations of the mysql database.
    |   |       |-- pap-tweaks.sh           
    |   |       |-- pap.conf                # The configurations of the PAP application.
    |   |       |-- paplp.conf              # The configurations of a JVM used by PAP.
    |   |       |-- push-policies.sh        
    |   |-- scripts                         # Captures scripts used by a container.
    |       |-- do-start.sh                 # The startup script of the PAP container.  
    |       |-- update-vfw-op-policy.sh     
    |-- charts                              # The charts of dependent containers; brmsgw, drools, mariadb, pdp, policy-nexus, and policy-common
    |   |-- brmsgw                          # The helm chart for BRMSGW
    |   |   |-- Chart.yaml                  # Captures the meta-information of the brmsgw chart. For example, name and namespace.
    |   |   |-- requirements.yaml           # Captures information about dependent charts; which is policy-common in this scenario.
    |   |   |-- resources                   # Captures resources required for deploying helm charts.
    |   |   |   |-- config
    |   |   |       |-- pe
    |   |   |           |-- brmsgw-tweaks.sh    
    |   |   |           |-- brmsgw.conf         # The configurations of the brmsgw application.
    |   |   |-- templates                   # Contains templates for the brmsgw deployment descriptors and brmsgw secrets.
    |   |   |   |-- NOTES.txt               
    |   |   |   |-- configmap.yaml          # The configuration-map of the brmsgw application running in a POD.
    |   |   |   |-- deployment.yaml         # The deployment descriptor of the brmsgw POD.
    |   |   |   |-- service.yaml            # The deployment descriptor of offered services by brmsgw.
    |   |   |-- values.yaml                 # The configurations of the deployment descriptor templates
    |   |-- drools                          # The helm chart for drools
    |   |   |-- Chart.yaml                  # Captures the meta-information of the brmsgw chart. For example, name and namespace.
    |   |   |-- charts                      # The charts of nexus containers
    |   |   |   |-- nexus                   # The helm chart for nexus
    |   |   |       |-- Chart.yaml          # Captures the meta-information of the nexus chart. For example, name and namespace.
    |   |   |       |-- requirements.yaml   # Captures information about dependent charts; which is policy-common in this scenario.
    |   |   |       |-- templates           # Contains templates for the nexus deployment descriptors and brmsgw secrets.
    |   |   |       |   |-- NOTES.txt       
    |   |   |       |   |-- deployment.yaml # The deployment descriptor of the nexus POD.
    |   |   |       |   |-- service.yaml    # The deployment descriptor of offered services by nexus.
    |   |   |       |-- values.yaml         # The configurations of the deployment descriptor templates
    |   |   |-- requirements.yaml           # Captures information about dependent charts; which is policy-common in this scenario.
    |   |   |-- resources                   # Captures resources required for deploying helm charts.
    |   |   |   |-- config
    |   |   |   |   |-- drools
    |   |   |   |   |   |-- settings.xml    # The maven settings file for the Drools application.
    |   |   |   |   |-- log
    |   |   |   |   |   |-- drools
    |   |   |   |   |       |-- logback.xml # The maven settings file for logging.
    |   |   |   |   |-- opt
    |   |   |   |       |-- policy
    |   |   |   |           |-- config
    |   |   |   |               |-- drools
    |   |   |   |                   |-- base.conf                       # The base configurations for drools 
    |   |   |   |                   |-- drools-tweaks.sh                
    |   |   |   |                   |-- keys
    |   |   |   |                   |   |-- feature-healthcheck.conf    # The configurations for the helth-check module.
    |   |   |   |                   |   |-- policy-keystore             
    |   |   |   |                   |-- policy-management.conf          # The configurations for the policy management module.
    |   |   |   |-- scripts
    |   |   |       |-- do-start.sh                 # The startup script of the drools container.
    |   |   |       |-- update-vfw-op-policy.sh     
    |   |   |-- templates                           # Contains templates for the drools deployment descriptors and secrets.
    |   |   |   |-- NOTES.txt                       
    |   |   |   |-- configmap.yaml                  # The configuration-map of the drools application running in a POD.
    |   |   |   |-- deployment.yaml                 # The deployment descriptor of the drools POD.
    |   |   |   |-- secrets.yaml                    # Captures secret keys related to authentication.
    |   |   |   |-- service.yaml                    # The deployment descriptor of offered services by drools.
    |   |   |-- values.yaml                         # The configurations of the deployment descriptor templates.
    |   |-- mariadb                                 # The helm chart for mariadb
    |   |   |-- Chart.yaml                          # Captures the meta-information of the mariadb chart. For example, name and namespace.
    |   |   |-- requirements.yaml                   # Captures information about dependent charts; which is policy-common in this scenario.
    |   |   |-- resources                           # Captures resources required for deploying helm charts.
    |   |   |   |-- config
    |   |   |       |-- db.sh                       # Startup script for mariadb.
    |   |   |-- templates                           # Contains templates for the mariadb deployment descriptors and secrets.
    |   |   |   |-- NOTES.txt                       
    |   |   |   |-- configmap.yaml                  # The configuration-map of the mariadb application running in a POD.
    |   |   |   |-- deployment.yaml                 # The deployment descriptor of the mariadb POD.
    |   |   |   |-- pv.yaml                         # The persistence volume configurations.
    |   |   |   |-- pvc.yaml                        # The persistence volume claim configurations.
    |   |   |   |-- secrets.yaml                    # Captures secret keys related to authentication.
    |   |   |   |-- service.yaml                    # The deployment descriptor of offered services by maridb.
    |   |   |-- values.yaml                         # The configurations of the deployment descriptor templates.
    |   |-- pdp                                     # The helm chart for pdp.
    |   |   |-- Chart.yaml                          # Captures the meta-information of the pdp chart. For example, name and namespace.
    |   |   |-- requirements.yaml                   # Captures information about dependent charts; which is policy-common in this scenario.
    |   |   |-- resources                           # Captures resources required for deploying helm charts.
    |   |   |   |-- config
    |   |   |       |-- log
    |   |   |       |   |-- xacml-pdp-rest
    |   |   |       |       |-- logback.xml         # The maven settings file for logging.
    |   |   |       |-- pe
    |   |   |           |-- pdp-tweaks.sh           
    |   |   |           |-- pdp.conf                # The configurations of the pdp application.
    |   |   |           |-- pdplp.conf              # The configurations of a JVM used by pdp.
    |   |   |-- templates                           # Contains templates for the pdp deployment descriptors and secrets.
    |   |   |   |-- NOTES.txt                       
    |   |   |   |-- configmap.yaml                  # The configuration-map of the pdp application running in a POD.
    |   |   |   |-- service.yaml                    # The deployment descriptor of offered services by pdp.
    |   |   |   |-- statefulset.yaml                # The stateful-set descriptor for pdp to support multiple container instances in a POD.
    |   |   |-- values.yaml                         # The configurations of the deployment descriptor templates.
    |   |-- policy-brmsgw                           
    |   |   |-- Chart.yaml                          
    |   |   |-- requirements.yaml                   
    |   |   |-- templates                           
    |   |   |   |-- NOTES.txt
    |   |   |   |-- configmap.yaml
    |   |   |   |-- deployment.yaml
    |   |   |   |-- secrets.yaml
    |   |   |   |-- service.yaml
    |   |   |-- values.yaml                         # The configurations of the deployment descriptor templates.
    |   |-- policy-common                           # The helm chart that is commonly used across all policy containers.
    |   |   |-- Chart.yaml                          # Captures the meta-information of the policy-common chart. For example, name and namespace.
    |   |   |-- requirements.yaml                   # Captures information about dependent charts. 
    |   |   |-- resources                           # Captures resources required for deploying helm charts.
    |   |   |   |-- config
    |   |   |       |-- log
    |   |   |       |   |-- filebeat
    |   |   |       |       |-- filebeat.yml        
    |   |   |       |-- pe
    |   |   |       |   |-- base.conf               # The base configurations applicable across many of the policy containers.
    |   |   |       |   |-- brmsgw-tweaks.sh        
    |   |   |       |   |-- brmsgw.conf             # The configurations of the brmsgw application.
    |   |   |       |   |-- console.conf            # The configurations of the console application.
    |   |   |       |   |-- elk.conf                # The configurations of the elastic search application.
    |   |   |       |   |-- mysql.conf              # The configurations of the mysql application.
    |   |   |       |   |-- pap-tweaks.sh           
    |   |   |       |   |-- pap.conf                # The configurations of the pap application.
    |   |   |       |   |-- paplp.conf              # The configurations of a JVM used by PAP.
    |   |   |       |   |-- pdp-tweaks.sh           
    |   |   |       |   |-- pdp.conf                # The configurations of the pdp application.
    |   |   |       |   |-- pdplp.conf              # The configurations of a JVM used by pdp.
    |   |   |       |   |-- push-policies.sh        
    |   |   |       |-- scripts
    |   |   |           |-- do-start.sh             # The startup script of container.
    |   |   |-- templates
    |   |   |   |-- NOTES.txt                       
    |   |   |   |-- configmap.yaml                  # The configuration-map applicable across any policy container.
    |   |   |-- values.yaml                         # The configurations of the deployment descriptor templates.
    |   |-- policy-mariadb                          
    |   |   |-- Chart.yaml
    |   |   |-- requirements.yaml
    |   |   |-- resources
    |   |   |   |-- config
    |   |   |       |-- db.sh
    |   |   |-- templates
    |   |   |   |-- NOTES.txt
    |   |   |   |-- configmap.yaml
    |   |   |   |-- deployment.yaml
    |   |   |   |-- pv.yaml
    |   |   |   |-- pvc.yaml
    |   |   |   |-- secrets.yaml
    |   |   |   |-- service.yaml
    |   |   |-- values.yaml
    |   |-- policy-nexus                            
    |   |   |-- Chart.yaml
    |   |   |-- requirements.yaml
    |   |   |-- templates
    |   |   |   |-- NOTES.txt
    |   |   |   |-- deployment.yaml
    |   |   |   |-- secrets.yaml
    |   |   |   |-- service.yaml
    |   |   |-- values.yaml
    |   |-- policy-pap                              
    |   |   |-- Chart.yaml
    |   |   |-- requirements.yaml
    |   |   |-- resources
    |   |   |   |-- config
    |   |   |       |-- log
    |   |   |           |-- ep_sdk_app
    |   |   |           |   |-- logback.xml
    |   |   |           |-- xacml-pap-rest
    |   |   |               |-- logback.xml
    |   |   |-- templates
    |   |   |   |-- NOTES.txt
    |   |   |   |-- configmap.yaml
    |   |   |   |-- deployment.yaml
    |   |   |   |-- secrets.yaml
    |   |   |   |-- service.yaml
    |   |   |-- values.yaml
    |   |-- policy-pdp                              
    |       |-- Chart.yaml
    |       |-- requirements.yaml
    |       |-- resources
    |       |   |-- config
    |       |       |-- log
    |       |           |-- xacml-pdp-rest
    |       |               |-- logback.xml
    |       |-- templates
    |       |   |-- NOTES.txt
    |       |   |-- configmap.yaml
    |       |   |-- secrets.yaml
    |       |   |-- service.yaml
    |       |   |-- statefulset.yaml
    |       |-- values.yaml



.. _PolicyConfigOfK8s : https://wiki.onap.org/display/DW/Policy+configuration+of+K8S



End of Document

