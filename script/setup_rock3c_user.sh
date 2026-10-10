#!/bin/sh

# Setup Xfce settings of ROCK 3 model C for running this application
# (see doc/setup_linux.md)
#
# - Disable screensaver and screen lock
# - Disable blank screen and sleep of Power Manager
# - Disable DPMS timer by ~/.config/autostart/xset.desktop
# - Remove panel 2 and hide panel 1 automatically
#
# - Login to Xfce desktop as the user who runs this application
#   - Automatic login is also OK
# - Run this script as the user (not root)
#   - Run on terminal of the desktop, or via SSH
#   - sh setup_rock3c_user.sh

set -e

# if you run via SSH
if [ "x${DBUS_SESSION_BUS_ADDRESS}" = "x" ]; then
	export DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/$(id -u)/bus
fi
if [ "x${DISPLAY}" = "x" ]; then
	export DISPLAY=:0
fi

set -x

# Screensaver
xfconf-query -c xfce4-screensaver -p /saver/idle-activation/enabled -n -t bool -s false
xfconf-query -c xfce4-screensaver -p /saver/enabled -n -t bool -s false
xfconf-query -c xfce4-screensaver -p /lock/enabled  -n -t bool -s false

# Power Manager
xfconf-query -c xfce4-power-manager -p /xfce4-power-manager/blank-on-ac      -n -t  int -s 0
xfconf-query -c xfce4-power-manager -p /xfce4-power-manager/dpms-on-ac-sleep -n -t uint -s 0
xfconf-query -c xfce4-power-manager -p /xfce4-power-manager/dpms-on-ac-off   -n -t uint -s 0
xfconf-query -c xfce4-power-manager -p /xfce4-power-manager/inactivity-on-ac -n -t uint -s 14

# Disable DPMS standby and suspend timer
mkdir -p ~/.config/autostart
cat > ~/.config/autostart/xset.desktop <<EOF
[Desktop Entry]
Name=xset
Comment=Disable DPM
Type=Application
Exec=/usr/bin/xset dpms 0 0 0
X-GNOME-Autostart-Phase=Initialization
EOF

# Remove panel 2
xfconf-query -c xfce4-panel -p /panels -n -t int -s 1 --force-array
xfconf-query -c xfce4-panel -p /panels/panel-2 -r -R

# Automatically hide the panel: Always
xfconf-query -c xfce4-panel -p /panels/panel-1/autohide-behavior -n -t uint -s 2
xfconf-query -c xfce4-panel -p /panels/panel-1/position-locked   -n -t bool -s true

# Restart panel to apply
xfce4-panel -r
