package lexicoSintaticoSemantico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Semantico implements Constants {
    public String codigoCompilado = "";
    private List<String> idList = new ArrayList<>();
    private String currentType;
    private Token currentToken;
    private Map<String, String> symbolTable = new HashMap<>(); // name -> MSIL type
    private Stack<String> typeStack = new Stack<>();
    private Stack<String> operatorStack = new Stack<>();
    private int labelCount = 0;

    public void executeAction(int action, Token token) throws SemanticError {
        System.out.println("Ação #" + action + ", Token: " + token);
        System.out.println("Token: " + token);
        this.currentToken = token;

        switch (action) {
            case 101:
                action101();
                break;
            case 102:
                action102();
                break;
            case 103:
                action103();
                break;
            case 104:
                action104();
                break;
            case 105:
                action105();
                break;
            case 106:
                action106();
                break;
            case 107:
                action107();
                break;
            case 108:
                action108(token);
                break;
            case 109:
                action109();
                break;
            case 110:
                action110();
                break;
            case 111:
                action111();
                break;
            case 112:
                action112();
                break;
            case 113:
                action113();
                break;
            case 114:
                action114();
                break;
            case 115:
                action115();
                break;
            case 116:
                action116();
                break;
            case 117:
                action117();
                break;
            case 118:
                action118();
                break;
            case 119:
                action119();
                break;
            case 120:
                action120();
                break;
            case 121:
                action121();
                break;
            case 122:
                action122();
                break;
            case 123:
                action123();
                break;
            case 124:
                action124();
                break;
            case 125:
                action125();
                break;
            case 126:
                action126();
                break;
            case 127:
                action127();
                break;
            case 128:
                action128();
                break;
            case 129:
                action129();
                break;
            case 130:
                action130();
                break;
            case 131:
                action131(token);
                break;
            case 132:
                action132();
                break;
            case 133:
                action133();
                break;
            default:
                throw new SemanticError("Ação semântica desconhecida: " + action);
        }
    }

    private String newLabel() {
        return "L" + labelCount++;
    }

    public void action101() {
        codigoCompilado += """
                // cabeçalho
                .assembly extern mscorlib {}
                .assembly _programa{}
                .module _programa.exe
                .class public _programa{
                .method static public void _principal(){
                .entrypoint
                // corpo
                """;
    }

    public void action102() {
        codigoCompilado += """
                ret
                }
                }
                """;
    }

    public void action103() {
        switch (currentToken.getLexeme()) {
            case "int":
                currentType = "int32";
                break;
            case "float":
                currentType = "float64";
                break;
            case "bool":
                currentType = "bool";
                break;
            case "char":
                currentType = "char";
                break;
            case "string":
                currentType = "string";
                break;
        }
    }

    public void action104() throws SemanticError {
        for (String id : idList) {
            if (symbolTable.containsKey(id)) {
                throw new SemanticError("Identificador '" + id + "' já declarado.");
            }
            symbolTable.put(id, currentType);
            codigoCompilado += String.format("    .locals init (%s %s)\n", currentType, id);
        }
        idList.clear();
    }

    public void action105() {
        idList.add(currentToken.getLexeme());
    }

    public void action106() throws SemanticError {
        String id = idList.remove(0);
        if (!symbolTable.containsKey(id)) {
            throw new SemanticError("Identificador '" + id + "' não declarado.");
        }
        String targetType = symbolTable.get(id);
        String expressionType = typeStack.pop();

        if (!targetType.equals(expressionType)) {
            if (targetType.equals("float64") && expressionType.equals("int32")) {
                codigoCompilado += "    conv.r8\n";
            } else {
                throw new SemanticError("Tipos incompatíveis no comando de atribuição para '" + id + "'.");
            }
        }

        codigoCompilado += String.format("    stloc %s\n", id);
    }

    public void action107() throws SemanticError {
        for (String id : idList) {
            if (!symbolTable.containsKey(id)) {
                throw new SemanticError("Identificador '" + id + "' não declarado.");
            }
            String type = symbolTable.get(id);
            codigoCompilado += "    call string [mscorlib]System.Console::ReadLine()\n";
            switch (type) {
                case "int32":
                    codigoCompilado += "    call int32 [mscorlib]System.Int32::Parse(string)\n";
                    break;
                case "float64":
                    codigoCompilado += "    call float64 [mscorlib]System.Double::Parse(string)\n";
                    break;
                case "bool":
                    codigoCompilado += "    call bool [mscorlib]System.Boolean::Parse(string)\n";
                    break;
            }
            codigoCompilado += String.format("    stloc %s\n", id);
        }
        idList.clear();
    }

    public void action108(Token token) throws SemanticError {
        if (token.getLexeme().equals("\\n")) {
            System.out.println("Nova linha detectada.");
            codigoCompilado += String.format("    call void [mscorlib]System.Console::WriteLine(%s)\n", "string");
        } else {
            String type = typeStack.pop();
            codigoCompilado += String.format("    call void [mscorlib]System.Console::Write(%s)\n", type);
        }
    }

    public void action109() {
        String endLabel = newLabel();
        operatorStack.push(endLabel);
    }

    public void action110() {
        String endLabel = operatorStack.pop();
        codigoCompilado += String.format("%s:\n", endLabel);
    }

    public void action111() {
        String nextLabel = newLabel();
        operatorStack.push(nextLabel);
        codigoCompilado += "    dup\n";
    }

    public void action112() {
        codigoCompilado += "    ceq\n";
        String nextLabel = operatorStack.peek();
        codigoCompilado += String.format("    brfalse %s\n", nextLabel);
    }

    public void action113() {
        String endLabel = operatorStack.peek();
        codigoCompilado += String.format("    br %s\n", endLabel);
        String nextLabel = operatorStack.pop();
        codigoCompilado += String.format("%s:\n", nextLabel);
    }

    public void action114() {
        String startLabel = newLabel();
        operatorStack.push(startLabel);
        codigoCompilado += String.format("%s:\n", startLabel);
    }

    public void action115() {
        String startLabel = operatorStack.pop();
        codigoCompilado += String.format("    brtrue %s\n", startLabel);
    }

    public void action116() {
        String startLabel = operatorStack.pop();
        codigoCompilado += String.format("    brfalse %s\n", startLabel);
    }

    public void action117() { // &
        typeStack.pop();
        typeStack.pop();
        typeStack.push("bool");
        codigoCompilado += "    and\n";
    }

    public void action118() { // |
        typeStack.pop();
        typeStack.pop();
        typeStack.push("bool");
        codigoCompilado += "    or\n";
    }

    public void action119() { // true
        typeStack.push("bool");
        codigoCompilado += "    ldc.i4.1\n";
    }

    public void action120() { // false
        typeStack.push("bool");
        codigoCompilado += "    ldc.i4.0\n";
    }

    public void action121() { // !
        typeStack.push("bool");
        codigoCompilado += "    ldc.i4.0\n    ceq\n";
    }

    public void action122() {
        operatorStack.push(currentToken.getLexeme());
    }

    public void action123() {
        String op = operatorStack.pop();
        switch (op) {
            case "==":
                codigoCompilado += "    ceq\n";
                break;
            case "!=":
                codigoCompilado += "    ceq\n    ldc.i4.0\n    ceq\n";
                break;
            case "<":
                codigoCompilado += "    clt\n";
                break;
            case ">":
                codigoCompilado += "    cgt\n";
                break;
        }
        typeStack.pop();
        typeStack.pop();
        typeStack.push("bool");
    }

    public void action124() throws SemanticError { // +
        handleArithmeticOp("add");
    }

    public void action125() throws SemanticError { // -
        handleArithmeticOp("sub");
    }

    public void action126() throws SemanticError { // *
        handleArithmeticOp("mul");
    }

    public void action127() throws SemanticError { // /
        handleArithmeticOp("div");
    }

    private void handleArithmeticOp(String op) throws SemanticError {
        String type2 = typeStack.pop();
        String type1 = typeStack.pop();
        if (type1.equals("float64") || type2.equals("float64")) {
            typeStack.push("float64");
        } else if (type1.equals("int32") && type2.equals("int32")) {
            typeStack.push("int32");
        } else {
            throw new SemanticError("Tipos incompatíveis para operação aritmética.");
        }
        codigoCompilado += String.format("    %s\n", op);
    }

    public void action128() throws SemanticError {
        String id = currentToken.getLexeme();
        if (!symbolTable.containsKey(id)) {
            throw new SemanticError("Identificador '" + id + "' não declarado.");
        }
        String type = symbolTable.get(id);
        typeStack.push(type);
        codigoCompilado += String.format("    ldloc %s\n", id);
    }

    public void action129() {
        typeStack.push("int32");
        codigoCompilado += String.format("    ldc.i4 %s\n", currentToken.getLexeme());
    }

    public void action130() {
        typeStack.push("float64");
        codigoCompilado += String.format("    ldc.r8 %s\n", currentToken.getLexeme());
    }

    public void action131(Token token) {
        if (token.getLexeme().equals("\\n")) {
            typeStack.push("string");
            codigoCompilado += "    ldstr \"\"\n";
        } else {
            typeStack.push("char");
            codigoCompilado += String.format("    ldc.i4.s %d\n", (int) currentToken.getLexeme().charAt(1));
        }
    }

    public void action132() {
        typeStack.push("string");
        codigoCompilado += String.format("    ldstr %s\n", currentToken.getLexeme());
    }

    public void action133() {
        codigoCompilado += "    neg\n";
    }
}