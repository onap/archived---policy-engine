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

#########################################################################
##
## Functions
##
#########################################################################

function usage() {
	echo -n "syntax: $(basename $0) "
	echo -n "--debug ("
	echo -n "[--backup <backup-dir-location>] | "
	echo -n "[--restore <backup-dir-location>])"
}

function backup() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	if [[ -z ${POLICY_HOME} ]]; then
		echo "error: ${POLICY_HOME} is not set"
		exit 1
	fi
	
	BACKUP_DIR=$1	
	if [[ -z ${BACKUP_DIR} ]]; then
		echo "error: a backup directory must be provided"
		usage
		exit 1
	fi
	
	/bin/mkdir -p ${BACKUP_DIR} > /dev/null 2>&1
	if [[ ! -d ${BACKUP_DIR} ]]; then
		echo "error: ${BACKUP_DIR} is not a directory"
		exit 1
	fi	
	
	if [[ ! -w ${BACKUP_DIR} ]] ; then
		echo "error: ${BACKUP_DIR} is not writable"
		exit 1	
	fi
	
	if [ "$(ls -A ${BACKUP_DIR})" ]; then
		echo "error: ${BACKUP_DIR} must be empty"
		exit 1
	fi
	
	echo "backing up ${POLICY_HOME} to ${BACKUP_DIR} to.."
	rsync -a --delete \
			--exclude logs \
			--exclude tmp \
			--exclude backup \
			--exclude servers/pap/webapps/pap \
			--exclude servers/pdp/webapps/pdp \
			--exclude servers/pypdp/webapps/PyPDPServer \
			--exclude servers/console/webapps/policy \
			${POLICY_HOME}/* \
			${BACKUP_DIR}
}

function restore() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	if [[ -z ${POLICY_HOME} ]]; then
		echo "error: ${POLICY_HOME} is not set"
		exit 1
	fi
	
	BACKUP_DIR=$1	
	if [[ -z ${BACKUP_DIR} ]]; then
		echo "error: a backup directory must be provided"
		usage
		exit 1
	fi
	
	if [[ ! -d ${BACKUP_DIR} ]]; then
		echo "error: ${BACKUP_DIR} is not a directory"
		exit 1
	fi	
	
	if [ "$(ls -A ${BACKUP_DIR})" ]; then
		echo "OK: ${BACKUP_DIR} has content"
	else
		echo "error: ${BACKUP_DIR} is empty"
		exit 1
	fi
	
	echo "restoring from ${BACKUP_DIR} to ${POLICY_HOME} .."
	rsync -a ${BACKUP_DIR}/* ${POLICY_HOME}
}

OPERATION=none
DEBUG=n

# command line options parsing
until [[ -z "$1" ]]; do
	case $1 in
		-d|--debug) 	DEBUG=y
						set -x
						;;
		-b|--backup) 	OPERATION=backup
						shift
						DIR=$1		
						;;
		-r|--restore) 	OPERATION=restore
						shift
						DIR=$1
						;;						
		*)				usage
						exit 1
						;;
	esac
	shift
done

# operation validation
case $OPERATION in
	backup) 	backup $DIR
				;;
	restore) 	restore $DIR
				;;
	*)		echo "invalid operation (${OPERATION}): must be in {backup|restore}";
			usage
			exit 1
			;;
esac