FROM alpine:3.9

ARG HTTP_PROXY=${HTTP_PROXY}
ARG HTTPS_PROXY=${HTTPS_PROXY}
ARG BUILD_VERSION=${BUILD_VERSION}
ARG POLICY_LOGS=/var/log/onap

ENV http_proxy $HTTP_PROXY
ENV https_proxy $HTTPS_PROXY
ENV BUILD_VERSION ${BUILD_VERSION}
ENV POLICY_LOGS ${POLICY_LOGS}
ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk

RUN apk --update add --no-cache busybox-extras \
                                bash coreutils grep findutils \
                                zip unzip curl \
                                wget openssh maven jq \
                                httpie py-pip openjdk8

RUN adduser -S policy -s /bin/bash

# install MariaDB client
RUN apk --update add --no-cache mariadb-client

RUN mkdir -p /opt/app/policy /tmp/policy-install ${POLICY_LOGS} && \
    chown policy /opt/app/policy /tmp/policy-install ${POLICY_LOGS}

WORKDIR /tmp/policy-install

COPY install.zip docker-install.sh do-start.sh wait-for-port.sh ./
RUN unzip install.zip && rm install.zip && chown policy * && chmod +x *.sh

USER policy

ENTRYPOINT [ "bash", "./do-start.sh" ]

