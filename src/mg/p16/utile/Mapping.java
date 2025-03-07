package mg.p16.utile;

import java.util.ArrayList;
import java.util.List;

public class Mapping {

    private String className;
    private List<VerbAction> verbActions;
    private boolean needAuth = false;
    private String profil;

    public Mapping(String className) {
        this.className = className;
        this.verbActions = new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }

    public void setVerbActions(VerbAction verbAction) {
        this.verbActions.add(verbAction);
    }

    public List<VerbAction> getVerbActions() {
        return verbActions;
    }

    public void setVerbActions(List<VerbAction> verbActions) {
        this.verbActions = verbActions;
    }

    public boolean isVerbPresent(String verbToCheck) {
        for (VerbAction action : this.verbActions) {
            if (action.getVerb().equalsIgnoreCase(verbToCheck)) {
                return true;
            }
        }
        return false;
    }

    public void addVerbAction(VerbAction verbAction) throws Exception {
        for (VerbAction v : verbActions) {
            if (v.getVerb().equals(verbAction.getVerb())) {
                throw new Exception("Conflit : L'URL possède déjà une méthode " + verbAction.getVerb());
            }
        }
        verbActions.add(verbAction);
    }

    public boolean isVerbAction(VerbAction verbToCheck) {
        for (VerbAction action : this.verbActions) {
            if (action.getVerb().equalsIgnoreCase(verbToCheck.getVerb())
                    && action.getMethodeName().equalsIgnoreCase(verbToCheck.getMethodeName())) {
                return true;
            }
        }
        return false;
    }

    public void setNeedAuth(boolean needAuth) {
        this.needAuth = needAuth;
    }

    public boolean isNeedAuth() {
        return needAuth;
    }

    public void setProfil(String profil) {
        this.profil = profil;
    }

    public String getProfil() {
        return profil;
    }
}

