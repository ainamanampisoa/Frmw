package mg.itu.prom16;
import java.util.HashMap;

public class ModelView {
    // Attributs
    private String url;
    private HashMap<String, Object> data;

    // Constructeur
    public ModelView(String url) {
        this.url = url;
        this.data = new HashMap<>();
    }

    // Getter pour l'URL
    public String getUrl() {
        return url;
    }

    // Setter pour l'URL
    public void setUrl(String url) {
        this.url = url;
    }

    // Getter pour les données
    public HashMap<String, Object> getData() {
        return data;
    }

    // Méthode pour ajouter un objet à la HashMap
    public void addObject(String key, Object value) {
        data.put(key, value);
    }

    // // Méthode main pour tester la classe
    // public static void main(String[] args) {
    //     // Création d'une instance de ModelView
    //     ModelView modelView = new ModelView("http://example.com");

    //     // Ajout de données
    //     modelView.addObject("username", "john_doe");
    //     modelView.addObject("age", 30);

    //     // Affichage de l'URL
    //     System.out.println("URL: " + modelView.getUrl());

    //     // Affichage des données
    //     System.out.println("Data: " + modelView.getData());
    // }
}
