FROM ubuntu:14.04

ARG HTTP_PROXY=${HTTP_PROXY}
ARG HTTPS_PROXY=${HTTPS_PROXY}
ARG BUILD_VERSION=${BUILD_VERSION}
ARG POLICY_LOGS=/var/log/onap

ENV http_proxy $HTTP_PROXY
ENV https_proxy $HTTPS_PROXY
ENV BUILD_VERSION ${BUILD_VERSION}
ENV POLICY_LOGS ${POLICY_LOGS}

RUN \
        apt-get clean && \
        apt-get update && \
        apt-get install -y zip unzip curl wget ssh telnet maven && \
        apt-get install -y software-properties-common && \
        apt-get install -y jq httpie && \
        apt-get install -y python-pip && \
        add-apt-repository ppa:openjdk-r/ppa && \
        apt-get clean && \
        apt-get update && \
        apt-get install -y openjdk-8-jdk

RUN useradd --create-home --shell /bin/bash policy

# install MariaDB client
RUN \
    apt-get install -y apt-transport-https && \
        apt-key adv --recv-keys --keyserver hkp://keyserver.ubuntu.com:80 0xcbcb082a1bb943db && \
        add-apt-repository 'deb [arch=amd64,i386,ppc64el] http://ftp.osuosl.org/pub/mariadb/repo/10.0/ubuntu trusty main' && \
        apt-get clean && \
        apt-get update && \
        apt-get install -y mariadb-client

RUN mkdir -p /opt/app/policy /tmp/policy-install ${POLICY_LOGS} && \
    chown policy /opt/app/policy /tmp/policy-install ${POLICY_LOGS}

WORKDIR /tmp/policy-install

COPY install.zip docker-install.sh do-start.sh wait-for-port.sh ./
RUN unzip install.zip && rm install.zip && chown policy * && chmod +x *.sh

USER policy

ENTRYPOINT [ "bash", "./do-start.sh" ]

