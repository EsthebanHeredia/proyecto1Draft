package uvg.edu.gt.datos;

import uvg.edu.gt.exceptions.ErrorInvalidArguments;
import java.io.PrintStream;

public class Parentesis extends Expresion {
    private Expresion primero;
    private Expresion segundo;

    public Parentesis(Expresion primero, Expresion segundo) {
        this.primero = primero;
        this.segundo = segundo;
    }

    @Override
    public boolean esAtomo() {
        return false;
    }

    @Override
    public Expresion primero() {
        return primero;
    }

    @Override
    public Expresion segundo() {
        return segundo;
    }

    @Override
    public void imprimir(PrintStream salida) {
        salida.print("(");
        primero.imprimir(salida);
        salida.print(" . ");
        segundo.imprimir(salida);
        salida.print(")");
    }
}