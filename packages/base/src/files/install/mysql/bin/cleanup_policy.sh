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

# cleanup_policy.sh: Run this script to delete policy record marked as 'deleted'
# 
# Usage  : cleanup_policy.sh db_user     db_user_password retention_period
# Example: cleanup_policy.sh policy_user password         90
#
#
#

. $HOME/.profile

DB_USER=""
DB_PASSWORD=""
RETENTION_PERIOD=""
DATE=`date +"%Y%m%d"`
DATETIME=`date +"%Y%m%d%H%M%S"`
LOG=""
ERR=""

function cleanup_deleted_policy
{
  # 1
  echo "1- cleanup_deleted_policy [policyGroupEntity] ... `date`" | tee -a $LOG
  ${MYSQL} -e "delete from xacml.policyGroupEntity where policyId in ( select policyId from xacml.policyEntity where deleted = true and modified_date < (current_date - INTERVAL $RETENTION_PERIOD DAY)) and groupKey in ( select groupKey from xacml.groupEntity where deleted = true and modified_date < (current_date - INTERVAL $RETENTION_PERIOD DAY)); " 2>&1 | tee -a $LOG
  echo "--" | tee -a $LOG

  # 2
  echo "2- cleanup_deleted_policy [pdpEntity] ... `date`" | tee -a $LOG
  ${MYSQL} -e "delete from xacml.pdpEntity where groupKey in ( select groupKey from xacml.groupEntity where deleted = true and modified_date < (current_date - INTERVAL $RETENTION_PERIOD DAY)); " 2>&1 | tee -a $LOG
  echo "--" | tee -a $LOG

  # 3
  echo "3- cleanup_deleted_policy [groupEntity] ... `date`" | tee -a $LOG
  ${MYSQL} -e "delete from xacml.groupEntity where deleted = true and modified_date < (current_date - INTERVAL $RETENTION_PERIOD DAY); " 2>&1 | tee -a $LOG
  echo "--" | tee -a $LOG

  # 4
  echo "4- cleanup_deleted_policy [policyEntity] ... `date`" | tee -a $LOG
  ${MYSQL} -e "delete from xacml.policyEntity where configurationDataId in ( select configurationDataId from xacml.configurationDataEntity where deleted = true and modified_date < (current_date - INTERVAL $RETENTION_PERIOD DAY)) and actionBodyId in ( select actionBodyId from xacml.actionBodyEntity where deleted = true and modified_date < (current_date - INTERVAL $RETENTION_PERIOD DAY)); " 2>&1 | tee -a $LOG
  echo "--" | tee -a $LOG

  # 5
  echo "5- cleanup_deleted_policy [configurationDataEntity] ... `date`" | tee -a $LOG
  ${MYSQL} -e "delete from xacml.configurationDataEntity where deleted = true and modified_date < (current_date - INTERVAL $RETENTION_PERIOD DAY); " 2>&1 | tee -a $LOG
  echo "--" | tee -a $LOG

  # 6
  echo "6- cleanup_deleted_policy [actionBodyEntity] ... `date`" | tee -a $LOG
  ${MYSQL} mysql --verbose -u${DB_USER} -p${DB_PASSWORD} -e "delete from xacml.actionBodyEntity where deleted = true and modified_date < (current_date - INTERVAL $RETENTION_PERIOD DAY); " 2>&1 | tee -a $LOG
  echo "--" | tee -a $LOG
}

# MAIN
if [ -z ${POLICY_LOGS} ]; then
  POLICY_LOGS=/var/log/onap
fi
mkdir -p $POLICY_LOGS/policy/db
LOG=$POLICY_LOGS/policy/db/cleanup_policy_$DATE.log
ERR=$POLICY_LOGS/policy/db/cleanup_policy_$DATE.err
echo "cleanup_policy.sh started ... `date`" | tee -a $LOG
if [ $# -eq 3 ]; then 
  DB_USER="${1}"
  DB_PASSWORD="${2}"
  RETENTION_PERIOD="${3}"
  echo "DB_USER: $DB_USER" | tee -a $LOG

  typeset -r MYSQL="mysql -u${DB_USER} -p${DB_PASSWORD} --verbose ";

  cleanup_deleted_policy

  echo "cleanup_policy.sh completed ... `date`" | tee -a $LOG
else
  echo "Usage  : cleanup_policy.sh db_user_id  db_user_password retention_period" 
  echo "Example: cleanup_policy.sh policy_user password         90" 
fi