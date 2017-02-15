###
# ============LICENSE_START=======================================================
# LogParser
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

#!/bin/sh
#
# init script for a Java application
#

#Arguments for application
SERVER=http://localhost:8070/pap/
LOGTYPE=PAP 
LOGPATH="/var/lib/servers/pap/logs/catalina.out"
PARSERLOGPATH="$POLICY_HOME/logs/parserlog.log"
JDBC_URL="jdbc:h2:tcp://localhost:9092/log"
JDBC_USER="sa"
JDBC_DRIVER="org.h2.Driver"
JDBC_PASSWORD=""
SERVICE="LogParser.jar"

# Check the application status
#
# This function checks if the application is running
check_status() {

  # Running pgrep with some arguments to check if the PID exists
  if pgrep -f "$SERVICE $SERVER $LOGTYPE" ; then
     RESULT=$(pgrep -f ${SERVICE})
     return $RESULT
  fi
  return 0
  # In any another case, return 0

}

# Starts the application
start() {

  # At first checks if the application is already started calling the check_status
  # function
  check_status

  # $? is a special variable that hold the "exit status of the most recently executed
  # foreground pipeline"
  pid=$?

  if [ $pid -ne 0 ] ; then
    echo "The application is already started"
    exit 1
  fi

  # If the application isn't running, starts it
  echo -n "Starting application: "

  # Redirects default and error output to a log file
  java -jar LogParser.jar $SERVER $LOGTYPE $LOGPATH $PARSERLOGPATH $JDBC_URL $JDBC_USER $JDBC_DRIVER $JDBC_PASSWORD>> $POLICY_HOME/logs/parserlog.log 2>&1 &
  echo "OK"
}

# Stops the application
stop() {

  # Like as the start function, checks the application status
  check_status

  pid=$?

  if [ $pid -eq 0 ] ; then
    echo "Application is already stopped"
    exit 1
  fi

  # Kills the application process
  echo -n "Stopping application: "
  kill -9 $pid &
  echo "OK"
}

# Show the application status
status() {

  # The check_status function, again...
  check_status

  # If the PID was returned means the application is running
  if [ $? -ne 0 ] ; then
    echo "Application is started"
  else
    echo "Application is stopped"
  fi

}

# Main logic, a simple case to call functions
case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  status)
    status
    ;;
  restart|reload)
    stop
    start
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|reload|status}"
    exit 1
esac

exit 0
