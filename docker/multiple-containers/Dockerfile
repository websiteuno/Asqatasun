FROM tomcat:7-jre7
MAINTAINER Matthieu Faure <mfaure@asqatasun.org>

#USER root

# ##########################################################
#
# Env variables
#
# ##########################################################

# XVFB
ENV XVFB_INITSCRIPT_FILENAME="xvfb" \
    XVFB_INITSCRIPT_DIR="./install"

# Firefox ESR (Please keep *this* version of Firefox)
ENV FIREFOX_PARENT_DIR="/opt" \
    FIREFOX_BASENAME="firefox-31.4.0esr" \
    FIREFOX_URL_i686="http://download.cdn.mozilla.net/pub/mozilla.org/firefox/releases/31.4.0esr/linux-i686/en-US" \
    FIREFOX_URL_x86_64="http://download.cdn.mozilla.net/pub/mozilla.org/firefox/releases/31.4.0esr/linux-x86_64/en-US"

# Mysql + Tomcat
ENV MYSQL_DATABASE="asqatasun" \
    MYSQL_USER="asqatasun" \
    MYSQL_PASSWORD="asqaP4sswd" \
    MYSQL_HOST="db" \
    TOMCAT_WEBAPP_DIR="$CATALINA_HOME/webapps" \
    TOMCAT_USER="root"

# Asqatasun
ENV ASQATASUN_URL="http://localhost:8080/asqatasun/" \
    ASQATASUN_ADMIN_EMAIL="me@my-email.org" \
    ASQATASUN_ADMIN_PASSWD="myAsqaPassword"  \
    ASQATASUN_RELEASE="4.0.1"

# ##########################################################
#
# Grab & unpack Asqatasun 
#
# ##########################################################

WORKDIR /tmp

# Add Asqatasun
#ADD http://download.asqatasun.org/asqatasun-${ASQATASUN_RELEASE}.tar.gz /tmp/
#RUN tar xvfz asqatasun-${ASQATASUN_RELEASE}.tar.gz && \
#    mv asqatasun*/ ./asqatasun/

# The two following commands are needed for local testing when a release is created
ADD asqatasun-${ASQATASUN_RELEASE}.i386.tar.gz /tmp/
RUN mv asqatasun*/ ./asqatasun/
WORKDIR /tmp/asqatasun

# ##########################################################
#
# Asqatasun pre-requisites
# http://doc.asqatasun.org/en/10_Install_doc/Asqatasun/pre-requisites.html
#
# ##########################################################

# Packages
RUN export DEBIAN_FRONTEND=noninteractive \
    && apt-get update \
    && apt-get -y --no-install-recommends install apt-utils \
    && apt-get -y --no-install-recommends install \
        wget \
        bzip2 \
        unzip \
        mysql-client \
        libmysql-java \
        libspring-instrument-java \
        xvfb \
        libdbus-glib-1-2 \
    && apt-get clean \
    && apt-get autoremove \
    && rm -rf /var/lib/apt/lists/*

# Tomcat config
RUN ln -s /usr/share/java/spring3-instrument-tomcat.jar ${CATALINA_HOME}/lib/spring3-instrument-tomcat.jar \
    && ln -s /usr/share/java/mysql-connector-java.jar ${CATALINA_HOME}/lib/mysql-connector-java.jar

# XVFB Config
RUN cp ${XVFB_INITSCRIPT_DIR}/${XVFB_INITSCRIPT_FILENAME} ${XVFB_INITSCRIPT_DIR}/${XVFB_INITSCRIPT_FILENAME}.old \
    && SH_PERL_COMMAND="s!RUN_AS_USER=tomcat7!RUN_AS_USER=${TOMCAT_USER}!o" \
    && perl -pi -e "${SH_PERL_COMMAND}" ${XVFB_INITSCRIPT_DIR}/${XVFB_INITSCRIPT_FILENAME} \
    && cp ${XVFB_INITSCRIPT_DIR}/${XVFB_INITSCRIPT_FILENAME} /etc/init.d/ \
    && update-rc.d ${XVFB_INITSCRIPT_FILENAME} defaults 

# Firefox
#RUN echo ${FIREFOX_BASENAME}
#RUN echo ${CATALINA_HOME}
RUN wget -q ${FIREFOX_URL_x86_64}/${FIREFOX_BASENAME}.tar.bz2 -O ${FIREFOX_PARENT_DIR}/${FIREFOX_BASENAME}.tar.bz2 \
    && cd ${FIREFOX_PARENT_DIR}/ \
    && tar xvfj ${FIREFOX_BASENAME}.tar.bz2 \
    && mv firefox ${FIREFOX_BASENAME} \
    && ln -s ${FIREFOX_BASENAME} firefox \
    && rm ${FIREFOX_BASENAME}.tar.bz2

# ##########################################################
#
# Asqatasun installation
# http://doc.asqatasun.org/en/10_Install_doc/Asqatasun/index.html
#
# ##########################################################

ENTRYPOINT ./wait-for-it.sh $MYSQL_DATABASE
CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]

#RUN echo "yes\n" | ./install.sh  \
#        --database-db $MYSQL_DATABASE \ 
#        --database-host $MYSQL_HOST \
#        --database-user $MYSQL_USER \
#        --database-passwd $MYSQL_PASSWORD \
#        --asqatasun-url $ASQATASUN_URL \
#        --tomcat-webapps $TOMCAT_WEBAPP_DIR \
#        --tomcat-user $TOMCAT_USER \
#        --asqa-admin-email $ASQATASUN_ADMIN_EMAIL \
#        --asqa-admin-passwd $ASQATASUN_ADMIN_PASSWD \
#        --firefox-esr-binary-path /opt/firefox/firefox \
#        --display-port :99
#
#CMD service xvfb start \
#    && /usr/local/tomcat/bin/catalina.sh run  \
#    && tail -f /var/log/asqatasun/asqatasun.log

#CMD ["catalina.sh", "run"]