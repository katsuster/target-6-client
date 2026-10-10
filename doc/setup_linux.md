
# Setup Client Machine

The client side needs to use Linux desktop environment.
There is no limitaion about CPU architectures if the Linux environment supports Java/D-Bus/Bluez/Bluetooth stacks.

This document describes environments that someone has confirmed working correctly.


## ARM SBC boards

### Raspberry Pi 3 model B


### OKDO/Radxa ROCK 3 model C (Debian 11 + Xfce)

* [ROCK 3 model C (Debian 11 + Xfce)](setup_linux_rock3c_xfce.md)


### OKDO/Radxa ROCK 3 model C (Debian 12 + KDE)


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

