#!/bin/bash
#
#============LICENSE_START==================================================
#  ONAP Policy Engine
#===========================================================================
#  Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
#===========================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#============LICENSE_END==================================================
#


ELK_MAP_SUCCESS_RE="\"acknowledged\": *true"

function usage() {
	echo -n "syntax: $(basename $0) "
	echo -n "[--debug] "
	echo -n "([--audit] |"	
}

function log() {
	echo "$(date +"%Y-%m-%d_%H-%M-%S") $1" >> ${{POLICY_LOGS}}/policy/elk.log
	echo "$1"
}

function delete_index() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	COMMAND="curl --silent -X DELETE http://localhost:9200/policy"
	OUTPUT="$(${COMMAND} 2>&1)"
	RC=$?
	
	log "${RC}: ${COMMAND}"
	log "${OUTPUT}"
	
	if [[ ${RC} != 0 ]] || [[ ! ${OUTPUT} =~ ${ELK_MAP_SUCCESS_RE} ]]; then
		log "WARNING: curl: delete /policy: ${RC}"
		return 1
	fi
	
	log "OK: curl: delete /policy: ${OUTPUT}"
	return 0
}

function create_index() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	COMMAND="curl --silent -X PUT http://localhost:9200/policy"
	OUTPUT="$(${COMMAND} 2>&1)"
	RC=$?
	
	log "${RC}: ${COMMAND}"
	log "${OUTPUT}"
	
	if [[ ${RC} != 0 ]] || [[ ! ${OUTPUT} =~ ${ELK_MAP_SUCCESS_RE} ]]; then
		log "ERROR: curl: put /policy: ${RC}"
		return 1
	fi
		
	log "OK: curl: put /policy."
	return 0
}

function check_elk_status() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
		
	${POLICY_HOME}/etc/init.d/elk status
	if [[ $? != 0 ]]; then
		log "ERROR: elk is down.   Aborting .."
		exit 1
	fi
}

function check_elk_policy_index() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	COMMAND="curl --silent -X GET http://localhost:9200/policy"
	OUTPUT="$(${COMMAND} 2>&1)"
	RC=$?
	
	log "${RC}: ${COMMAND}"
	
	if [[ ${RC} != 0 ]] || [[ ! ${OUTPUT} =~ policy ]]; then
		log "ERROR: curl: get /policy: ${RC}"
		return 1
	fi
		
	log "OK: curl: get /policy."
	return 0		
}

#The Script will update the policy data on querying from database as a bulk to Elastic Database
function audit() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	check_elk_status
	
	if ! check_elk_policy_index; then
		echo "policy index does not exist. So, Policy Index is created."
		if ! create_index; then
			echo "abort: policy index creation failed."
			exit 1
		fi
	fi
	
	$JAVA_HOME/bin/java -cp $POLICY_HOME/servers/pap/webapps/pap/WEB-INF/lib/ONAP-PAP-REST-*.jar:$POLICY_HOME/servers/pap/webapps/pap/WEB-INF/lib/*: -DPROPERTY_FILE=$POLICY_HOME/servers/pap/bin/policyelk.properties org.onap.policy.pap.xacml.rest.elk.client.ElasticSearchPolicyUpdate
}

#########################################################################
##
## script execution body
##
#########################################################################

DEBUG=n
OPERATION=none

until [[ -z "$1" ]]; do
	case $1 in
		-d|--debug|debug) 	DEBUG=y
							set -x
							;;
		-a|--audit|audit) 	OPERATION=audit
							;;																	
		*)	usage
			exit 1
			;;
	esac
	shift
done

# operation validation
case $OPERATION in
	audit)	;;
	*)		echo "invalid operation (${OPERATION}).";
			usage
			exit 1
			;;
esac

if [[ -z ${POLICY_HOME} ]]; then
	echo "error: POLICY_HOME is unset."
	exit 1
fi

log "**** $OPERATION ****"

if pidof -o %PPID -x $(basename $0) > /dev/null 2>&1; then
	echo "WARNING: an $(basename $0) process is already running.  Exiting."
	exit 1
fi

case $OPERATION in
	audit)	
		audit
		;;		
	*)	echo "invalid operation (${OPERATION}).";
		usage
		exit 1
		;;
esac