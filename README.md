
# Frmw
    -creation de GetAnnotation
    -changement dans processRequest
# TEST
-ajouter une class annote par AnnotationController puis ajouter des methodes annotes par GetAnnotation
-annoter les methodes de cette maniere GetAnnotation("emplist") 

EXEMPLE:

@AnnotationController("Employe")
public class Employe {

    @GetAnnotation("emplist")
    public void liste(){

    }

    @GetAnnotation("emplist")
    public void getliste(){

    }

    @GetAnnotation("liste")
    public void getlistes(){

    }

}

-web.xml contenant le package controller(package de votre controller)
(exemple:<servlet>
            <servlet-name>FrontController</servlet-name>
            <servlet-class>mg.itu.prom16.FrontController</servlet-class>
            <init-param>
                <param-name>controller-package</param-name>
                <param-value>controller</param-value>
            </init-param>
         </servlet>)
-script pour deployer dans webapps

