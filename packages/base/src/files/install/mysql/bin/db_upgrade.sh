#!/bin/bash
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
# db_upgrade.sh: Run this script to upgrade database to a given release level, it is recommanded switch to policy user to run this script
# 
# Usage  : db_upgrade.sh target_db_release_level db_user_id  db_user_password
# Example: db_upgrade.sh 151000                  policy_user password
#
# Assumption: 1. DB upgrade sql script in $POLICY_HOME/data/mysql folder with read permission
#             2. DB user has privilege to create/drop/alter database table
#
# Note: The default location for db release script is $POLICY_HOME/data/mysql
#       The release level is represented as Two-digit-Year+Two-digit-Month+two-digit-Sub-release (151000, 151001)
#
#

TARGET_RELEASE=""
CURRENT_RELEASE=""
DB_UPGRADE_USER=""
DB_UPGRADE_PASSWORD=""
DB_UPGRADE_DIR=$POLICY_HOME/data/mysql
DATE=`date +"%Y%m%d%H%M%S"`
LOG=""
ERR=""

function get_current_release_level
{
  echo "Get current release level started ...@`date`" | tee -a $LOG
  # display output vertical
  query="select version from support.db_version where the_key = 'VERSION' \G"
  CURRENT_RELEASE=`${MYSQL} --skip-column-names --execute "${query}" 2>$ERR | grep -v "*"`
  echo "CURRENT_RELEASE: [$CURRENT_RELEASE]" | tee -a $LOG
  echo "Get current release level completed ...@`date`" | tee -a $LOG
}

function evaluate_upgrade_downgrade
{
  echo "CURRENT_RELEASE --> [$CURRENT_RELEASE]" | tee -a $LOG
  echo "TARGET_RELEASE  --> [$TARGET_RELEASE] " | tee -a $LOG
  if [[ "${CURRENT_RELEASE}" < "${TARGET_RELEASE}" ]]; then 
    # perform db upgrade
    UPGRADE_LIST=/tmp/db_upgrade_list.$$
    find ${DB_UPGRADE_DIR} -name "*_upgrade_script.sql" 2>/dev/null | grep -v "droolspdp" | sort > $UPGRADE_LIST
    while read -r file
    do
      RELEASE=`basename $file | cut -d'_' -f1`
      #echo "[$RELEASE] [$TARGET_RELEASE]" | tee -a $LOG
      if [ "${RELEASE}" -gt "${CURRENT_RELEASE}" ] && [ "${RELEASE}" -le "${TARGET_RELEASE}" ]; then
        run_script "UPGRADE" "${file}" 2>&1 | tee -a $LOG
      fi
    done < $UPGRADE_LIST
    rm -f $UPGRADE_LIST
    set_current_release_level $TARGET_RELEASE
  elif [[ "${CURRENT_RELEASE}" > "${TARGET_RELEASE}" ]]; then 
    # perform db downgrade
    DOWNGRADE_LIST=/tmp/db_downgrade_list.$$
    find ${DB_UPGRADE_DIR} -name "*_downgrade_script.sql" 2>/dev/null | grep -v "droolspdp" | sort -r > $DOWNGRADE_LIST
    while read -r file
    do
      RELEASE=`basename $file | cut -d'_' -f1`
      #echo "[$RELEASE] [$TARGET_RELEASE]" | tee -a $LOG
      if [ "${RELEASE}" -le "${CURRENT_RELEASE}" ] && [ "${RELEASE}" -gt "${TARGET_RELEASE}" ]; then 
        run_script "DOWNGRADE" "${file}"
      fi
    done < $DOWNGRADE_LIST
    rm -f $DOWNGRADE_LIST
    set_current_release_level $TARGET_RELEASE
  else
    echo "CURRENT DB RELEASE LEVEL THE SAME AS TARGET RELEASE LEVEL, NO ACTION TAKEN ..." | tee -a $LOG
  fi
}

function run_script
{
  action="${1}"
  script="${2}"
  echo "Perform DB $action use $script ..." | tee -a $LOG
  echo "--" | tee -a $LOG
  ${MYSQL} --verbose < "${script}" 2>$ERR | tee -a $LOG
  echo "--" | tee -a $LOG
}

function set_current_release_level
{
  RELEASE="${1}"
  echo "Set current release level to [$RELEASE] started ...@`date`" | tee -a $LOG
  update_statement="insert into support.db_version (the_key, version) values ('VERSION', '${RELEASE}') on duplicate key update version='${RELEASE}';"
  ${MYSQL} --execute "${update_statement}" 

  echo "" | tee -a $LOG
  echo "CURRENT_RELEASE set to: [$RELEASE]" | tee -a $LOG
  echo "" | tee -a $LOG
  echo "Set current release level completed ...@`date`" | tee -a $LOG
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
  POLICY_LOGS=/var/log/onap
fi
mkdir -p $POLICY_LOGS/policy/db
LOG=$POLICY_LOGS/policy/db/db_upgrade_$DATE.log
ERR=$POLICY_LOGS/policy/db/db_upgrade_$DATE.err
echo "db_upgrade.sh started ..." | tee -a $LOG
if [ $# -eq 3 ]; then 
  TARGET_RELEASE="${1}"
  DB_UPGRADE_USER="${2}"
  DB_UPGRADE_PASSWORD="${3}"
  echo "TARGET_RELEASE : $TARGET_RELEASE" | tee -a $LOG
  echo "DB_UPGRADE_USER: $DB_UPGRADE_USER" | tee -a $LOG
  echo "DB_UPGRADE_DIR : $DB_UPGRADE_DIR" | tee -a $LOG
  #
  if [ ${#TARGET_RELEASE} -ne 6 ]; then 
    echo "ERROR, TARGET_RELEASE MUST BE 6 DIGITS: $TARGET_RELEASE" | tee -a $LOG | tee -a $ERR
  else
    typeset -r MYSQL="mysql -u${DB_UPGRADE_USER} -p${DB_UPGRADE_PASSWORD} ";
    get_current_release_level
    evaluate_upgrade_downgrade
  fi
else
  echo "Usage  : db_upgrade.sh target_release_level db_user_id   db_user_password" | tee -a $LOG
  echo "Example: db_upgrade.sh 151000               policy_user  password" | tee -a $LOG
fi

echo "db_upgrade.sh completed ..." | tee -a $LOG
