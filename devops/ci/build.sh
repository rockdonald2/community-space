#!/bin/bash

cd ../../backend || exit 1 # failed to change directory

# Build common-lib
echo
echo "===> Building common-lib"
echo

cd common-library/common-library || exit 1
./gradlew publishToMavenLocal
cd ../..

# Build account-mgmt
echo
echo "===> Building account-mgmt"
echo

cd account-mgmt/account-mgmt || exit 1
./gradlew assemble
cd ../..

# Build gateway
echo
echo "===> Building gateway"
echo

cd gateway/gateway || exit 1
./gradlew assemble
cd ../..

# Build hub-mgmt
echo
echo "===> Building hub-mgmt"
echo

cd hub-mgmt/hub-mgmt || exit 1
./gradlew assemble
cd ../..

# Build memo-mgmt
echo
echo "===> Building memo-mgmt"
echo

cd memo-mgmt/memo-mgmt || exit 1
./gradlew assemble
cd ../..

# Build notification-mgmt
echo
echo "===> Building activity-notifications-mgmt"
echo

cd activity-notifications-mgmt/activity-notifications-mgmt || exit 1
./gradlew assemble
cd ../..

cd ../devops/ci || exit 1 # change back to ci directory
