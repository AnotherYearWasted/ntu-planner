package com.example.app;

import com.example.app.api.LibraryAPIService;
import com.example.app.exceptions.APIException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class LibraryAPIServiceTest {

    private final LibraryAPIService libraryAPIService = new LibraryAPIService();

    @Test
    public void shouldSendWrongCode() throws APIException {
        String code = "dj9q";

        StepVerifier.create(libraryAPIService.checkIn(code))
                .expectErrorMatches(throwable -> throwable instanceof APIException
                        && throwable.getMessage().equals("Unable to find booking matching code"))
                .verify();
    }
}