package uvg.edu.gt.datos;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class Symbol extends Atom {
    private static final Map<String, Symbol> simbolos = new HashMap<>();

    public static final Symbol NADA = simbolo("nil");
    public static final Symbol VERDAD = simbolo("t");
    public static final Symbol PARAMS = simbolo("*command-line-args*");
    public static final Symbol SUMA = simbolo("+");
    public static final Symbol RESTA = simbolo("-");
    public static final Symbol MULTIPLICACION = simbolo("*");
    public static final Symbol DIVISION = simbolo("/");

    private final String nombre;

    private Symbol(String nombre) {
        this.nombre = nombre;
    }

    public static Symbol simbolo(String nombre) {
        return simbolos.computeIfAbsent(nombre, Symbol::new);
    }

    @Override
    public void imprimir(PrintStream salida) {
        salida.print(nombre);
    }

    @Override
    public boolean esSimbolo() {
        return true;
    }
}