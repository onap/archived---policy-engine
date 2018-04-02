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
# db_restore.sh: Restore database table(s) from database backup file
# 
# Usage  : db_restore.sh db_user     db_password backup_file database table_name
# Example: db_restore.sh policy_user password    /opt/app/policy/data/mysql/20150901/backup_onap_sdk_20150910102030.sql onap_sdk attribute
#          db_restore.sh policy_user password    /opt/app/policy/data/mysql/20150901/backup_onap_sdk_20150910102030.sql onap_sdk all
#
# Assumption: Database backup_file is created from mysqldump utility
#
# Note: use lower case table name
#
#

DB_USER=""
DB_PASSWORD=""
BACKUP_FILE=""
DATABASE=""
TABLE=""
TEMP_FILE=/tmp/db_restore_$$.sql
DATE=`date +"%Y%m%d"`
LOG=""

function restore_all
{
  echo "restore_all started ...@`date`" | tee -a $LOG
  echo "Before restore table ..." | tee -a $LOG
  echo "--" 
  mysql -u${DB_USER} -p${DB_PASSWORD} < $BACKUP_FILE 
  echo "--" 

  echo "restore_all completed ...@`date`" | tee -a $LOG
}

function restore_table
{
  database="${1}"
  table="${2}"
  echo "restore_table [$database] [$table] started ...@`date`" | tee -a $LOG
  # extract sql statement from backup file
  echo "use $database;" > $TEMP_FILE 
  echo "set foreign_key_checks=0; " >> $TEMP_FILE
  sed -n -e '/DROP TABLE IF EXISTS `'$table'`;/,/UNLOCK TABLES;/p' $BACKUP_FILE >> $TEMP_FILE
  echo "set foreign_key_checks=1; " >> $TEMP_FILE
  echo "--"
  cat $TEMP_FILE
  echo "--"
  echo "Before restore table ..." | tee -a $LOG
  mysql -u${DB_USER} -p${DB_PASSWORD} < $TEMP_FILE 
  echo "--" 
  echo "restore_table [$database] [$table] completed ...@`date`" | tee -a $LOG
}


# MAIN
if [ -z ${POLICY_LOGS} ]; then
  POLICY_LOGS=/var/log/onap
fi
mkdir -p $POLICY_LOGS/policy/db
LOG=$POLICY_LOGS/policy/db/db_restore_$DATE.log
  
echo "db_restore.sh started ... `date`" | tee -a $LOG
if [ $# -eq 5 ]; then 
  DB_USER="${1}"
  DB_PASSWORD="${2}"
  BACKUP_FILE="${3}"
  typeset -l DATABASE="${4}"
  typeset -l TABLE="${5}"
  echo "DB_USER: $DB_USER" 
  if [ -f $BACKUP_FILE ]; then 
    if [ "${TABLE}" != "all" ]; then 
      restore_table ${DATABASE} ${TABLE}
    else
      restore_all
    fi
  else
    echo "BACKUP FILE NOT FOUND: $BACKUP_FILE" | tee -a $LOG
  fi
else
  echo "Usage  : db_restore.sh db_user_id  db_password  backup_file database table_name" 
  echo "Example: db_restore.sh policy_user password     /opt/app/policy/data/mysql/20150901/backup_onap_sdk_20150901102030.sql onap_sdk attribute" 
fi

rm -f $TEMP_FILE
echo "db_restore.sh completed ... `date`" | tee -a $LOG
