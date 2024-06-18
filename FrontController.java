package mg.itu.prom16;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrontController extends HttpServlet {
    private final Map<String, List<Mapping>> urlMapping = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        scanControllers(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>FrontController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1 style='color:blue'>URL actuelle :</h1>");
            out.println("<p>" + request.getRequestURL() + "</p>");

            String path = getPathInfo(request);
            List<Mapping> matchedMappings = urlMapping.get(path);

            if (matchedMappings != null && !matchedMappings.isEmpty()) {
                handleMappings(request, response, out, matchedMappings);
            } else {
                out.println("<h2 style='color:red'>Aucun mapping trouvé pour l'URL : " + path + "</h2>");
            }
            out.println("</body>");
            out.println("</html>");
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    private String getPathInfo(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path == null) {
            path = "/";
        } else if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    private void handleMappings(HttpServletRequest request, HttpServletResponse response, PrintWriter out, List<Mapping> matchedMappings) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ServletException, IOException {
        out.println("<h2>Liste des contrôleurs et leurs méthodes annotées :</h2>");
        for (Mapping mapping : matchedMappings) {
            out.println("<p>Classe: " + mapping.getControllerClass().getName() + "</p>");
            out.println("<p>Méthode: " + mapping.getMethod().getName() + "</p>");
            Object controllerInstance = mapping.getControllerClass().getDeclaredConstructor().newInstance();
            Object[] params = getMethodParameters(request, mapping.getMethod());
            Object result = mapping.getMethod().invoke(controllerInstance, params);

            if (result instanceof String) {
                out.println("<p>Valeur de retour: " + result + "</p>");
            } else if (result instanceof ModelView) {
                ModelView mv = (ModelView) result;
                out.println("<h3>Data:</h3>");
                for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                    out.println("<p>" + entry.getKey() + ": " + entry.getValue() + "</p>");
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
                out.println("<p>URL de destination: " + mv.getUrl() + "</p>");
                RequestDispatcher dispatcher = request.getRequestDispatcher(mv.getUrl());
                dispatcher.forward(request, response);
                return;
            } else {
                out.println("<p>Valeur de retour non reconnue</p>");
            }
            out.println("<hr>");
        }
    }

    private Object[] getMethodParameters(HttpServletRequest request, Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] parameterValues = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(GetParam.class)) {
                GetParam getParam = parameters[i].getAnnotation(GetParam.class);
                String paramName = getParam.value();
                String paramValue = request.getParameter(paramName);
                parameterValues[i] = paramValue;
            }
        }
        return parameterValues;
    }

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Error</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1 style='color:red'>Une erreur est survenue</h1>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private void scanControllers(ServletConfig config) {
        String controllerPackage = config.getInitParameter("controller-package");
        System.out.println("Scanning package: " + controllerPackage);

        try {
            String path = "WEB-INF/classes/" + controllerPackage.replace('.', '/');
            File directory = new File(getServletContext().getRealPath(path));
            if (directory.exists()) {
                scanDirectory(directory, controllerPackage);
            } else {
                System.out.println("Directory does not exist: " + directory.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scanDirectory(File directory, String packageName) {
        System.out.println("Scanning directory: " + directory.getAbsolutePath());

        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(AnnotationController.class)) {
                        for (Method method : clazz.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(GetAnnotation.class)) {
                                GetAnnotation requestMapping = method.getAnnotation(GetAnnotation.class);
                                String urlKey = requestMapping.value();
                                if (!urlKey.startsWith("/")) {
                                    urlKey = "/" + urlKey;
                                }
                                urlMapping.computeIfAbsent(urlKey, k -> new ArrayList<>()).add(new Mapping(urlKey, clazz, method));
                                System.out.println("Mapped URL: " + urlKey + " to " + clazz.getName() + "." + method.getName());
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
