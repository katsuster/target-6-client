
# Setup Client Machine

The client side needs to use Linux desktop environment.
There is no limitaion about CPU architectures if the Linux environment supports Java/D-Bus/Bluez/Bluetooth stacks.

This document describes environments that someone has confirmed working correctly.


## ARM SBC boards

### Raspberry Pi 3 model B

### OKDO/Radxa ROCK 3 model C

ROCK 3 model C is a Raspberry Pi compatible size ARM board that supports HDMI output and Wi-Fi/Bluetooth 5.

* URL: https://wiki.radxa.com/Rock3/3c
* Image: `rock-3c_debian_bullseye_xfce_b42.img.xz`

It would be better to set some settings if you plan to run this application for a long time:

* Disable blank screen
* Enable automatically login
* Hide taskbar panel

How to disable blank screen and screen lock.

(Language: English)

* Xfce settings
  * Screensaver
    * Screensaver
      * Activate screensaver when computer is idle: OFF (turn off before disabling screensaver)
      * Enable Screensaver: OFF
    * Lock Screen
      * Enable Lock Screen : OFF
  * Power Mnager
    * Display
      * Blank after: Never
      * Put to sleep after: Never
      * Switch off after: Never

(Language: Japanese)

* 設定
  * スクリーンセーバー
    * スクリーンセーバー
      * コンピューターがアイドル状態のときにスクリーンセーバーをアクティブにする: OFF (スクリーンセーバーを無効にする前にOFFにする)
      * スクリーンセーバーを有効にする: OFF
    * ロック画面
      * ロック画面を有効にする: OFF
  * 電源管理
    * ディスプレイ
      * ブランク画面にするまでの時間: しない
      * スリープするまでの時間: しない
      * 電源を切断するまでの時間: しない

If you use CUI:

Note: The value 14 of xfce4-power-manager/inactivity-on-ac means "Never".

```
(if you run via SSH)
export DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/$(id -u)/bus

(Screensaver)
xfconf-query -c xfce4-screensaver -p /saver/idle-activation/enabled -n -t bool -s false
xfconf-query -c xfce4-screensaver -p /saver/enabled -n -t bool -s false
xfconf-query -c xfce4-screensaver -p /lock/enabled  -n -t bool -s false

(Power Manager)
xfconf-query -c xfce4-power-manager -p /xfce4-power-manager/blank-on-ac      -n -t  int -s 0
xfconf-query -c xfce4-power-manager -p /xfce4-power-manager/dpms-on-ac-sleep -n -t uint -s 0
xfconf-query -c xfce4-power-manager -p /xfce4-power-manager/dpms-on-ac-off   -n -t uint -s 0
xfconf-query -c xfce4-power-manager -p /xfce4-power-manager/inactivity-on-ac -n -t uint -s 14
```

How to disable blank screen (DPMS) by using XDG and xset.
Please create ~/.config/autostart/xset.desktop file for disabling DPMS standby and suspend timer.

```
[Desktop Entry]
Name=xset
Comment=Disable DPM
Type=Application
Exec=/usr/bin/xset dpms 0 0 0
X-GNOME-Autostart-Phase=Initialization
```

How to enable automatically login.
Need to change the settings of LightDM.
This is an example to enable automatically login as username `rock`.

* Open `/etc/lightdm/lightdm.conf`
* Rewrite such as `autologin-user=rock`

```
[Seat:*]
autologin-user=rock
autologin-user-timeout=0
```

How to hide taskbar panel.
Right click on the taskbar panel on the top of screen.

* Panel
  * Panel Preferences...
    * Remove panel 2 (select Panel 2 and push "-" button)
    * Automatically hide the panel: select "Always"

If you use CUI:

```
(if you run via SSH)
export DBUS_SESSION_BUS_ADDRESS=unix:path=/run/user/$(id -u)/bus

(Remove panel 2)
xfconf-query -c xfce4-panel -p /panels -n -t int -s 1 --force-array
xfconf-query -c xfce4-panel -p /panels/panel-2 -r -R

(Automatically hide the panel: Always)
xfconf-query -c xfce4-panel -p /panels/panel-1/autohide-behavior -n -t uint -s 2
xfconf-query -c xfce4-panel -p /panels/panel-1/position-locked   -n -t bool -s true

(Restart panel to apply)
xfce4-panel -r
```

How to avoid X11 crash on resolution change.
After upgrading to Debian 12 (bookworm) (xserver-xorg-core 2:21.1.7, kernel 6.1.84 rk2410,
libmali-bifrost-g52 1.9-1), the X server crashes when the display resolution is changed
after login (e.g. by Xfce Display settings). LightDM then falls back to the login screen
and automatic login looks like it failed.

To avoid it, start X with the same resolution as the Xfce Display settings.
Add PreferredMode to the Monitor section of /etc/X11/xorg.conf.d/20-modesetting.conf such as:

```
Section "Monitor"
    Identifier  "Default Monitor"
    Option          "Rotate" "normal"
    Option          "PreferredMode" "1024x768"
EndSection
```


## Extend the life of storage

There are some tips that are especially useful for SD-Card for extending the life of SBC storage.

Stop zram swap service to prevent swap out RAM to storage.

```
# systemctl disable zramswap
```

Change temporary directory (/tmp) from file system on storage to tmpfs on RAM.

```
# vim /etc/fstab

tmpfs	/tmp	tmpfs	nodev,nosuid,size=80%,mode=1777	0	0
```

Forward log of journald to rsyslog to reduce write bytes.

```
# vim /etc/systemd/journald.conf

[Journal]
Storage=volatile
ForwardToSyslog=yes
```

Stop or change setting of systemd-timesyncd service that writes current time to file per 60 seconds.

```
(how to stop)

# systemctl disable systemd-timesyncd


(how to change)

# vim /etc/systemd/timesyncd.conf
SaveIntervalSec=2000

# systemctl restart systemd-timesyncd
```

