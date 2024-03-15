
# How to build

This document describes how to build and run this project.


## Prerequisites

Please install maven and OpenJDK to build this project.

```
# apt-get install vim git maven openjdk-17-jdk
```


## Build

Just run maven package.

```
$ mvn package
```

Maven shows such messages if all build processes are successful.

```
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  18.080 s
[INFO] Finished at: 2024-03-15T09:53:42+09:00
[INFO] ------------------------------------------------------------------------
```

The JAR file is generated and placed under target/ directory.

```
$ ls target/*.jar

target/target-6-client-0.1-jar-with-dependencies.jar
target/target-6-client-0.1.jar
```


## Run

Pass the generated JAR to java `-jar` and `--sb` to setup bluetooth devices.

```
$ java -jar target/target-6-client-0.1-jar-with-dependencies.jar --sb
```

The application shows opening window and start to connect BLE devices.
The `--debug` option can be used if you want to check internal logs.

```
$ java -jar target/target-6-client-0.1-jar-with-dependencies.jar
```
