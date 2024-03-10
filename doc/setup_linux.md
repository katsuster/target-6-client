
# Setup a Linux environment

The client side needs to use Linux desktop environment.
There is no limitaion about CPU architectures if the Linux environment supports Java/D-Bus/Bluez/Bluetooth stacks.

This document describes environments that someone has confirmed working correctly.


## ARM SBC boards

### Raspberry Pi 3 model B

### OKDO/Radxa ROCK 3 model C

ROCK 3 model C is a Raspberry Pi compatible size ARM board that supports HDMI output and Wi-Fi/Bluetooth 5.

* URL: https://wiki.radxa.com/Rock3/3c
* Image: `rock-3c_debian_bullseye_xfce_b42.img.xz`

How to disable blank screen and screen lock.

(Language: English)

* Xfce settings
  * Screensaver
    * Screensaver
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
      * スクリーンセーバーを有効にする: OFF
    * ロック画面
      * ロック画面を有効にする: OFF
  * 電源管理
    * ディスプレイ
      * ブランク画面にするまでの時間: しない
      * スリープするまでの時間: しない
      * 電源を切断するまでの時間: しない

How to enable automatically login.
Need to change the settings of LightDM.
This is an example to enable automatically login as username `rock`.

* Open `/etc/lightdm/lightdm.conf`
* Find `autologin-user`
* Rewirte such as `autologin-user=rock`

