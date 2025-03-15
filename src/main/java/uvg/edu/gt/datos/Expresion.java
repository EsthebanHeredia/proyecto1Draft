package uvg.edu.gt.datos;
import uvg.edu.gt.exceptions.ErrorInvalidArguments;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
public abstract class Expresion implements Iterable<Expresion> {
    public abstract Expresion primero() throws ErrorInvalidArguments;
    public abstract Expresion segundo() throws ErrorInvalidArguments;

    public boolean esAtomo() {
        return false;
    }

    public boolean esSimbolo() {
        return false;
    }

    public boolean esNumero() {
        return false;
    }

    public boolean esTexto() {
        return false;
    }

    public abstract void imprimir(PrintStream salida);

    public String toString() {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        PrintStream salida = new PrintStream(byteArray);
        this.imprimir(salida);
        salida.close();
        return new String(byteArray.toByteArray(), StandardCharsets.UTF_8);
    }

    public Iterator<Expresion> iterator() {
        return new IteradorExpresion(this);
    }

    private static class IteradorExpresion implements Iterator<Expresion> {
        private Expresion actual;

        IteradorExpresion(Expresion expr) {
            this.actual = expr;
        }

        public boolean hasNext() {
            return actual != uvg.edu.gt.datos.Symbol.NADA;
        }

        public Expresion next() {
            try {
                Expresion resultado = actual.primero();
                actual = actual.segundo();
                return resultado;
            } catch (ErrorInvalidArguments e) {
                throw new RuntimeException("Error en iterador: " + e.getMessage());
            }
        }
    }
}

