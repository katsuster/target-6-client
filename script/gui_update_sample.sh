#!/bin/sh

# - Repository path
#   - target-6-client (old): home directory of SBC
#   - target-6-client (new): /rock of USB storage
# - Mount USB storage
#   - Mount storage on /mnt/sda1
# - Run this script

set -ex

xfce4-terminal --command="sh -c './update.sh | tee update.log; cat'"
