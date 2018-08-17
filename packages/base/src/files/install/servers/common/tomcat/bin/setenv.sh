#!/usr/bin/env bash
###
# ============LICENSE_START=======================================================
# ONAP Policy Engine
# ================================================================================
# Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
# ================================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ============LICENSE_END=========================================================
###

CATALINA_OPTS="${CATALINA_OPTS} -Dcom.sun.management.jmxremote"
CATALINA_OPTS="${CATALINA_OPTS} -Dcom.sun.management.jmxremote.port=${{TOMCAT_JMX_PORT}}"
CATALINA_OPTS="${CATALINA_OPTS} -Dcom.sun.management.jmxremote.ssl=false" 
CATALINA_OPTS="${CATALINA_OPTS} -Dcom.sun.management.jmxremote.authenticate=false"
CATALINA_OPTS="${CATALINA_OPTS} -Djavax.net.ssl.keyStore=${POLICY_HOME}/etc/ssl/policy-keystore"
CATALINA_OPTS="${CATALINA_OPTS} -Djavax.net.ssl.keyStorePassword=${KEYSTORE_PASSWD}"
CATALINA_OPTS="${CATALINA_OPTS} -Djavax.net.ssl.trustStore=${POLICY_HOME}/etc/ssl/policy-truststore"
CATALINA_OPTS="${CATALINA_OPTS} -Djavax.net.ssl.trustStorePassword=${TRUSTSTORE_PASSWD}"
CATALINA_OPTS="${CATALINA_OPTS} -DPOLICY_LOGS=${POLICY_LOGS}"
CATALINA_OPTS="${CATALINA_OPTS} -Xms${{TOMCAT_X_MS_MB}}M"
CATALINA_OPTS="${CATALINA_OPTS} -Xmx${{TOMCAT_X_MX_MB}}M"
export CATALINA_OPTS
