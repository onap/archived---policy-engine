FROM onap/policy-common-alpine

LABEL maintainer="Policy Team"

ARG BUILD_VERSION_ENGINE=${BUILD_VERSION_ENGINE}
ARG POLICY_LOGS=/var/log/onap

ENV BUILD_VERSION_ENGINE ${BUILD_VERSION_ENGINE}
ENV POLICY_LOGS ${POLICY_LOGS}

RUN apk add --no-cache mariadb-client

RUN mkdir -p /tmp/policy-install ${POLICY_LOGS} && \
    chown policy:policy /tmp/policy-install ${POLICY_LOGS} && \
    rmdir ${POLICY_HOME}/etc/ssl && \
    rmdir ${POLICY_HOME}/etc

WORKDIR /tmp/policy-install

COPY /maven/install.zip docker-install.sh do-start.sh wait-for-port.sh ./
RUN unzip install.zip && rm install.zip && chown policy * && chmod +x *.sh

USER policy

ENTRYPOINT [ "bash", "./do-start.sh" ]

