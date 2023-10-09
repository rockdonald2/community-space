#!/bin/bash

cd ../../backend || exit 1 # failed to change directory


# Test account-mgmt
echo
echo "===> Testing account-mgmt"
echo

cd account-mgmt/account-mgmt || exit 1
chmod +x gradlew
./gradlew test
cd ../..

# Test gateway
echo
echo "===> Testing gateway"
echo

cd gateway/gateway || exit 1
chmod +x gradlew
./gradlew test
cd ../..

# Test hub-mgmt
echo
echo "===> Testing hub-mgmt"
echo

cd hub-mgmt/hub-mgmt || exit 1
chmod +x gradlew
./gradlew test
cd ../..

# Test memo-mgmt
echo
echo "===> Testing memo-mgmt"
echo

cd memo-mgmt/memo-mgmt || exit 1
chmod +x gradlew
./gradlew test
cd ../..

# Test notification-mgmt
echo
echo "===> Testing activity-notifications-mgmt"
echo

cd activity-notifications-mgmt/activity-notifications-mgmt || exit 1
chmod +x gradlew
./gradlew test
cd ../..

cd ../devops/ci || exit 1 # change back to ci directory
