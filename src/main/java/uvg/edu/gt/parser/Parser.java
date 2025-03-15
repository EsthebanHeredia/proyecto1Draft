package uvg.edu.gt.parser;
import uvg.edu.gt.datos.*;
import uvg.edu.gt.exceptions.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final BufferedReader lector;

    public Parser(BufferedReader lector) {
        this.lector = lector;
    }

    public Expresion leer() throws ErrorLisp {
        try {
            String linea = lector.readLine();
            if (linea == null || linea.trim().isEmpty()) {
                return Symbol.NADA;
            }
            return parsearExpresion(tokenizar(linea));
        } catch (IOException e) {
            throw new ErrorLisp("Error de lectura: " + e.getMessage());
        }
    }

    private List<String> tokenizar(String linea) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();

        for (char c : linea.toCharArray()) {
            if (c == '(' || c == ')' || c == ' ') {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token = new StringBuilder();
                }
                if (c != ' ') {
                    tokens.add(String.valueOf(c));
                }
            } else {
                token.append(c);
            }
        }

        if (token.length() > 0) {
            tokens.add(token.toString());
        }

        return tokens;
    }

    private Expresion parsearExpresion(List<String> tokens) throws ErrorLisp {
        if (tokens.isEmpty()) {
            return Symbol.NADA;
        }

        String token = tokens.remove(0);

        if (token.equals("(")) {
            return parsearLista(tokens);
        } else if (token.equals(")")) {
            throw new ErrorSintaxis("Paréntesis de cierre inesperado");
        } else {
            return parsearAtomo(token);
        }
    }

    private Expresion parsearLista(List<String> tokens) throws ErrorLisp {
        if (tokens.isEmpty()) {
            throw new ErrorSintaxis("Lista sin cerrar");
        }

        if (tokens.get(0).equals(")")) {
            tokens.remove(0);
            return Symbol.NADA;
        }

        Expresion primero = parsearExpresion(tokens);
        Expresion resto = parsearLista(tokens);

        return new Parentesis(primero, resto);
    }

    private Expresion parsearAtomo(String token) {
        try {
            return uvg.edu.gt.datos.Number.numero(Long.parseLong(token));
        } catch (NumberFormatException e) {
            return Symbol.simbolo(token);
        }
    }
}