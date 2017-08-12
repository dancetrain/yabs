#!/bin/sh



cd server/target
java -jar server-1.0-SNAPSHOT.jar ${1?"Usage: $0 PORT"}
