package lexicoSintaticoSemantico;

public class AnalysisError extends Exception {
    private int position;
    private String lexema;

    public AnalysisError(String msg, int position, String lexema) {
        super(msg);
        this.position = position;
        this.lexema = lexema;
    }

    public AnalysisError(String msg) {
        super(msg);
        this.position = -1;
    }

    public AnalysisError(String msg, int position) {
        super(msg);
        this.position = position;
    }

    public String getLexema() {
        return lexema;
    }

    public int getPosition() {
        return position;
    }

    public String toString() {
        return super.toString() + ", @ " + position;
    }
}
