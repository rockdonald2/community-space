#!/bin/bash

cd ../../backend || exit 1 # failed to change directory

# Deploy account-mgmt
echo
echo "===> Deploying account-mgmt"
echo

cd account-mgmt/account-mgmt || exit 1
docker build -t account-mgmt .
docker tag account-mgmt:latest registry.gitlab.com/rockdonald2/community-space:account-mgmt-latest
docker push registry.gitlab.com/rockdonald2/community-space:account-mgmt-latest
cd ../..

# Deploy gateway
echo
echo "===> Deploying gateway"
echo

cd gateway/gateway || exit 1
docker build -t gateway .
docker tag gateway:latest registry.gitlab.com/rockdonald2/community-space:gateway-latest
docker push registry.gitlab.com/rockdonald2/community-space:gateway-latest
cd ../..

# Deploy hub-mgmt
echo
echo "===> Deploying hub-mgmt"
echo

cd hub-mgmt/hub-mgmt || exit 1
docker build -t hub-mgmt .
docker tag hub-mgmt:latest registry.gitlab.com/rockdonald2/community-space:hub-mgmt-latest
docker push registry.gitlab.com/rockdonald2/community-space:hub-mgmt-latest
cd ../..

# Deploy memo-mgmt
echo
echo "===> Deploying memo-mgmt"
echo

cd memo-mgmt/memo-mgmt || exit 1
docker build -t memo-mgmt .
docker tag memo-mgmt:latest registry.gitlab.com/rockdonald2/community-space:memo-mgmt-latest
docker push registry.gitlab.com/rockdonald2/community-space:memo-mgmt-latest
cd ../..

# Deploy notification-mgmt
echo
echo "===> Deploying activity-notifications-mgmt"
echo

cd activity-notifications-mgmt/activity-notifications-mgmt || exit 1
docker build -t activity-notifications-mgmt .
docker tag activity-notifications:latest registry.gitlab.com/rockdonald2/community-space:activity-notifications-latest
docker push registry.gitlab.com/rockdonald2/community-space:activity-notifications-latest
cd ../..

# Deploy front-end
echo
echo "===> Deploying front-end"
echo

cd ../frontend/community-space || exit 1
docker build -t community-space .
docker tag community-space:latest registry.gitlab.com/rockdonald2/community-space:web-latest
docker push registry.gitlab.com/rockdonald2/community-space:web-latest
# shellcheck disable=SC2103
cd ..

cd ../devops/ci || exit 1 # change back to ci directory
