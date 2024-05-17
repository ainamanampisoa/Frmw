#!/bin/bash

# Définir le chemin vers le répertoire où se trouve votre classe FrontController
DIR="D:\WORK\Frmw"

# Déplacer dans le répertoire contenant votre classe FrontController
cd "$DIR"

# Compiler le fichier FrontController.java
javac -d . *.java

# Créer un fichier JAR en incluant le fichier compilé FrontController.class
jar cf FrontController.jar mg
jar cf AnnotationController.jar mg

# Déplacer le fichier JAR créé dans le répertoire souhaité
mv FrontController.jar "D:\WORK\TEST\lib"
mv AnnotationController.jar "D:\WORK\TEST\lib"

sleep 60
