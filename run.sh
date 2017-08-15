#!/bin/sh



cd server/target
java -jar yabs-server.jar ${1?"Usage: $0 PORT"}
