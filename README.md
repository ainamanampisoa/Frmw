
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

    @Post("formList")
    public ModelView formList(@RequestBody User user, MySession session) {
        UserDataStore.addUser(user);
        session.add("username", user.getUsername());
        ModelView mv = new ModelView("/dataList.jsp");
        mv.addObject("user", user);
        return mv;
    }

    @GetAnnotation("logout")
    public ModelView logout(MySession session) {
        session.delete("username");
        session.delete("user");
        ModelView mv = new ModelView("/login.jsp");
        mv.addObject("message", "Logged out successfully");
        return mv;
    }
}
--------------------------------------login.jsp------------------------------------------------------------
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>
    <h2>Login</h2>
    <form action="login" method="POST">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username"><br><br>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password"><br><br>
        <input type="submit" value="Login">
    </form>
    <c:if test="${not empty error}">
        <p style="color:red;">${error}</p>
    </c:if>
</body>
</html>
--------------------------------------dataListjsp--------------------------------------------------------------
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Data List</title>
</head>
<body>
    <h2>Data List</h2>
    <c:if test="${not empty user}">
        <p>Username: ${user.username}</p>
        <p>Nom: ${user.nom}</p>
        <p>Adresse: ${user.adresse}</p>
    </c:if>
    <form action="logout" method="POST">
        <input type="submit" value="Logout">
    </form>
</body>
</html>
----------------------------------------------------------x----------------------------------------------------
  
Les donnees sont stockees dans une class:

package mg.test.controller;

import java.util.HashMap;
import java.util.Map;

public class UserDataStore {
    private static Map<String, User> users = new HashMap<>();

    public static void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public static User getUserByUsername(String username) {
        return users.get(username);
    }

    // Initialiser quelques utilisateurs par défaut
    static {
        User defaultUser1 = new User();
        defaultUser1.setUsername("miandry@miandry");
        defaultUser1.setPassword("a");
        defaultUser1.setNom("Miandry");
        defaultUser1.setAdresse("Lot EA64");

        User defaultUser2 = new User();
        defaultUser2.setUsername("aina@aina");
        defaultUser2.setPassword("b");
        defaultUser2.setNom("Aina");
        defaultUser2.setAdresse("Lot || H 18 Bis");

        addUser(defaultUser1);
        addUser(defaultUser2);
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

