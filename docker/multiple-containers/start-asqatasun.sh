#!/bin/bash

# 2016-03-31 mfaure

set -o errexit

if [[ $UID -ne 0 ]]; then
    echo "$0 must be run as root"
    exit 1
fi

#############################################
# init
#############################################

fail() {
        echo ""
	echo "FAILURE : $*"
        echo ""
	if [[ ! $omit_cleanup ]]; then
            cleanup
        fi
	exit -1
}

wait_for_it() {
    # Idea from https://docs.docker.com/compose/startup-order/
    until mysql -h "$host" -u "asqatasun" -c '\l'; do
        >&2 echo "MariaDB is unavailable - waiting"
        sleep 1
    done

    >&2 echo "MariaDB is up - moving forward"
}

host="$1"

wait_for_it $host

echo "yes\n" | ./install.sh  \
        --database-db $MYSQL_DATABASE \ 
        --database-host $MYSQL_HOST \
        --database-user $MYSQL_USER \
        --database-passwd $MYSQL_PASSWORD \
        --asqatasun-url $ASQATASUN_URL \
        --tomcat-webapps $TOMCAT_WEBAPP_DIR \
        --tomcat-user $TOMCAT_USER \
        --asqa-admin-email $ASQATASUN_ADMIN_EMAIL \
        --asqa-admin-passwd $ASQATASUN_ADMIN_PASSWD \
        --firefox-esr-binary-path /opt/firefox/firefox \
        --display-port :99

service xvfb start \
    && /usr/local/tomcat/bin/catalina.sh run  \
    && tail -f /var/log/asqatasun/asqatasun.log

