package uvg.edu.gt.datos;
import java.io.PrintStream;
public class LispText extends Atom {
    private String valor;

    private LispText(String valor) {
        this.valor = valor;
    }

    public static LispText texto(String valor) {
        return new LispText(valor);
    }

    @Override
    public boolean esTexto() {
        return true;
    }

    public void imprimir(PrintStream salida) {
        salida.print("\"" + valor + "\"");
    }

    public String valor() {
        return this.valor;
    }
}