package uvg.edu.gt.datos;

import uvg.edu.gt.exceptions.ErrorInvalidArguments;

public abstract class Atom extends Expresion {
    @Override
    public boolean esAtomo() {
        return true;
    }

    @Override
    public Expresion primero() throws ErrorInvalidArguments {
        throw new ErrorInvalidArguments("Atom no tiene primero");
    }

    @Override
    public Expresion segundo() throws ErrorInvalidArguments {
        throw new ErrorInvalidArguments("Atom no tiene segundo");
    }
}