package com.example.app.Factories;

import com.example.app.exceptions.BuilderException;
import com.example.app.exceptions.FactoryException;
import com.example.app.models.Module;

public class ModuleFactory {

    public static com.example.app.models.Module createModule() throws FactoryException {
        try {
            return new Module.ModuleBuilder().build();
        }
        catch (BuilderException e) {
            throw new FactoryException(e);
        }
    }

}
