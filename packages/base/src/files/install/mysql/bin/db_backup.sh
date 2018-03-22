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
# db_backup.sh: Run this script to back database to a file
# 
# Usage  : db_backup.sh db_user     db_user_password database
# Example: db_backup.sh policy_user password         onap_sdk
#
# Note: mysqldump requires at least the SELECT privilege for dumped tables
#
#

. $HOME/.profile

DB_USER=""
DB_PASSWORD=""
DATABASE=""
DATE=`date +"%Y%m%d"`
DATETIME=`date +"%Y%m%d%H%M%S"`
DAILY_BACKUP_DIR=""
LOG=""
ERR=""

function create_backup_dir
{
  if [ ! -d $DAILY_BACKUP_DIR ]; then 
    echo "Create DAILY_BACKUP_DIR [$DAILY_BACKUP_DIR] ..." 
    mkdir -p $DAILY_BACKUP_DIR 2>&1 
  fi
}

function backup_database
{
  echo "backup_database [$DATABASE] started ...@`date`" | tee -a $LOG

  BACKUP_FILE=$DAILY_BACKUP_DIR/backup_${DATABASE}_${DATETIME}.sql
  echo $BACKUP_FILE
  mysqldump --user=${DB_USER} --password=${DB_PASSWORD} --databases ${DATABASE} > $BACKUP_FILE 
  echo "" | tee -a $LOG
  echo "database backup file --> $BACKUP_FILE" | tee -a $LOG
  echo "" | tee -a $LOG
  echo "backup_database [$DATABASE] completed ...@`date`" | tee -a $LOG
}


# MAIN
if [ -z ${POLICY_LOGS} ]; then
  POLICY_LOGS=/var/log/onap
fi
mkdir -p $POLICY_LOGS/policy/db
LOG=$POLICY_LOGS/policy/db/db_backup_$DATE.log
ERR=$POLICY_LOGS/policy/db/db_backup_$DATE.err  
echo "db_backup.sh started ... `date`" | tee -a $LOG
if [ $# -eq 3 ]; then 
  DB_USER="${1}"
  DB_PASSWORD="${2}"
  DATABASE="${3}"
  echo "DB_USER: $DB_USER" | tee -a $LOG
    
  DAILY_BACKUP_DIR=$POLICY_HOME/data/mysql/$DATE
  create_backup_dir 

  backup_database
  echo "db_backup.sh completed ... `date`" | tee -a $LOG
else
  echo "Usage  : db_backup.sh db_user_id  db_user_password database" 
  echo "Example: db_backup.sh policy_user password         onap_sdk" 
fi

