import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

@WebServlet("/FrontController")
public class FrontController extends HttpServlet {
    private boolean checked = false;
    private List<String> listeControllers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(request, response);
    }

    private synchronized void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>FrontController</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>URL actuelle :</h1>");
        out.println("<p>" + request.getRequestURL() + "</p>");

        if (!checked) {
            scanControllers();
            checked = true;
        }

        out.println("<h2>Liste des contrôleurs annotés avec @AnnotationController :</h2>");
        for (String controller : listeControllers) {
            out.println("<p>" + controller + "</p>");
        }

        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    private void scanControllers() {
        // Obtenez le package spécifié dans la configuration
        ServletConfig config = getServletConfig();
        String controllerPackage = config.getInitParameter("controller-package");

        // Utilisez Reflections pour scanner le package et récupérer les classes annotées avec @AnnotationController
        Reflections reflections = new Reflections(controllerPackage);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AnnotationController.class);

        // Ajoutez les noms des classes annotées à la liste des contrôleurs
        for (Class<?> clazz : annotatedClasses) {
            listeControllers.add(clazz.getName());
        }
    }
}
