#!/bin/sh

# - Repository path
#   - target-6-client (old): home directory of SBC
#   - target-6-client (new): /rock of USB storage
# - Mount USB storage
#   - Mount storage on /mnt/sda1
# - Run this script

set -e

# Default values

if [ "x${MEDIA_PATH}" = "x" ]; then
	MEDIA_PATH=/mnt/sda1
fi
REPO_NEW_PATH=${MEDIA_PATH}/rock/target-6-client

# Check path
if [ ! -d ${REPO_NEW_PATH}/.git ]; then
	echo "Please mount update media on ${MEDIA_PATH}"
	exit 1
fi

set -x

cd ~/target-6-client
if git remote show | grep update; then
	git remote remove update
fi
git remote add update ${REPO_NEW_PATH}
git fetch --prune --tags update
git rebase update/main
git remote remove update

rm -rf target
mvn package
