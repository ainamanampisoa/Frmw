package mg.itu.prom16;
public class VerbAction {
    private String verb;
    private String methodName;

    public VerbAction(String verb, String methodName) {
        this.verb = verb;
        this.methodName = methodName;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
