package mg.itu.prom16;

import mg.itu.prom16.AnnotationController;
import mg.itu.prom16.GetAnnotation;
import mg.itu.prom16.Post;
import mg.itu.prom16.GetParam;
import mg.itu.prom16.VerbAction;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import com.fasterxml.jackson.databind.ObjectMapper;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 50,      // 50MB
    maxRequestSize = 1024 * 1024 * 100   // 100MB
)
public class FrontController extends HttpServlet {
    private List<String> controller = new ArrayList<>();
    private String controllerPackage;
    boolean checked = false;
    HashMap<String, Mapping> lien = new HashMap<>();
    String error = "";

    @Override
    public void init() throws ServletException {
        super.init();
        controllerPackage = getInitParameter("controller-package");
        try {
            this.scan();
        } catch (Exception e) {
            error = e.getMessage();
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String[] requestUrlSplitted = request.getRequestURL().toString().split("/");
        String controllerSearched = requestUrlSplitted[requestUrlSplitted.length - 1];

        response.setContentType("text/html");

        if (!error.isEmpty()) {
            out.println(error);
        } else if (!lien.containsKey(controllerSearched)) {
            out.println("<p>Méthode non trouvée.</p>");
        } else {
            try {
                Mapping mapping = lien.get(controllerSearched);
                Class<?> clazz = Class.forName(mapping.getClassName());
                Method method = null;

                // Trouver l'action correspondant au verbe HTTP de la requête
                for (VerbAction action : mapping.getActions()) {
                    if (action.getVerb().equalsIgnoreCase(request.getMethod())) {
                        method = clazz.getMethod(action.getMethodName());
                        break;
                    }
                }

                if (method == null) {
                    out.println("<p>Aucune méthode correspondante trouvée pour le verbe " + request.getMethod() + ".</p>");
                    return;
                }

                // Injecter les paramètres, y compris les fichiers
                Object[] parameters = getMethodParameters(method, request);

                Object object = clazz.getDeclaredConstructor().newInstance();
                Object returnValue = method.invoke(object, parameters);

                // Vérifier l'annotation Restapi
                if (method.isAnnotationPresent(Restapi.class)) {
                    // Convertir en JSON et envoyer la réponse
                    response.setContentType("application/json");
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonResponse = "";

                    if (returnValue instanceof ModelView) {
                        ModelView modelView = (ModelView) returnValue;
                        jsonResponse = objectMapper.writeValueAsString(modelView.getData());
                    } else {
                        jsonResponse = objectMapper.writeValueAsString(returnValue);
                    }

                    out.println(jsonResponse);
                } else {
                    // Continuer le traitement si Restapi n'est pas présent
                    if (returnValue instanceof String) {
                        out.println("Méthode trouvée dans " + returnValue);
                    } else if (returnValue instanceof ModelView) {
                        ModelView modelView = (ModelView) returnValue;
                        for (Map.Entry<String, Object> entry : modelView.getData().entrySet()) {
                            request.setAttribute(entry.getKey(), entry.getValue());
                        }
                        RequestDispatcher dispatcher = request.getRequestDispatcher(modelView.getUrl());
                        dispatcher.forward(request, response);
                    } else {
                        out.println("Type de données non reconnu");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    public void scan() throws Exception {
        try {
            String classesPath = getServletContext().getRealPath("/WEB-INF/classes");
            String decodedPath = URLDecoder.decode(classesPath, "UTF-8");
            String packagePath = decodedPath + "\\" + controllerPackage.replace('.', '\\');
            File packageDirectory = new File(packagePath);
            if (!packageDirectory.exists() || !packageDirectory.isDirectory()) {
                throw new Exception("Package n'existe pas");
            } else {
                File[] classFiles = packageDirectory.listFiles((dir, name) -> name.endsWith(".class"));
                if (classFiles != null) {
                    for (File classFile : classFiles) {
                        String className = controllerPackage + '.'
                                + classFile.getName().substring(0, classFile.getName().length() - 6);
                        try {
                            Class<?> classe = Class.forName(className);
                            if (classe.isAnnotationPresent(AnnotationController.class)) {
                                controller.add(classe.getSimpleName());

                                Mapping map = new Mapping(className);
                                Method[] methodes = classe.getDeclaredMethods();

                                for (Method methode : methodes) {
                                    if (methode.isAnnotationPresent(GetAnnotation.class)) {
                                        VerbAction getAction = new VerbAction("GET", methode.getName());
                                        map.addAction(getAction);
                                    } else if (methode.isAnnotationPresent(Post.class)) {
                                        VerbAction postAction = new VerbAction("POST", methode.getName());
                                        map.addAction(postAction);
                                    }
                                }

                                String valeur = methodes[0].isAnnotationPresent(GetAnnotation.class) ?
                                        methodes[0].getAnnotation(GetAnnotation.class).value() :
                                        methodes[0].getAnnotation(Post.class).value();

                                if (lien.containsKey(valeur)) {
                                    throw new Exception("double url " + valeur);
                                } else {
                                    lien.put(valeur, map);
                                }
                            }
                        } catch (Exception e) {
                            throw e;
                        }
                    }
                } else {
                    throw new Exception("Le package est vide");
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private Object[] getMethodParameters(Method method, HttpServletRequest request) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] parameterValues = new Object[parameters.length];

        HttpSession session = request.getSession();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            // Cas pour les fichiers (Part)
            if (parameter.getType() == Part.class) {
                parameterValues[i] = request.getPart(parameter.getName());
            } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                parameterValues[i] = createRequestBodyParameter(parameter, request.getParameterMap());
            } else if (parameter.isAnnotationPresent(GetParam.class)) {
                GetParam param = parameter.getAnnotation(GetParam.class);
                parameterValues[i] = request.getParameter(param.value());
            } else {
                throw new IllegalArgumentException("Paramètre non supporté pour cette méthode");
            }
        }

        return parameterValues;
    }

    private Object createRequestBodyParameter(Parameter parameter, Map<String, String[]> paramMap) throws Exception {
        Class<?> paramType = parameter.getType();
        Object paramObject = paramType.getDeclaredConstructor().newInstance();
        for (Field field : paramType.getDeclaredFields()) {
            String paramName = field.getName();
            if (paramMap.containsKey(paramName)) {
                String paramValue = paramMap.get(paramName)[0];
                field.setAccessible(true);
                field.set(paramObject, paramValue);
            }
        }
        return paramObject;
    }

    private String getControllerFromRequest(HttpServletRequest request) {
        // Extraire le nom du contrôleur à partir de l'URL de la requête
        String[] requestUrlSplitted = request.getRequestURL().toString().split("/");
        return requestUrlSplitted[requestUrlSplitted.length - 1];
    }
}
