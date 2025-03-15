package uvg.edu.gt.parser;
import uvg.edu.gt.exceptions.ErrorEntradaSalida;
import java.io.BufferedReader;
import java.io.IOException;
public class TokenAnalizer {
    private BufferedReader lector;
    private String buffer = null;

    public TokenAnalizer(BufferedReader lector) {
        this.lector = lector;
    }

    public String siguiente() throws ErrorEntradaSalida {
        try {
            if (buffer != null) {
                String token = buffer;
                buffer = null;
                return token;
            }

            int caracter;
            do {
                caracter = lector.read();
                if (caracter == -1) return null;
            } while (caracter == ' ' || caracter == '\n' || caracter == '\t' || caracter == '\r');

            if (caracter == '(' || caracter == ')' || caracter == '\'' || caracter == '`' ||
                    caracter == ',' || caracter == '.') {
                return Character.toString((char) caracter);
            }

            StringBuilder texto = new StringBuilder();
            if (caracter == '\"') {
                texto.append((char) caracter);
                while ((caracter = lector.read()) != '\"') {
                    if (caracter == -1 || caracter == '\n') {
                        throw new ErrorEntradaSalida("fin de texto inesperado");
                    }
                    texto.append((char) caracter);
                }
                texto.append((char) caracter);
                return texto.toString();
            }

            do {
                texto.append((char) caracter);
                caracter = lector.read();
            } while (caracter != -1 && caracter != ' ' && caracter != '\n' && caracter != '\t' &&
                    caracter != '\r' && caracter != '(' && caracter != ')' &&
                    caracter != '\'' && caracter != '`' && caracter != ',' && caracter != '.');

            if (caracter != -1) {
                buffer = Character.toString((char) caracter);
            }

            return texto.toString();
        } catch (IOException e) {
            throw new ErrorEntradaSalida("Error de E/S: " + e.getMessage());
        }
    }
}