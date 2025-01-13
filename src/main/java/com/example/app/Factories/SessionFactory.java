package com.example.app.Factories;

import com.example.app.exceptions.BuilderException;
import com.example.app.exceptions.FactoryException;
import com.example.app.models.Index;
import com.example.app.models.Session;

public class SessionFactory {

    public static Session createSession(Index index) throws FactoryException {
        try {
            Session session = new Session();
            session.setIndexId(index.getId());
            return session;
        } catch (Exception e) {
            throw new FactoryException(e);
        }
    }

}
