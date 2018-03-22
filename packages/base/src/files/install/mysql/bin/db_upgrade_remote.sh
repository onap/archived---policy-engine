#!/bin/bash 
###
# ============LICENSE_START=======================================================
# ONAP Policy Engine
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
#
# db_upgrade_remote.sh: This script is to perform database schema upgrade on remote db, 
#                       no shecma downgrade will be performed in case db_version is higher then target_version
# 
# Logic: 1. Get target schema version from db scripts in $POLICY_HOME/data/mysql
#        2. Get current db schema version from support.db_version table (of target system)
#        3. Apply db upgrade script in order if target_version is HIGHER than db_version
#        4. Print out warning message        if target_version is LOWER than db_version
#        4. Print out message                if target_version is EQUAL to db_version
#
#
# Usage  : db_upgrade_remote.sh db_user_id  db_user_password hostname
# Example: db_upgrade_remote.sh policy_user password         localhost.com
#
# Assumption: 1. DB schema upgrade script in $POLICY_HOME/data/mysql folder with read permission
#             2. DB user has privilege to create/drop/alter database table
#
# Note: The default location for db schema upgrade script is $POLICY_HOME/data/mysql
#       The release level is represented as Two-digit-Year+Two-digit-Month+two-digit-Sub-release (151000, 151001)
#
#

TARGET_SCHEMA_VERSION=""
CURRENT_SCHEMA_VERSION=""
DB_UPGRADE_USER=""
DB_UPGRADE_PASSWORD=""
DB_HOSTNAME=""
DB_UPGRADE_DIR=$POLICY_HOME/data/mysql
DATE=`date +"%Y%m%d%H%M%S"`
LOG=""
ERR=""

function get_current_schema_version
{
  echo "Get current schema version from [${DB_HOSTNAME}] started ...@`date`" | tee -a $LOG
  # display output vertical
  query="select version from support.db_version where the_key = 'VERSION' \G"
  CURRENT_SCHEMA_VERSION=`${MYSQL} --skip-column-names --execute "${query}" 2>$ERR | grep -v "*"`
  error_msg=`cat $ERR | grep "doesn't exist"`
  if [ "${error_msg}" != "" ]; then 
    echo "Create support.db_version table ..." | tee -a $LOG
    sql="create database if not exists support;"
    ${MYSQL} --execute "${sql}"
    sql="create table support.db_version(the_key varchar(20) not null, version varchar(20), primary key(the_key));"
    ${MYSQL} --execute "${sql}" 
    CURRENT_SCHEMA_VERSION="00"
  fi
  echo "CURRENT_SCHEMA_VERSION: [$CURRENT_SCHEMA_VERSION]" | tee -a $LOG
  echo "Get current schema version from [${DB_HOSTNAME}] completed ...@`date`" | tee -a $LOG
}

function get_target_schema_version
{
  UPGRADE_LIST=/tmp/db_upgrade_list.$$
  find ${DB_UPGRADE_DIR} -name "*_upgrade_script.sql" 2>/dev/null | grep -v "droolspdp" | sort -r | head -1 > $UPGRADE_LIST
  while read -r file
  do
    TARGET_SCHEMA_VERSION=`basename $file | cut -d'_' -f1`
    echo "TARGET_SCHEMA_VERSION: [$TARGET_SCHEMA_VERSION]" | tee -a $LOG
    break
  done < $UPGRADE_LIST
  rm -f $UPGRADE_LIST
}

function evaluate_upgrade_downgrade
{
  echo "CURRENT_SCHEMA_VERSION --> [$CURRENT_SCHEMA_VERSION]" | tee -a $LOG
  echo "TARGET_SCHEMA_VERSION  --> [$TARGET_SCHEMA_VERSION] " | tee -a $LOG
  if [[ "${CURRENT_SCHEMA_VERSION}" < "${TARGET_SCHEMA_VERSION}" ]]; then 
    # perform db upgrade
    UPGRADE_LIST=/tmp/db_upgrade_list.$$
    find ${DB_UPGRADE_DIR} -name "*_upgrade_script.sql" 2>/dev/null | grep -v "droolspdp" | sort > $UPGRADE_LIST
    while read -r file
    do
      DB_VERSION=`basename $file | cut -d'_' -f1`
      #echo "[$DB_VERSION] [$TARGET_SCHEMA_VERSION]" | tee -a $LOG
      if [ "${DB_VERSION}" -gt "${CURRENT_SCHEMA_VERSION}" ] && [ "${DB_VERSION}" -le "${TARGET_SCHEMA_VERSION}" ]; then
        run_script "UPGRADE" "${file}" 2>&1 | tee -a $LOG
      fi
    done < $UPGRADE_LIST
    rm -f $UPGRADE_LIST
    set_current_release_level $TARGET_SCHEMA_VERSION
  elif [[ "${CURRENT_SCHEMA_VERSION}" > "${TARGET_SCHEMA_VERSION}" ]]; then 
    # db downgrade
    echo "WARNING: Target db schema version is LOWER than current db scema version, please run downgrade script manually." | tee -a $LOG | tee -a $ERR
  else
    echo "CURRENT SCHEMA VERSION THE SAME AS TARGET SCHEMA VERSION, NO ACTION TAKEN ..." | tee -a $LOG
  fi
}

function run_script
{
  action="${1}"
  script="${2}"
  echo "Perform DB $action on [${DB_HOSTNAME}] use $script ..." | tee -a $LOG
  echo "--" | tee -a $LOG
  ${MYSQL} --verbose < "${script}" 2>$ERR | tee -a $LOG
  echo "--" | tee -a $LOG
}

function set_current_release_level
{
  DB_VERSION="${1}"
  echo "Set current release level on [${DB_HOSTNAME}] to [$DB_VERSION] started ...@`date`" | tee -a $LOG
  update_statement="insert into support.db_version (the_key, version) values ('VERSION', '${DB_VERSION}') on duplicate key update version='${DB_VERSION}';"
  ${MYSQL} --execute "${update_statement}" 

  echo "" | tee -a $LOG
  echo "CURRENT_SCHEMA_VERSION set to: [$DB_VERSION]" | tee -a $LOG
  echo "" | tee -a $LOG
  echo "Set current release level on [${DB_HOSTNAME}] to [$DB_VERSION] completed ...@`date`" | tee -a $LOG
}

function check_directory
{
  if [ ! -d $DB_UPGRADE_DIR ]; then
    echo "ERROR, DIRECTORY NOT EXIST: $DB_UPGRADE_DIR, PROCESS EXIT ..."
    exit;
  else
    if [ ! -d $DB_UPGRADE_DIR/logs ]; then
      mkdir $DB_UPGRADE_DIR/logs
    fi
  fi
}

# MAIN
#check_directory
if [ -z ${POLICY_LOGS} ]; then
  POLICY_LOGS=/var/log
fi
mkdir -p $POLICY_LOGS/ONAP/db
LOG=$POLICY_LOGS/ONAP/db/db_upgrade_remote_$DATE.log
ERR=$POLICY_LOGS/ONAP/db/db_upgrade_remote_$DATE.err
echo "db_upgrade_remote.sh started ..." | tee -a $LOG
if [ $# -eq 3 ]; then 
  DB_UPGRADE_USER="${1}"
  DB_UPGRADE_PASSWORD="${2}"
  DB_HOSTNAME="${3}"
  echo "DB_UPGRADE_USER: $DB_UPGRADE_USER" | tee -a $LOG
  echo "DB_UPGRADE_DIR : $DB_UPGRADE_DIR"  | tee -a $LOG
  echo "DB_HOSTNAME    : $DB_HOSTNAME"     | tee -a $LOG
  #
  typeset -r MYSQL="mysql -u${DB_UPGRADE_USER} -p${DB_UPGRADE_PASSWORD} -h ${DB_HOSTNAME}";
  get_target_schema_version
  if [ ${#TARGET_SCHEMA_VERSION} -ne 6 ]; then 
    echo "ERROR, TARGET_SCHEMA_VERSION MUST BE 6 DIGITS: $TARGET_SCHEMA_VERSION" | tee -a $LOG | tee -a $ERR
  else
    get_current_schema_version
    evaluate_upgrade_downgrade
  fi
else
  echo "Usage  : db_upgrade_remote.sh db_user_id   db_user_password db_hostname" | tee -a $LOG
  echo "Example: db_upgrade_remote.sh policy_user  password         localhost.com" | tee -a $LOG
fi

echo "db_upgrade_remote.sh completed ..." | tee -a $LOG
