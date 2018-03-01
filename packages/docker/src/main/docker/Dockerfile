FROM onap/policy/policy-base

RUN mkdir -p /opt/app/policy /tmp/policy-install && chown policy /opt/app/policy /tmp/policy-install

WORKDIR /tmp/policy-install

COPY install.zip docker-install.sh do-start.sh wait-for-port.sh ./
RUN unzip install.zip && rm install.zip && chown policy * && chmod +x *.sh

USER policy

ENTRYPOINT [ "bash", "./do-start.sh" ]

