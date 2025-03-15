package uvg.edu.gt.datos;

import java.io.PrintStream;

public class Number extends Atom {
    public static final Number CERO = new Number(0);
    public static final Number UNO = new Number(1);

    private final long valor;

    private Number(long valor) {
        this.valor = valor;
    }

    public static Number numero(long valor) {
        return new Number(valor);
    }

    public static Number numero(String texto) {
        return new Number(Long.parseLong(texto));
    }

    public long valor() {
        return valor;
    }

    @Override
    public void imprimir(PrintStream salida) {
        salida.print(valor);
    }

    @Override
    public boolean esNumero() {
        return true;
    }
}