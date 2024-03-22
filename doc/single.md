
# Single player mode

This document describes specification and implementation of single player mode.


## Specification

Single player mode has 1 game.

* Time attack
  * Shoot all 6 targets as fast as possible

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
  [*] --> init_sensor
  init_sensor --> six_wait1: six cmd
  init_sensor --> [*]: init cmd
  six_wait1 --> six_wait2: LED ON
  six_wait2 --> six_run: wait for falling edge, LED OFF
  six_run --> [*]: wait for game finished, send results
  six_run --> [*]: init cmd
}
```


### Normal sequence

Here is normal sequence diagram of single mode game.

```mermaid
sequenceDiagram
participant client as Client
participant controller as ControllerNode
participant sensor as SensorNode
participant led as LED
participant target as Target
participant buzzer as Buzzer

activate client

client ->> controller: init 0 cmd
controller ->> controller: GPIO0 In/Hi-Z
controller -->> client: OK

client ->> sensor: init 1 cmd
sensor ->> led: All off
sensor ->> target: All off
sensor -->> client: OK

client ->> sensor: six cmd
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
sensor ->> target: LED on
activate target

controller ->> buzzer: Beep start
activate buzzer
controller ->> controller: Wait 0.6 sec.
controller ->> buzzer: Beep stop
deactivate buzzer
deactivate controller

target ->> sensor: Detect hits of all targets
deactivate target
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
participant led as LED
participant target as Target
participant buzzer as Buzzer

activate client
activate controller
activate sensor

sensor ->> target: LED on
activate target

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
sensor ->> led: All off
sensor ->> target: All off
deactivate target
sensor -->> client: OK
deactivate sensor

deactivate client
```
