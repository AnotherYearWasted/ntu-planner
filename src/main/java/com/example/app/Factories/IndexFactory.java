package com.example.app.Factories;

import com.example.app.exceptions.FactoryException;
import com.example.app.models.Index;
import com.example.app.models.Module;

public class IndexFactory {

    public static Index createIndex(Module module, Long index) throws FactoryException {

        return new Index(module.getId(), index);

    }
}
