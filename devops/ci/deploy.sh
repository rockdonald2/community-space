#!/bin/bash

cd ../../backend || exit 1 # failed to change directory

# login to registry
# docker login registry.gitlab.com

# Deploy account-mgmt
echo
echo "===> Deploying account-mgmt"
echo

cd account-mgmt/account-mgmt || exit 1
docker build -t account-mgmt .
cd ../..

# Deploy gateway
echo
echo "===> Deploying gateway"
echo

cd gateway/gateway || exit 1
docker build -t gateway .
cd ../..

# Deploy hub-mgmt
echo
echo "===> Deploying hub-mgmt"
echo

cd hub-mgmt/hub-mgmt || exit 1
docker build -t hub-mgmt .
cd ../..

# Deploy memo-mgmt
echo
echo "===> Deploying memo-mgmt"
echo

cd memo-mgmt/memo-mgmt || exit 1
docker build -t memo-mgmt .
cd ../..

# Deploy notification-mgmt
echo
echo "===> Deploying activity-notifications-mgmt"
echo

cd activity-notifications-mgmt/activity-notifications-mgmt || exit 1
docker build -t activity-notifications-mgmt .
cd ../..

# Deploy front-end
echo
echo "===> Deploying front-end"
echo

cd ../frontend/community-space || exit 1
docker build -t community-space .
# shellcheck disable=SC2103
cd ..

cd ../devops/ci || exit 1 # change back to ci directory
