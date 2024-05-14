#!/bin/bash

DIR="D:\WORK\FRW"

cd "$DIR"


javac -d . *.java

jar cf FrontController.jar mg
jar cf AnnotationController.jar mg


mv FrontController.jar "D:\WORK\TEST\lib"
mv AnnotationController.jar "D:\WORK\TEST\lib"

sleep 60
