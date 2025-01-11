package com.example.app.Factories;

import com.example.app.exceptions.BuilderException;
import com.example.app.exceptions.FactoryException;
import com.example.app.models.Index;
import com.example.app.models.Session;

public class SessionFactory {

    public static Session createSession(Index index) throws FactoryException {
        try {
            return new Session.SessionBuilder().setIndexId(index.getIndex()).build();
        } catch (Exception e) {
            throw new FactoryException(e);
        }
    }

}
