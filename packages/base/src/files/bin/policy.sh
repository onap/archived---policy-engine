###
# ============LICENSE_START=======================================================
# ECOMP Policy Engine
# ================================================================================
# Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

#!/bin/bash

function usage() {
	echo -n "syntax: $(basename $0) "
	echo -n "[--debug] "
	echo    "status|start|stop"
}

function check_r_file() {	
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} --"
		set -x
	fi

	FILE=$1
	if [[ ! -f ${FILE} || ! -r ${FILE} ]]; then
        return 1
	fi

	return 0
}

function check_x_file() {	
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} --"
		set -x
	fi

	FILE=$1
	if [[ ! -f ${FILE} || ! -x ${FILE} ]]; then
        return 1
	fi

	return 0
}

function component_status() {
	COMPONENT=$1
	output=$("${POLICY_HOME}"/etc/init.d/"${COMPONENT}" status)
	if [[ $? == 0 ]]; then 
		echo "	${COMPONENT}: UP: ${output}"
	else
		echo "	${COMPONENT}: DOWN"
	fi
}

function component_start() {
	COMPONENT=$1
	"${POLICY_HOME}"/etc/init.d/"${COMPONENT}" status > /dev/null 2>&1
	if [[ $? == 0 ]]; then 
		echo "	${COMPONENT}: UP: already running .."
	else
		"${POLICY_HOME}"/etc/init.d/"${COMPONENT}" start > /dev/null 2>&1
		echo "	${COMPONENT}: STARTING .."
	fi
}

function component_stop() {
	COMPONENT=$1
	"${POLICY_HOME}"/etc/init.d/"${COMPONENT}" status > /dev/null 2>&1
	if [[ $? != 0 ]]; then 
		echo "	${COMPONENT}: DOWN: already stopped .."
	else
		"${POLICY_HOME}"/etc/init.d/"${COMPONENT}" stop > /dev/null 2>&1
		echo "	${COMPONENT}: STOPPING .."
	fi
}

function policy_status() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} --"
		set -x
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/pap"; then
		component_status pap
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/pdp"; then
		component_status pdp
	fi

	if check_x_file "${POLICY_HOME}/etc/init.d/configs"; then
		component_status configs
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/pypdp"; then
		component_status pypdp
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/console"; then
		component_status console
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/brmsgw"; then
		component_status brmsgw
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/paplp"; then
		component_status paplp
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/pdplp"; then
		component_status pdplp
	fi

	NUM_CRONS=$(crontab -l 2> /dev/null | wc -l)
	echo "	${NUM_CRONS} cron jobs installed."
	
}

function policy_start() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} --"
		set -x
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/pap"; then
		component_start pap
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/pdp"; then
		component_start pdp
	fi

	if check_x_file "${POLICY_HOME}/etc/init.d/configs"; then
		component_start configs
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/pypdp"; then
		component_start pypdp
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/console"; then
		component_start console
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/brmsgw"; then
		component_start brmsgw
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/paplp"; then
		component_start paplp
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/pdplp"; then
		component_start pdplp
	fi
	
	cat "${POLICY_HOME}"/etc/cron.d/*.cron | crontab
}

function policy_stop() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} --"
		set -x
	fi
	
	pkill -f "/bin/bash ${POLICY_HOME}/bin/monitor.sh"
	crontab -r > /dev/null 2>&1
	sleep 2

	if check_x_file "${POLICY_HOME}/etc/init.d/paplp"; then
		component_stop paplp
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/pdplp"; then
		component_stop pdplp
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/brmsgw"; then
		component_stop brmsgw
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/console"; then
		component_stop console
	fi
		
	if check_x_file "${POLICY_HOME}/etc/init.d/pypdp"; then
		component_stop pypdp
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/configs"; then
		component_stop configs
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/pdp"; then
		component_stop pdp
	fi
	
	if check_x_file "${POLICY_HOME}/etc/init.d/pap"; then
		component_stop pap
	fi
	
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
		-i|--status|status) 		OPERATION=status
									;;
		-s|--start|start)			OPERATION=start
									;;
		-h|--stop|stop|--halt|halt)	OPERATION=halt
									;;
		*)							usage
									exit 1
									;;
	esac
	shift
done

# operation validation
case $OPERATION in
	status)	;;
	start)	;;
	halt)	;;
	*)		echo "invalid operation \(${OPERATION}\): must be in {status|start|stop}";
			usage
			exit 1
			;;
esac

if [[ -z ${POLICY_HOME} ]]; then
	echo "error: POLICY_HOME is unset."
	exit 1
fi

# operation validation
case $OPERATION in
	status)	
		policy_status
		;;
	start)	
		policy_start
		;;
	halt)	
		policy_stop
		;;
	*)		echo "invalid operation \(${OPERATION}\): must be in {status|start|stop}";
			usage
			exit 1
			;;
esac
