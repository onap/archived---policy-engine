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

export POLICY_HOME=${{POLICY_HOME}}
export POLICY_USER=${{POLICY_USER}}
export POLICY_GROUP=${{POLICY_GROUP}}
export POLICY_LOGS=${{POLICY_LOGS}}
export KEYSTORE_PASSWD=${{KEYSTORE_PASSWD}}
export TRUSTSTORE_PASSWD=${{TRUSTSTORE_PASSWD}}

export JAVA_HOME=${{JAVA_HOME}}
export PATH=${PATH}:${{POLICY_HOME}}/bin
