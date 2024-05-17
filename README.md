
# Frmw
-creer AnnotationController 
-creer FrontController avec fonction qui recupere les class depuis package controller(web.xml) de TEST
-script pour envoyer les .jar dans lib de TEST

# TEST

-creer class TestController et autres en utilisant annotation du frmw
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
