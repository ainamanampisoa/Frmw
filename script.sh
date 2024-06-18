#!/bin/bash

DIR="D:\WORK\Frmw"

cd "$DIR"

javac -d . *.java

jar cf FrontController.jar mg
jar cf AnnotationController.jar mg
jar cf GetAnnotation.jar mg
jar cf Mapping.jar mg
jar cf ModelView.jar mg
jar cf GetParam.jar mg

mv FrontController.jar "D:\WORK\TEST\lib"
mv AnnotationController.jar "D:\WORK\TEST\lib"
mv GetAnnotation.jar "D:\WORK\TEST\lib"
mv Mapping.jar "D:\WORK\TEST\lib"
mv ModelView.jar "D:\WORK\TEST\lib"
mv GetParam.jar "D:\WORK\TEST\lib"

sleep 60
