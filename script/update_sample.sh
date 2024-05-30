#!/bin/sh

# - Repository path
#   - target-6-client (old): home directory of SBC
#   - target-6-client (new): /rock of USB storage
# - Mount USB storage
#   - Mount storage on /mnt/sda1
# - Run this script

set -ex

cd ~/target-6-client
git remote add update /mnt/sda1/rock/target-6-client
git fetch --prune --tags update
git rebase update/main
git remote remove update

rm -r target
mvn package

