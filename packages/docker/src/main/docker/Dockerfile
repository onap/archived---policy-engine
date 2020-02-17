FROM onap/policy-jdk-debian

LABEL maintainer="Policy Team"

ARG BUILD_VERSION_ENGINE=${BUILD_VERSION_ENGINE}
ARG POLICY_LOGS=/var/log/onap

ENV BUILD_VERSION_ENGINE ${BUILD_VERSION_ENGINE}
ENV POLICY_LOGS ${POLICY_LOGS}

RUN apt update && \
    apt-get install -y mariadb-client && \
    apt-get install -y netcat && \
    apt-get install -y cron && \
    mkdir -p /tmp/policy-install ${POLICY_LOGS} && \
    chown policy:policy /tmp/policy-install ${POLICY_LOGS}

WORKDIR /tmp/policy-install

COPY /maven/install.zip docker-install.sh do-start.sh wait-for-port.sh ./
RUN unzip install.zip && rm install.zip && chown policy * && chmod +x *.sh

USER policy

ENTRYPOINT [ "bash", "./do-start.sh" ]

