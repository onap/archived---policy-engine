#!/bin/bash
#
#============LICENSE_START==================================================
#  ONAP Policy Engine
#===========================================================================
#  Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
#===========================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#============LICENSE_END==================================================
#

#
echo '============== STARTING SCRIPT TO BUILD DOCKER IMAGES ================='
DOCKER_REPOSITORY=nexus3.onap.org:10003
MVN_VERSION=$(cat packages/docker/target/version)
MVN_MAJMIN_VERSION=$(cut -f 1,2 -d . packages/docker/target/version)
TIMESTAMP=$(date -u +%Y%m%dT%H%M%S)
PROXY_ARGS=""
IMAGE=policy-pe

if [ $HTTP_PROXY ]; then
    PROXY_ARGS+="--build-arg HTTP_PROXY=${HTTP_PROXY}"
fi
if [ $HTTPS_PROXY ]; then
    PROXY_ARGS+=" --build-arg HTTPS_PROXY=${HTTPS_PROXY}"
fi

echo $DOCKER_REPOSITORY
echo $MVN_VERSION
echo $MVN_MAJMIN_VERSION
echo $TIMESTAMP

if [[ -z $MVN_VERSION ]]
then
    echo "MVN_VERSION is empty"
    exit 1
fi

if [[ -z $MVN_MAJMIN_VERSION ]]
then
    echo "MVN_MAJMIN_VERSION is empty"
    exit 1
fi

if [[ $MVN_VERSION == *"SNAPSHOT"* ]]
then
    MVN_MAJMIN_VERSION="${MVN_MAJMIN_VERSION}-SNAPSHOT"
else
    MVN_MAJMIN_VERSION="${MVN_MAJMIN_VERSION}-STAGING"
fi

echo $MVN_MAJMIN_VERSION

echo "Building $IMAGE"

#
# This is the local latest tagged image. The Dockerfile's need this to build images
#
TAGS="--tag onap/${IMAGE}:latest"
#
# This is the nexus repo prepended for latest tagged image.
#
TAGS="${TAGS} --tag ${DOCKER_REPOSITORY}/onap/${IMAGE}:latest"
#
# This has the nexus repo prepended and only major/minor version with latest
#
TAGS="${TAGS} --tag ${DOCKER_REPOSITORY}/onap/${IMAGE}:${MVN_MAJMIN_VERSION}-latest"
#
# This has the nexus repo prepended and major/minor/patch version with timestamp
#
TAGS="${TAGS} --tag ${DOCKER_REPOSITORY}/onap/${IMAGE}:${MVN_VERSION}-STAGING-${TIMESTAMP}Z"

echo $TAGS

docker build --quiet ${PROXY_ARGS} $TAGS packages/docker/target/$IMAGE

if [ $? -ne 0 ]
then
    echo "Docker build failed"
    docker images
    exit 1
fi

docker images

echo "Pushing $IMAGE"

docker push ${DOCKER_REPOSITORY}/onap/${IMAGE}:latest

if [ $? -ne 0 ]
then
    echo "Docker push failed"
    exit 1

fi

docker push ${DOCKER_REPOSITORY}/onap/${IMAGE}:${MVN_MAJMIN_VERSION}-latest

if [ $? -ne 0 ]
then
    echo "Docker push failed"
    exit 1

fi
docker push ${DOCKER_REPOSITORY}/onap/${IMAGE}:${MVN_VERSION}-STAGING-${TIMESTAMP}Z

if [ $? -ne 0 ]
then
    echo "Docker push failed"
    exit 1

fi
