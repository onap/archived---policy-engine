#!/bin/bash

#########################################################################
##
## Functions
##
#########################################################################

function usage() {
	echo -n "syntax: $(basename $0) "
	echo -n "--debug ("
	echo -n "[--install base|pap|pdp|console|mysql|elk|brmsgw|paplp|pdplp] | "
	echo -n "[--configure base|pap|pdp|console|mysql|elk|brmsgw|paplp|pdplp] | "
}

function check_java() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	TARGET_JAVA_VERSION=$1
	
	if [[ -z ${JAVA_HOME} ]]; then
		echo "error: ${JAVA_HOME} is not set"
		return 1
	fi
	
	if ! check_x_file "${JAVA_HOME}/bin/java"; then
		echo "error: ${JAVA_HOME}/bin/java is not accessible"
		return 1
	fi
	
	INSTALLED_JAVA_VERSION=$("${JAVA_HOME}/bin/java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
	if [[ -z $INSTALLED_JAVA_VERSION ]]; then
		echo "error: ${JAVA_HOME}/bin/java is invalid"
		return 1
	fi
	
	if [[ "${INSTALLED_JAVA_VERSION}" != ${TARGET_JAVA_VERSION}* ]]; then
		echo "error: java version (${INSTALLED_JAVA_VERSION}) does not"\
			 "march desired version ${TARGET_JAVA_VERSION}"
		return 1
	fi 
	
	echo "OK: java ${INSTALLED_JAVA_VERSION} installed"
	
}

function process_configuration() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	CONF_FILE=$1
	while read line || [ -n "${line}" ]; do
        if [[ -n ${line} ]] && [[ ${line} != \#* ]]; then
	        name=$(echo "${line%%=*}")
	        value=$(echo "${line#*=}")
	        # escape ampersand so that sed does not replace it with the search string
            value=${value//&/\\&}
	        if [[ -z ${name} ]] || [[ -z $value ]]; then
	        	echo "WARNING: ${line} missing name or value"
	    	fi
	    	export ${name}="${value}"
	        eval "${name}" "${value}" 2> /dev/null
        fi
	done < "${CONF_FILE}"
	return 0
}

function component_preconfigure() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi

	/bin/sed -i -e 's!${{POLICY_HOME}}!'"${POLICY_HOME}!g" \
		-e 's!${{FQDN}}!'"${FQDN}!g" \
		*.conf > /dev/null 2>&1
}

function tomcat_component() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	TOMCAT_TARGET_INSTALL_DIR=${POLICY_HOME}/servers/${COMPONENT_TYPE}
	if [[ -d ${TOMCAT_TARGET_INSTALL_DIR} ]]; then
		echo "error: ${TOMCAT_TARGET_INSTALL_DIR} exists."
		return 1
	fi
	
	TOMCAT_INSTALL_DIR=${POLICY_HOME}/install/3rdparty/${TOMCAT_PACKAGE_NAME}/
	if [[ -d ${TOMCAT_INSTALL_DIR} ]]; then
		echo "error: ${TOMCAT_INSTALL_DIR} exists."
		return 1		
	fi
	
	tar -C "${POLICY_HOME}/servers" -xf "${POLICY_HOME}/install/3rdparty/${TOMCAT_PACKAGE_NAME}.tar.gz"
	
	mv "${POLICY_HOME}/servers/${TOMCAT_PACKAGE_NAME}" "${POLICY_HOME}/servers/${COMPONENT_TYPE}/"
	/bin/cp "${POLICY_HOME}"/install/servers/common/tomcat/bin/* "${POLICY_HOME}/servers/${COMPONENT_TYPE}/bin"
	/bin/cp "${POLICY_HOME}"/install/servers/common/tomcat/conf/* "${POLICY_HOME}/servers/${COMPONENT_TYPE}/conf"
	
	/bin/cp "${POLICY_HOME}/install/servers/common/tomcat/init.d/tomcatd" "${POLICY_HOME}/etc/init.d/${COMPONENT_TYPE}"
	/bin/sed -i -e "s!\${{COMPONENT_TYPE}}!${COMPONENT_TYPE}!g" "${POLICY_HOME}/etc/init.d/${COMPONENT_TYPE}" >/dev/null 2>&1


	/bin/cp -fr "${POLICY_HOME}"/install/servers/${COMPONENT_TYPE}/webapps/* "${POLICY_HOME}/servers/${COMPONENT_TYPE}/webapps"
	/bin/cp -fr "${POLICY_HOME}"/install/servers/${COMPONENT_TYPE}/bin/* "${POLICY_HOME}/servers/${COMPONENT_TYPE}/bin" >/dev/null 2>&1
	/bin/cp -fr "${POLICY_HOME}"/install/servers/${COMPONENT_TYPE}/conf/* "${POLICY_HOME}/servers/${COMPONENT_TYPE}/conf" >/dev/null 2>&1
	
	/bin/rm -fr "${POLICY_HOME}/servers/${COMPONENT_TYPE}/webapps/docs" \
		 "${POLICY_HOME}/servers/${COMPONENT_TYPE}/webapps/examples" \
		 "${POLICY_HOME}/servers/${COMPONENT_TYPE}/webapps/ROOT" \
		 "${POLICY_HOME}/servers/${COMPONENT_TYPE}/webapps/manager" \
		 "${POLICY_HOME}/servers/${COMPONENT_TYPE}/webapps/host-manager"
	
	if [[ ${COMPONENT_TYPE} == console ]]; then
		install_onap_portal_settings
	fi

	return 0
}

function configure_tomcat_component() {
	configure_component "${COMPONENT_TYPE}.conf" "${POLICY_HOME}/servers/${COMPONENT_TYPE}/"
}

function configure_component() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
		
	if ! process_configuration "${COMPONENT_TYPE}.conf"; then
		echo "error: aborting ${COMPONENT_TYPE} installation: cannot process configuration ${COMPONENT_TYPE}.conf"
		exit 1
	fi
	
	CONF_FILE=$1
	COMPONENT_ROOT_DIR=$2
	
	SED_LINE="sed -i"
	SED_LINE+=" -e 's!\${{POLICY_HOME}}!${POLICY_HOME}!g' "
	SED_LINE+=" -e 's!\${{POLICY_USER}}!${POLICY_USER}!g' "
	SED_LINE+=" -e 's!\${{POLICY_GROUP}}!${POLICY_GROUP}!g' "
	SED_LINE+=" -e 's!\${{KEYSTORE_PASSWD}}!${KEYSTORE_PASSWD}!g' "
	SED_LINE+=" -e 's!\${{JAVA_HOME}}!${JAVA_HOME}!g' "
	SED_LINE+=" -e 's!\${{COMPONENT_TYPE}}!${COMPONENT_TYPE}!g' "
		
	while read line || [ -n "${line}" ]; do
        if [[ -n $line ]] && [[ $line != \#* ]]; then
	        name=$(echo "${line%%=*}")
	        value=$(echo "${line#*=}")
	        # escape ampersand so that sed does not replace it with the search string
        	value=${value//&/\\&}
	    	if [[ -z ${name} ]] || [[ -z ${value} ]]; then
	        	echo "WARNING: ${line} missing name or value"
	    	fi
	    	SED_LINE+=" -e 's!\${{${name}}}!${value}!g' "
	        
        fi
	done < "$CONF_FILE"
	
	SED_FILES=""
	for sed_file in $(find "${COMPONENT_ROOT_DIR}" -name '*.xml' -o -name '*.sh' -o -name '*.properties' -o -name '*.conf' -o -name '*.cfg' -o -name '*.template' -o -name '*.conf' -o -name '*.cron' -o -name '*.json' | grep -v /backup/); do
		if fgrep -l '${{' ${sed_file} > /dev/null 2>&1; then
			SED_FILES+="${sed_file} "
		fi
	done

	if [[ -f $HOME/.m2/settings.xml ]]; then
		SED_FILES+="$HOME/.m2/settings.xml "
	fi
	

	if [[ -z ${SED_FILES} ]]; then
		echo "WARNING: no xml, sh, properties, or conf files to perform configuration expansion"
	else
		SED_LINE+=${SED_FILES}
		eval "${SED_LINE}"
	fi

	list_unexpanded_files ${POLICY_HOME}
}

function install_onap_portal_settings() {
	echo "Install onap portal settings"

	# unpack onap war file
	mkdir -p "${POLICY_HOME}"/servers/console/webapps/onap
	cd "${POLICY_HOME}"/servers/console/webapps/onap
	unzip -q ../onap.war
	cd ${INSTALL_DIR}

	# copy over the configured settings
	/bin/cp -fr "${POLICY_HOME}"/install/servers/onap/* "${POLICY_HOME}/servers/console/webapps/onap"
}

function check_r_file() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
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
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi

	FILE=$1
	if [[ ! -f ${FILE} || ! -x ${FILE} ]]; then
        return 1
	fi

	return 0
}

function install_prereqs() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	CONF_FILE=$1
	
	if ! check_r_file "${CONF_FILE}"; then
		echo "error: aborting ${COMPONENT_TYPE} installation: ${CONF_FILE} is not accessible"
		exit 1
	fi
	
	if ! process_configuration "${CONF_FILE}"; then
		echo "error: aborting ${COMPONENT_TYPE} installation: cannot process configuration ${CONF_FILE}"
		exit 1
	fi
	
#	if ! check_java "1.8"; then
#		echo "error: aborting ${COMPONENT_TYPE} installation: invalid java version"
#		exit 1
#	fi
	
	if [[ -z ${POLICY_HOME} ]]; then
		echo "error: aborting ${COMPONENT_TYPE} installation: ${POLICY_HOME} is not set"
		exit 1	
	fi

	HOME_OWNER=$(ls -ld "${POLICY_HOME}" | awk '{print $3}')
	if [[ ${HOME_OWNER} != ${POLICY_USER} ]]; then
		echo "error: aborting ${COMPONENT_TYPE} installation: ${POLICY_USER} does not own ${POLICY_HOME} directory"
		exit 1
	fi
	
	echo -n "Starting ${OPERATION} of ${COMPONENT_TYPE} under ${POLICY_USER}:${POLICY_GROUP} "
	echo "ownership with umask $(umask)."
}

function list_unexpanded_files() {
	ROOT_DIR=$1
	SEARCH_LIST=$(find ${ROOT_DIR} -type f -name '*.properties' -o -name '*.sh'  -o -name '*.conf' -o -name '*.yml' -o -name '*.template' -o -name '*.xml' -o -name '*.cfg' -o -name '*.json' -o -path "${ROOT_DIR}/etc/init.d/*" | egrep -v '/m2/|/install/|/logs/')
    NOT_EXPANDED_BASE_FILES=$(grep -l '${{' ${SEARCH_LIST} 2> /dev/null)
	if [[ -n ${NOT_EXPANDED_BASE_FILES} ]]; then
		echo "error: component installation has completed but some base files have not been expanded:"
		echo "${NOT_EXPANDED_BASE_FILES}"
		return 1
	fi
	return 0
}

function install_base() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	install_prereqs "${BASE_CONF}"
	
	if [[ -z ${POLICY_HOME} ]]; then
		echo "error: ${POLICY_HOME} is not set"
		exit 1
	fi
	
	POLICY_HOME_CONTENTS=$(ls -A "${POLICY_HOME}" 2> /dev/null)
	if [[ -n ${POLICY_HOME_CONTENTS} ]]; then
		echo "error: aborting base installation: ${POLICY_HOME} directory is not empty"
		exit 1
	fi
	
	if [[ ! -d ${POLICY_HOME} ]]; then
		echo "error: aborting base installation: ${POLICY_HOME} is not a directory."
		exit 1
	fi
	
	if ! /bin/mkdir -p "${POLICY_HOME}/servers/" > /dev/null 2>&1; then	
		echo "error: aborting base installation: cannot create ${POLICY_HOME}/servers/"
		exit 1
	fi	
	
	if ! /bin/mkdir -p "${POLICY_HOME}/logs/" > /dev/null 2>&1; then	
		echo "error: aborting base installation: cannot create ${POLICY_HOME}/logs/"
		exit 1
	fi	
	
	BASE_TGZ=$(ls base-*.tar.gz)
	if [ ! -r ${BASE_TGZ} ]; then
		echo "error: aborting base installation: ${POLICY_USER} cannot access tar file: ${BASE_TGZ}"
		exit 1			
	fi
	
	tar -tzf ${BASE_TGZ} > /dev/null 2>&1
	if [[ $? != 0 ]]; then
		echo >&2 "error: aborting base installation: invalid base package tar file: ${BASE_TGZ}"
		exit 1
	fi
	
	BASH_PROFILE_LINE=". ${POLICY_HOME}/etc/profile.d/env.sh"
	PROFILE_LINE="ps -p \$\$ | grep -q bash || . ${POLICY_HOME}/etc/profile.d/env.sh"
		
	tar -C ${POLICY_HOME} -xf ${BASE_TGZ} --no-same-owner
	if [[ $? != 0 ]]; then
		# this should not happened
		echo "error: aborting base installation: base package cannot be unpacked: ${BASE_TGZ}"
		exit 1
	fi

	/bin/mkdir -p ${POLICY_HOME}/etc/ssl > /dev/null 2>&1
	/bin/mkdir -p ${POLICY_HOME}/etc/init.d > /dev/null 2>&1
	/bin/mkdir -p ${POLICY_HOME}/tmp > /dev/null 2>&1
	/bin/mkdir -p ${POLICY_HOME}/var > /dev/null 2>&1
			
	#list_unexpanded_files ${POLICY_HOME}
}


function configure_base() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	# check if fqdn is set in base.conf and use that value if set
	if [[ -z ${INSTALL_FQDN} ]]
	then
		echo "FQDN not set in config...using the default FQDN ${FQDN}"
	else
		echo "Using FQDN ${INSTALL_FQDN} from config"
		FQDN=${INSTALL_FQDN}
	fi

	configure_component "${BASE_CONF}" "${POLICY_HOME}"
	
	BASH_PROFILE_LINE=". ${POLICY_HOME}/etc/profile.d/env.sh"
	PROFILE_LINE="ps -p \$\$ | grep -q bash || . ${POLICY_HOME}/etc/profile.d/env.sh"
	
	if ! fgrep -x "${BASH_PROFILE_LINE}" "${HOME}/.bash_profile" >/dev/null 2>&1; then
		echo "${BASH_PROFILE_LINE}" >> "${HOME}/.bash_profile"
	fi
	
	if ! fgrep -x "${PROFILE_LINE}" "${HOME}/.profile" >/dev/null 2>&1; then
		echo "${PROFILE_LINE}" >> "${HOME}/.profile"
	fi
}

function install_tomcat_component() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	install_prereqs "${BASE_CONF}"

	if ! process_configuration "${COMPONENT_TYPE}.conf"; then
		echo "error: aborting ${COMPONENT_TYPE} installation: cannot process configuration ${COMPONENT_TYPE}.conf"
		exit 1
	fi
	
	if ! tomcat_component; then
		echo "error: aborting ${COMPONENT_TYPE} installation: tomcat installation failed."
		exit 1			
	fi
	
}

# This function installs mysql related shell scripts and sql files in the proper locations
# under $POLICY_HOME. It also adds the MySQL client bin to the PATH based on configuration.
#
function install_mysql() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	install_prereqs "${BASE_CONF}"

	if ! process_configuration "${COMPONENT_TYPE}.conf"; then
		echo "error: aborting ${COMPONENT_TYPE} installation: cannot process configuration ${COMPONENT_TYPE}.conf"
		exit 1
	fi
	
	MYSQL_DATA_PATH=${POLICY_HOME}/data/mysql
	/bin/mkdir -p ${MYSQL_DATA_PATH} > /dev/null 2>&1
	
	/bin/cp -f "${POLICY_HOME}"/install/mysql/data/* "${MYSQL_DATA_PATH}"
	/bin/chmod 555 "${MYSQL_DATA_PATH}"/*
	
	MYSQL_BIN_SOURCE=${POLICY_HOME}/install/mysql/bin
	/bin/mkdir -p ${POLICY_HOME}/bin > /dev/null 2>&1
	for script in $(/bin/ls "${MYSQL_BIN_SOURCE}"); do
		/bin/cp ${MYSQL_BIN_SOURCE}/${script} ${POLICY_HOME}/bin
		/bin/chmod 555 "${POLICY_HOME}/bin/${script}"
	done
}

function configure_mysql() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	# nothing to do
}

# This function installs elk related shell scripts and sql files in the proper locations
# under $POLICY_HOME. It also adds the Elk to the PATH based on configuration.
#
function configure_elk() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	# nothing to do
}

function install_elk() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	if [[ -f "${HOME}/.bash_profile" ]]; then
		source "${HOME}/.bash_profile"
	fi
	
	if [[ -f "${HOME}/.profile" ]]; then
		source "${HOME}/.profile"
	fi
	
	ELK_TARGET_INSTALL_DIR="${POLICY_HOME}"/elk
	
	if [[ -d ${ELK_TARGET_INSTALL_DIR} ]]; then
		echo "WARNING: ${ELK_TARGET_INSTALL_DIR} exists."
		return 1
	fi
	
	/bin/mkdir -p "${ELK_TARGET_INSTALL_DIR}" > /dev/null 2>&1
	
	if [[ ! -d ${ELK_TARGET_INSTALL_DIR} ]]; then
		echo "WARNING: ${ELK_TARGET_INSTALL_DIR} doesn't exist."
		return 1
	fi
	
	cd ${ELK_TARGET_INSTALL_DIR}
	curl -L -O https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.4.0.tar.gz
	
	tar xvzf elasticsearch-5.4.0.tar.gz -C .
	/bin/rm -fr elasticsearch-5.4.0.tar.gz
	/bin/mv  ${ELK_TARGET_INSTALL_DIR}/elasticsearch-5.4.0/* .
	/bin/rm -fr ${ELK_TARGET_INSTALL_DIR}/elasticsearch-5.4.0
	
	/bin/cp "${POLICY_HOME}"/install/elk/bin/* "${POLICY_HOME}/bin"	
	/bin/cp -f "${POLICY_HOME}"/install/elk/config/* "${ELK_TARGET_INSTALL_DIR}/config"
	/bin/cp -f "${POLICY_HOME}/install/elk/init.d/elkd" "${POLICY_HOME}/etc/init.d/elk"
	
	install_prereqs "${COMPONENT_TYPE}.conf"
	
	/bin/sed -i -e "s!\${{POLICY_HOME}}!${POLICY_HOME}!g" \
		-e "s!\${{FQDN}}!${FQDN}!g" \
		-e "s!\${{ELK_JMX_PORT}}!${ELK_JMX_PORT}!g" \
		"${ELK_TARGET_INSTALL_DIR}"/config/* "${POLICY_HOME}/etc/init.d/elk" > /dev/null 2>&1
		

	list_unexpanded_files ${POLICY_HOME}
		
	return $?
}

# This function installs brmsgw related shell scripts and config files in the proper
# locations under $POLICY_HOME. 
#

function install_brmsgw() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	install_prereqs "${BASE_CONF}"

	if ! process_configuration "${COMPONENT_TYPE}.conf"; then
		echo "error: aborting ${COMPONENT_TYPE} installation: cannot process configuration ${COMPONENT_TYPE}.conf"
		exit 1
	fi
	
	if [ -z "$M2_HOME" ]; then
		echo "error: aborting ${COMPONENT_TYPE} installation: M2_HOME must be set in brmsgw.conf"
		exit 1
	fi
	
	echo "export M2_HOME=$M2_HOME" >>$POLICY_HOME/etc/profile.d/env.sh

	/bin/cp -f "${POLICY_HOME}/install/servers/brmsgw/init.d/brmsgw" "${POLICY_HOME}/etc/init.d/brmsgw"
	
	if ! /bin/mkdir -p "${POLICY_HOME}/servers/${COMPONENT_TYPE}" > /dev/null 2>&1; then	
		echo "error: aborting base installation: cannot create ${POLICY_HOME}/servers/${COMPONENT_TYPE}"
		exit 1
	fi	
	
	/bin/cp -fr "${POLICY_HOME}"/install/servers/${COMPONENT_TYPE}/BRMSGateway.jar "${POLICY_HOME}/servers/${COMPONENT_TYPE}"
	/bin/cp -fr "${POLICY_HOME}"/install/servers/${COMPONENT_TYPE}/*.properties "${POLICY_HOME}/servers/${COMPONENT_TYPE}"
	/bin/cp -fr "${POLICY_HOME}"/install/servers/${COMPONENT_TYPE}/config "${POLICY_HOME}/servers/${COMPONENT_TYPE}"
	/bin/cp -fr "${POLICY_HOME}"/install/servers/${COMPONENT_TYPE}/dependency.json "${POLICY_HOME}/servers/${COMPONENT_TYPE}"
	
	/bin/mv $POLICY_HOME/m2 $HOME/.m2

	return 0
}


function install_logparser() {
	if [[ $DEBUG == y ]]; then
		echo "-- ${FUNCNAME[0]} $@ --"
		set -x
	fi
	
	install_prereqs "${BASE_CONF}"

	if ! process_configuration "${COMPONENT_TYPE}.conf"; then
		echo "error: aborting ${COMPONENT_TYPE} installation: cannot process configuration ${COMPONENT_TYPE}.conf"
		exit 1
	fi
	
	LP_TARGET_DIR=${POLICY_HOME}/servers/${COMPONENT_TYPE}
	/bin/mkdir -p ${LP_TARGET_DIR}/bin > /dev/null 2>&1
	/bin/mkdir -p ${LP_TARGET_DIR}/logs > /dev/null 2>&1
	
	# copy binaries, initialization script and configuration
	/bin/cp "${POLICY_HOME}"/install/servers/common/logparser/bin/*jar "${LP_TARGET_DIR}/bin"
	/bin/cp "${POLICY_HOME}/install/servers/common/logparser/init.d/logparserd" "${POLICY_HOME}/etc/init.d/${COMPONENT_TYPE}"
	/bin/cp "${POLICY_HOME}/install/servers/${COMPONENT_TYPE}/bin/parserlog.properties" "${LP_TARGET_DIR}/bin"
	/bin/cp -fr "${POLICY_HOME}"/install/servers/${COMPONENT_TYPE}/bin/config "${POLICY_HOME}/servers/${COMPONENT_TYPE}/bin"
	
}

#########################################################################
##
## script execution body
##
#########################################################################


OPERATION=none
COMPONENT_TYPE=none
DEBUG=n

BASE_CONF=base.conf

TOMCAT_PACKAGE_NAME=apache-tomcat-8.0.23

INSTALL_DIR="$(pwd)"

export POLICY_USER=$(/usr/bin/id -un)

# command line options parsing
until [[ -z "$1" ]]; do
	case $1 in
		-d|--debug) 	DEBUG=y
						set -x
						;;
		-i|--install) 	OPERATION=install
						shift
						COMPONENT_TYPE=$1
						;;
		-c|--configure)	OPERATION=configure
						shift
						COMPONENT_TYPE=$1
						;;
		*)				usage
						exit 1
						;;
	esac
	shift
done

# component-type validation
case $COMPONENT_TYPE in
	base)	;;
	pdp)	;;
	pap)	;;
	console)	;;
	mysql)	;;
	elk)    ;;
	brmsgw)	;;
	paplp)	;;
	pdplp)	;;
	skip)	;;
	*)		echo "invalid component type (${COMPONENT_TYPE}): must be in {base|pdp|pap|console|mysql|elk|brmsgw|paplp|pdplp}";
			usage
			exit 1
			;;
esac

# operation validation
case $OPERATION in
	install|configure)	;;
	*)		echo "invalid operation (${OPERATION}): must be in {install|configure}";
			usage
			exit 1
			;;
esac

if [[ -n ${POLICY_GROUP} ]]; then
	groups=$(groups)
	if ! echo ${groups} | grep -qP "\b${POLICY_GROUP}"; then
		echo "error: ${POLICY_GROUP} is not a valid group for account ${POLICY_USER}"
		exit 1
	fi
fi

if [[ -z ${POLICY_GROUP} ]]; then
	numGroups=$(groups | sed "s/^.*: *//g" | wc -w)
	if [ ${numGroups} -eq 1 ]; then
		export POLICY_GROUP=$(groups ${POLICY_USER} | sed "s/^.*: *//g")
	else
		echo "error: ${POLICY_USER} belongs to multiple groups, one group \
              must be provided for the installation"
		usage
		exit 1
	fi
fi

if [[ -z ${POLICY_GROUP} ]]; then
	echo "error: installation of root section must not provide the \
	      installation group owner argument."
	usage
	exit 1
fi

FQDN=$(hostname -f 2> /dev/null)
if [[ $? != 0 || -z ${FQDN} ]]; then
	echo "error: cannot determine the FQDN for this host $(hostname)."
	exit 1
fi

if [[ ${OPERATION} == install ]]; then
	case $COMPONENT_TYPE in
		base)	
			install_base
			;;
		pdp)	
			install_tomcat_component
			;;
		pap)
			install_tomcat_component
			;;
		console)
			install_tomcat_component
			;;
		mysql)
			install_mysql
			;;
		elk)
			install_elk
			;;		
		brmsgw)
			install_brmsgw
			;;
		paplp|pdplp)
			install_logparser
			;;
		*)		
			echo "invalid component type (${COMPONENT_TYPE}): must be in {base|pdp|pap|console|mysql|elk|brmsgw|paplp|pdplp}";
			usage
			exit 1
			;;
	esac
fi
if [[ ${OPERATION} == configure ]]; then

	install_prereqs "${BASE_CONF}"

	case $COMPONENT_TYPE in
		base)	
			configure_base
			component_preconfigure
			;;
		pdp)	
			configure_component "${COMPONENT_TYPE}.conf" "${POLICY_HOME}/servers/${COMPONENT_TYPE}/"
			;;
		pap)
			configure_component "${COMPONENT_TYPE}.conf" "${POLICY_HOME}/servers/${COMPONENT_TYPE}/"
			;;
		console)
			configure_component "${COMPONENT_TYPE}.conf" "${POLICY_HOME}/servers/${COMPONENT_TYPE}/"
			;;
		mysql)
			configure_mysql
			;;
		elk)
			configure_elk
			;;	
		brmsgw)
			configure_component "${COMPONENT_TYPE}.conf" "${POLICY_HOME}/servers/${COMPONENT_TYPE}/"
			;;
		paplp|pdplp)
			configure_component "${COMPONENT_TYPE}.conf" "${POLICY_HOME}/servers/${COMPONENT_TYPE}/"
			;;
		*)		
			echo "invalid component type (${COMPONENT_TYPE}): must be in {base|pdp|pap|console|mysql|elk|brmsgw|paplp|pdplp}";
			usage
			exit 1
			;;
	esac
fi


echo -n "Successful ${OPERATION} of ${COMPONENT_TYPE} under ${POLICY_USER}:${POLICY_GROUP} "
echo "ownership with umask $(umask)."
