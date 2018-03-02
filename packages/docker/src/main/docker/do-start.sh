#!/bin/bash

# Script to configure and start the Policy components that are to run in the designated container,
# It is intended to be used as the entrypoint in the Dockerfile, so the last statement of the
# script just goes into a long sleep so that the script does not exit (which would cause the
# container to be torn down).

container=$1

case $container in
pap)
	comps="base pap paplp console mysql elk"
	;;
pdp)
	comps="base pdp pdplp"
	;;
brmsgw)
	comps="base brmsgw"
	;;
*)
	echo "Usage: do-start.sh pap|pdp|brmsgw" >&2
	exit 1
esac


# skip installation if build.info file is present (restarting an existing container)
if [[ -f /opt/app/policy/etc/build.info ]]; then
	echo "Found existing installation, will not reinstall"
	. /opt/app/policy/etc/profile.d/env.sh

else
	if [[ -d config ]]; then
		cp config/*.conf .
	fi

	for comp in $comps; do
		echo "Installing component: $comp"
		./docker-install.sh --install $comp
	done
	for comp in $comps; do
		echo "Configuring component: $comp"
		./docker-install.sh --configure $comp
	done

	. /opt/app/policy/etc/profile.d/env.sh

	# install policy keystore
	mkdir -p $POLICY_HOME/etc/ssl
	cp config/policy-keystore $POLICY_HOME/etc/ssl

	if [[ -f config/$container-tweaks.sh ]] ; then
		# file may not be executable; running it as an
		# argument to bash avoids needing execute perms.
		bash config/$container-tweaks.sh
	fi

	if [[ $container == pap ]]; then
		# wait for DB up
		./wait-for-port.sh mariadb 3306
		# now that DB is up, invoke database upgrade
		# (which does nothing if the db is already up-to-date)
		dbuser=$(echo $(grep '^JDBC_USER=' base.conf | cut -f2 -d=))
		dbpw=$(echo $(grep '^JDBC_PASSWORD=' base.conf | cut -f2 -d=))
		db_upgrade_remote.sh $dbuser $dbpw mariadb
	fi

fi

# pap needs to wait for mariadb up before starting;
# others need to wait for pap up (in case it had to do db upgrade)
if [[ $container == pap ]]; then
	# we may have already done this above, but doesn't hurt to repeat
	./wait-for-port.sh mariadb 3306
else
	./wait-for-port.sh pap 9091
fi

policy.sh start

# on pap, wait for pap, pdp, brmsgw, nexus and drools up,
# then push the initial default policies
if [[ $container == pap ]]; then
	./wait-for-port.sh pap 9091
	./wait-for-port.sh pdp 8081
	# brmsgw doesn't have a REST API, so check for JMX port instead
	./wait-for-port.sh brmsgw 9989
	./wait-for-port.sh nexus 8081
	./wait-for-port.sh drools 6969
	# wait addional 1 minute for all processes to get fully initialized and synched up
	sleep 60
	bash -xv config/push-policies.sh
fi

sleep 1000d
