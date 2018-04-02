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
# db_backup_remote.sh: Perform database backup from remote 
# 
# Usage  : db_backup_remote.sh db_user     db_user_password database  db_hostname
# Example: db_backup_remote.sh policy_user password         onap_sdk localhost.com
#
# Note: 1. mysqldump utility must be available in the env where this script intend to run
#       2. db_user requires at least the SELECT privilege for dumped tables
#
#

. $HOME/.profile

DB_USER=""
DB_PASSWORD=""
DATABASE=""
DB_HOSTNAME=""
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
  echo "backup database [$DATABASE]@[${DB_HOSTNAME}] started ...@`date`" | tee -a $LOG

  BACKUP_FILE=$DAILY_BACKUP_DIR/backup_${DATABASE}_${DB_HOSTNAME}_${DATETIME}.sql
  #echo $BACKUP_FILE
  mysqldump --user=${DB_USER} --password=${DB_PASSWORD} --databases ${DATABASE} -h ${DB_HOSTNAME} > $BACKUP_FILE 
  echo "" | tee -a $LOG
  echo "database backup file --> $BACKUP_FILE" | tee -a $LOG
  echo "" | tee -a $LOG
  echo "backup database [$DATABASE]@[${DB_HOSTNAME}] completed ...@`date`" | tee -a $LOG
}


# MAIN
if [ $# -eq 4 ]; then 
  DB_USER="${1}"
  DB_PASSWORD="${2}"
  DATABASE="${3}"
  DB_HOSTNAME="${4}"
  if [ -z ${POLICY_LOGS} ]; then
    POLICY_LOGS=/var/log/onap
  fi
  mkdir -p $POLICY_LOGS/policy/db
  LOG=$POLICY_LOGS/policy/db/db_backup_remote_$DATE.log
  ERR=$POLICY_LOGS/policy/db/db_backup_remote_$DATE.err

  echo "db_backup_remote.sh for [$DATABASE]@[${DB_HOSTNAME}] started ... `date`" | tee -a $LOG
  echo "DB_USER    : $DB_USER"     | tee -a $LOG
  echo "DATABASE   : $DATABASE"    | tee -a $LOG
  echo "DB_HOSTNAME: $DB_HOSTNAME" | tee -a $LOG
    
  DAILY_BACKUP_DIR=$POLICY_HOME/data/mysql/$DATE
  create_backup_dir 

  backup_database
  echo "db_backup_remote.sh for [$DATABASE]@[${DB_HOSTNAME}] completed ... `date`" | tee -a $LOG
else
  echo "Usage  : db_backup_remote.sh db_user_id  db_user_password database  db_hostname" 
  echo "Example: db_backup_remote.sh policy_user password         onap_sdk localhost.com" 
fi
