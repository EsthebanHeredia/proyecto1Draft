package uvg.edu.gt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import uvg.edu.gt.datos.*;
import uvg.edu.gt.exceptions.*;
import uvg.edu.gt.parser.Parser;

public class Interpreter {
    private boolean modoDebug = false;
    private Expresion entornoGlobal = Symbol.NADA;
    private InputStream entrada = System.in;
    private PrintStream salida = System.out;
    private int nivelDebug = 0;

    public Interpreter() {
        this(new String[0]);
    }

    public Interpreter(String[] parametros) {
        Expresion listaParametros = Symbol.NADA;
        for (int i = parametros.length - 1; i >= 0; i--) {
            listaParametros = crearPar(LispText.texto(parametros[i]), listaParametros);
        }
        try {
            entornoGlobal = crearPar(crearPar(Symbol.PARAMS, listaParametros), entornoGlobal);
        } catch (Exception e) {
            salida.println("Error al inicializar parámetros: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws ErrorLisp {
        Interpreter interpreter = new Interpreter(args);
        interpreter.iniciarBucle();
    }

    public void iniciarBucle() {
        iniciarBucle(entrada, salida);
    }

    public void iniciarBucle(InputStream entrada, PrintStream salida) {
        BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
        Parser parser = new Parser(lector);
        Expresion expresion;
        Expresion valorResultado;

        while (true) {
            try {
                if (entrada == System.in) {
                    salida.print(">> ");
                }

                expresion = parser.leer();

                if (expresion == null) {
                    salida.println("Error de sintaxis");
                } else if (expresion == Symbol.NADA) {
                    return;
                } else {
                    if (modoDebug) {
                        salida.print("Expresión: ");
                        expresion.imprimir(salida);
                        salida.println();
                    }

                    try {
                        valorResultado = evaluar(expresion, entornoGlobal);
                        if (!expresion.esAtomo()) {
                            salida.print("Resultado: ");
                            valorResultado.imprimir(salida);
                            salida.println();
                        }
                    } catch (ErrorLisp e) {
                        salida.println("Error: " + e.getMessage());
                    }
                }
            } catch (ErrorSintaxis e) {
                salida.println("Error de sintaxis: " + e.getMessage());
            } catch (ErrorLisp e) {
                salida.println("Error: " + e.getMessage());
            }
        }
    }

    public static Expresion crearPar(Expresion primero, Expresion segundo) {
        return new Parentesis(primero, segundo);
    }

    private static Expresion primero(Expresion expr) throws ErrorInvalidArguments {
        return expr.primero();
    }

    private static Expresion segundo(Expresion expr) throws ErrorInvalidArguments {
        return expr.segundo();
    }

    public Expresion evaluar(Expresion expr, Expresion entorno) throws ErrorLisp {
        Expresion valor;

        if (modoDebug) imprimirEntradaDebug(expr, entorno);

        if (expr.esSimbolo()) {
            if (Symbol.VERDAD == expr || Symbol.NADA == expr) {
                valor = expr;
            } else {
                valor = buscarVariable((Symbol) expr, entorno);
            }
        } else if (expr.esNumero()) {
            valor = expr;
        } else if (expr.esTexto()) {
            valor = expr;
        } else {
            Expresion funcion = primero(expr); // No evaluamos el operador
            Expresion args = expr.segundo();
            Expresion argsEvaluados = evaluarArgumentos(args, entorno);

            if (funcion instanceof Symbol) {
                Symbol op = (Symbol) funcion;
                if (op == Symbol.SUMA) {
                    valor = reducir(argsEvaluados, this::sumar, uvg.edu.gt.datos.Number.CERO);
                } else if (op == Symbol.RESTA) {
                    valor = restar(argsEvaluados);
                } else if (op == Symbol.MULTIPLICACION) {
                    valor = reducir(argsEvaluados, this::multiplicar, uvg.edu.gt.datos.Number.UNO);
                } else if (op == Symbol.DIVISION) {
                    valor = dividir(argsEvaluados);
                } else {
                    valor = aplicar(funcion, argsEvaluados, entorno);
                }
            } else {
                throw new ErrorInvalidArguments("No es un operador válido: " + funcion);
            }
        }

        if (modoDebug) imprimirSalidaDebug(valor);
        return valor;
    }











    public Expresion aplicar(Expresion funcion, Expresion args, Expresion entorno) throws ErrorLisp {
        if (funcion == Symbol.LISTP) return esLista(args) ? Symbol.VERDAD : Symbol.NADA;
        if (funcion == Symbol.EQUAL) return esIgual(args) ? Symbol.VERDAD : Symbol.NADA;
        if (funcion == Symbol.LT) return esMenor(args) ? Symbol.VERDAD : Symbol.NADA;
        if (funcion == Symbol.GT) return esMayor(args) ? Symbol.VERDAD : Symbol.NADA;
        if (funcion == Symbol.EQUALS_NUM) return esIgualNumerico(args) ? Symbol.VERDAD : Symbol.NADA;
        throw new ErrorInvalidArguments("Función no reconocida: " + funcion);
    }

    // Métodos de predicados
    private boolean esLista(Expresion expr) {
        if (expr == Symbol.NADA) return true;
        if (expr.esAtomo()) return false;
        try {
            return esLista(segundo(expr));
        } catch (ErrorInvalidArguments e) {
            return false;
        }
    }

    private boolean esIgual(Expresion exprs) throws ErrorInvalidArguments {
        Expresion primero = primero(exprs);
        Expresion segundo = segundo(exprs);
        return primero.equals(segundo);
    }

    private boolean esMenor(Expresion exprs) throws ErrorInvalidArguments {
        return valorNumerico(primero(exprs)) < valorNumerico(segundo(exprs));
    }

    private boolean esMayor(Expresion exprs) throws ErrorInvalidArguments {
        return valorNumerico(primero(exprs)) > valorNumerico(segundo(exprs));
    }

    private boolean esIgualNumerico(Expresion exprs) throws ErrorInvalidArguments {
        return valorNumerico(primero(exprs)) == valorNumerico(segundo(exprs));
    }



















    private Expresion evaluarArgumentos(Expresion args, Expresion entorno) throws ErrorLisp {
        if (args == Symbol.NADA) {
            return Symbol.NADA;
        }
        return crearPar(
                evaluar(primero(args), entorno),
                evaluarArgumentos(segundo(args), entorno)
        );
    }

    private Expresion sumar(Expresion a, Expresion b) throws ErrorLisp {
        return uvg.edu.gt.datos.Number.numero(valorNumerico(a) + valorNumerico(b));
    }

    private Expresion multiplicar(Expresion a, Expresion b) throws ErrorLisp {
        return uvg.edu.gt.datos.Number.numero(valorNumerico(a) * valorNumerico(b));
    }

    private Expresion restar(Expresion args) throws ErrorLisp {
        long resultado = valorNumerico(primero(args));
        args = segundo(args);

        while (args != Symbol.NADA) {
            resultado -= valorNumerico(primero(args));
            args = segundo(args);
        }

        return uvg.edu.gt.datos.Number.numero(resultado);
    }

    private Expresion dividir(Expresion args) throws ErrorLisp {
        long resultado = valorNumerico(primero(args));
        args = segundo(args);

        while (args != Symbol.NADA) {
            long divisor = valorNumerico(primero(args));
            if (divisor == 0) {
                throw new ErrorInvalidArguments("división por cero");
            }
            resultado /= divisor;
            args = segundo(args);
        }

        return uvg.edu.gt.datos.Number.numero(resultado);
    }

    private long valorNumerico(Expresion expr) throws ErrorInvalidArguments {
        if (!expr.esNumero()) {
            throw new ErrorInvalidArguments("no es un número: " + expr);
        }
        return ((uvg.edu.gt.datos.Number) expr).valor();
    }

    private Expresion buscarVariable(Symbol s, Expresion lista) throws ErrorInvalidArguments {
        if (lista == Symbol.NADA) {
            return Symbol.NADA;
        } else if (primero(primero(lista)) == s) {
            return segundo(primero(lista));
        } else {
            return buscarVariable(s, segundo(lista));
        }
    }

    private Expresion reducir(Expresion lista, OperadorBinario f, Expresion identidad) throws ErrorLisp {
        if (lista == Symbol.NADA) {
            return identidad;
        } else {
            return f.aplicar(primero(lista), reducir(segundo(lista), f, identidad));
        }
    }

    interface OperadorBinario {
        Expresion aplicar(Expresion a, Expresion b) throws ErrorLisp;
    }

    private void imprimirEntradaDebug(Expresion expr, Expresion env) {
        nivelDebug++;
        salida.print(indentacion() + ">EVAL: ");
        expr.imprimir(salida);
        salida.println();
    }

    private void imprimirSalidaDebug(Expresion valor) {
        salida.print(indentacion() + "<EVAL: ");
        valor.imprimir(salida);
        salida.println();
        nivelDebug--;
    }

    private String indentacion() {
        StringBuilder espacios = new StringBuilder();
        for (int i = 0; i < nivelDebug; i++) {
            espacios.append(" ");
        }
        return espacios.toString();
    }
}