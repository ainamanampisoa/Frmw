package mg.itu.prom16;
import java.util.ArrayList;
import java.util.List;

public class Mapping {
    private String className;
    private List<VerbAction> actions;

    public Mapping(String className) {
        this.className = className;
        this.actions = new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<VerbAction> getActions() {
        return actions;
    }

    public void addAction(VerbAction action) {
        this.actions.add(action);
    }
}
