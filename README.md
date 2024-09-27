
EXEMPLE d'utilisation:

-ajouter username dans la session
-verifier si cet user existe et peut se connecter
-afficher les donnees correspondantes par cet user dans dataList.jsp

--------------------------------------------CONTROLLER---------------------------------------------------------
package mg.test.controller;
import mg.itu.prom16.AnnotationController;
import mg.itu.prom16.GetAnnotation;
import mg.itu.prom16.GetParam;
import mg.itu.prom16.Post;
import mg.itu.prom16.ModelView;
import mg.itu.prom16.RequestBody;
import mg.itu.prom16.MySession;

@AnnotationController
public class Controller_1 {

    @GetAnnotation("loginForm")
    public ModelView loginForm() {
        return new ModelView("/login.jsp");
    }


    @Restapi
    @Post("login")
    public ModelView login(@GetParam("username") String username, @GetParam("password") String password, MySession session) {
        ModelView mv;
        User user = UserDataStore.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            session.add("username", username);
            mv = new ModelView("/dataList.jsp");
            mv.addObject("user", user);
        } else {
            mv = new ModelView("/login.jsp");
            mv.addObject("error", "Invalid username or password");
        }
        return mv;
    }

    @Restapi
    @Post("formList")
    public ModelView formList(@RequestBody User user, MySession session) {
        UserDataStore.addUser(user);
        session.add("username", user.getUsername());
        ModelView mv = new ModelView("/dataList.jsp");
        mv.addObject("user", user);
        return mv;
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

