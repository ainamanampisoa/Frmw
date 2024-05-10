#!/bin/bash

DIR="D:\WORK\FRW"

cd "$DIR"


javac -d . FrontController.java

jar cf FrontController.jar mg

mv FrontController.jar "D:\WORK\TEST\lib"

sleep 60
