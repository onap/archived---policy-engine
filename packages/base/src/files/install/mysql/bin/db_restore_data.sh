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
# db_restore_data.sh: Restore only data for database table(s) from database backup file
# 
# Usage  : db_restore_data.sh db_user     db_password backup_file database table_name
# Example: db_restore_data.sh policy_user password    /opt/app/policy/data/mysql/20150901/backup_xacml_data_20150910102030.sql xacml attribute
#          db_restore_data.sh policy_user password    /opt/app/policy/data/mysql/20150901/backup_xacml_data_20150910102030.sql xacml all
#
# Assumption: 
#   1. Database backup_file is created from db_backup_data.sh (contains only data)
#   2. Data in table(s) will be wiped out and loaded from backup file
#
# Note: use lower case table name
#
#

DB_USER=""
DB_PASSWORD=""
BACKUP_FILE=""
DATABASE=""
TABLE=""
TEMP_FILE=/tmp/db_restore_data_$$.sql

function restore_all
{
  echo "restore_all started ...@`date`" 
  echo "set foreign_key_checks=0;" > $TEMP_FILE
  sed -e 's/LOCK TABLES \(.*\) WRITE;/delete from \1; LOCK TABLES \1 WRITE;/g' $BACKUP_FILE >> $TEMP_FILE
  echo "set foreign_key_checks=1;" >> $TEMP_FILE
  #cat $TEMP_FILE
  echo "Before restore table ..." | tee -a $LOG
  echo "--" | tee -a $LOG
  mysql -u${DB_USER} -p${DB_PASSWORD} --verbose < $BACKUP_FILE 2>&1 | tee -a $LOG
  echo "--" | tee -a $LOG
  echo "restore_all completed ...@`date`" 
}

function restore_table
{
  database="${1}"
  table="${2}"
  echo "restore_table [$database] [$table] started ...@`date`" 
  # extract sql statement from backup file
  echo "use $database;" > $TEMP_FILE
  echo "set sql_safe_updates=0;" >> $TEMP_FILE
  echo "delete from $table;" >> $TEMP_FILE
  sed -n -e '/LOCK TABLES `'$table'` WRITE;/,/UNLOCK TABLES;/p' $BACKUP_FILE >> $TEMP_FILE
  echo "set sql_safe_updates=1;" >> $TEMP_FILE
  #echo "--"
  #cat $TEMP_FILE
  echo "Before restore table ..." 2>&1 | tee -a $LOG
  echo "--" | tee -a $LOG
  mysql -u${DB_USER} -p${DB_PASSWORD} --verbose < $TEMP_FILE 2>&1 | tee -a $LOG
  echo "--" | tee -a $LOG
  echo "restore_table [$database] [$table] completed ...@`date`" 
}


# MAIN
echo "db_restore_data.sh started ... `date`" 
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
    echo "BACKUP FILE NOT FOUND: $BACKUP_FILE" 
  fi
else
  echo "Usage  : db_restore_data.sh db_user_id  db_password  backup_file database table_name" 
  echo "Example: db_restore_data.sh policy_user password     /opt/app/policy/data/mysql/20150901/backup_xacml_data_20150901102030.sql xacml attribute" 
fi

rm -f $TEMP_FILE
echo "db_restore_data.sh completed ... `date`" 
