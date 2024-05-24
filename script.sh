#!/bin/bash

DIR="D:\WORK\Frmw"

cd "$DIR"

javac -d . *.java

jar cf FrontController.jar mg
jar cf AnnotationController.jar mg
jar cf GetAnnotation.jar mg
jar cf Mapping.jar mg

mv FrontController.jar "D:\WORK\TEST\lib"
mv AnnotationController.jar "D:\WORK\TEST\lib"
mv GetAnnotation.jar "D:\WORK\TEST\lib"
mv Mapping.jar "D:\WORK\TEST\lib"

mv FrontController.jar "D:\WORK\TEST\lib"
mv AnnotationController.jar "D:\WORK\TEST\lib"

sleep 60
