
# Single player mode

This document describes specification and implementation of single player mode.


## Specification

Single player mode has 2 games.

* Count up
  * Start by `cntup` command
  * Shoot targets as much as possible
  * Game is finished after 30 seconds
* Speed shoot
  * Start by `sshot` command
  * Shoot all targets as fast as possible (sequence is free)
  * Shoot stop target at last
    * Add penalty (+3 secs) for each non-shoot target
    * Max time is 30 secs
  * Game is finished after 30 seconds
* Time attack
  * Start by `tatk` command
  * Shoot highlighted target as fast as possible (sequence is random)
  * Pass the target if 30 seconds past
  * Game is finished after shoot/pass all targets

Single player mode is using 2 nodes at same time.

* Controller node (device ID: 0)
  * Detect push button
  * Beep the buzzer
  * Send start signal to sensor node
* Sensor node (device ID: 1)
  * Turn ON LEDs of target
  * Detect a hit of target
  * Send results to client


## Implementation

### State

Here is states diagram of controller and sensor node.

```mermaid
stateDiagram-v2

[*] --> init: Reset
state if_state <<choice>>
init --> if_state: init cmd
if_state --> controller: if dev == 0
if_state --> sensor: if dev > 0
controller --> init
sensor --> init

state controller {
  [*] --> init_controller
  init_controller --> single_wait1: single cmd
  init_controller --> [*]: init cmd
  single_wait1 --> single_wait2: set out0 High
  single_wait2 --> beep: passed 3 sec. set out0 Low
  beep --> beep_wait: output high for beep
  beep_wait --> [*]: beep 0.6 sec.
}

state sensor {
  [*] --> ...
  ... --> [*]
}
```

```mermaid
stateDiagram-v2

state sensor {
  [*] --> init_sensor
  init_sensor --> [*]: init cmd

  init_sensor --> cntup_wait1: cntup cmd
  cntup_wait1 --> cntup_wait2: LED OFF
  cntup_wait2 --> cntup_run: wait for falling edge, LED OFF
  cntup_run --> [*]: wait 30 seconds, game finished and send results
  cntup_run --> [*]: init cmd

  init_sensor --> sshot_wait1: sshot cmd
  sshot_wait1 --> sshot_wait2: LED OFF
  sshot_wait2 --> sshot_run: wait for falling edge, LED ON
  sshot_run --> [*]: wait for game finished, send results
  sshot_run --> [*]: init cmd

  init_sensor --> tatk_wait1: tatk cmd
  tatk_wait1 --> tatk_wait2: LED OFF
  tatk_wait2 --> tatk_run: wait for falling edge, LED OFF
  tatk_run --> [*]: wait for game finished, send results
  tatk_run --> [*]: init cmd
}
```


### Normal sequence

Here is normal sequence diagram of single mode game.

```mermaid
sequenceDiagram
participant client as Client
participant controller as ControllerNode
participant sensor as SensorNode
participant led as LED of target
participant target as Target
participant buzzer as Buzzer

activate client

client ->> controller: init 0 cmd
controller ->> controller: GPIO0 In/Hi-Z
controller -->> client: OK

client ->> sensor: init 1 cmd
sensor ->> led: All off
sensor ->> target: All disable
sensor -->> client: OK

client ->> sensor: tatk cmd
activate sensor
sensor ->> led: All on
activate led
sensor -->> client: OK

client ->> controller: single cmd
activate controller
controller ->> sensor: GPIO0 Out/High
controller -->> client: OK

controller ->> controller: Wait 3 sec.
controller ->> sensor: GPIO0 Out/Low (falling edge)
sensor ->> led: All off
deactivate led
sensor ->> target: Enable
activate target
sensor ->> led: On
activate led

controller ->> buzzer: Beep start
activate buzzer
controller ->> controller: Wait 0.6 sec.
controller ->> buzzer: Beep stop
deactivate buzzer
deactivate controller

target ->> sensor: Detect a hit
sensor ->> target: Disable
deactivate target
sensor ->> led: Off
deactivate led

target ->> sensor: Detect hits of all targets
sensor ->> client: Send results
deactivate sensor

deactivate client
```


### Cancel sequence

Here is a sequence for cancel the running game.

```mermaid
sequenceDiagram
participant client as Client
participant controller as ControllerNode
participant sensor as SensorNode
participant led as LED of target
participant target as Target
participant buzzer as Buzzer

activate client
activate controller
activate sensor

sensor ->> target: Enable
activate target
sensor ->> led: On
activate led

controller ->> buzzer: Beep start
activate buzzer
controller ->> controller: Wait 1.5 sec.

client ->> controller: init 0 cmd
controller ->> controller: GPIO0 In/Hi-Z
controller ->> buzzer: Beep stop
deactivate buzzer
controller -->> client: OK
deactivate controller

client ->> sensor: init 1 cmd
sensor ->> target: All disable
deactivate target
sensor ->> led: All off
deactivate led
sensor -->> client: OK
deactivate sensor

deactivate client
```
