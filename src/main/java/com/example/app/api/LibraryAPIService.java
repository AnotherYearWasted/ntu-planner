package com.example.app.api;

import com.example.app.exceptions.APIException;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import reactor.core.publisher.Mono;

public class LibraryAPIService extends APIService {

    private static final String url = "https://libcalendar.ntu.edu.sg/";

    public LibraryAPIService() {
        super(url);
    }

    public Mono<String> checkIn(String code) throws APIException {
        return sendCode(code);
    }

    private Mono<String> sendCode(String code) throws APIException {
        // Log the code being sent
        System.out.println("Sending code: " + code);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("code", code); // Add the "code" form-data field
        // Use WebClient to make the request
        return this.getWebClient().post().uri("/r/checkin")
                .header("Referer", "https://libcalendar.ntu.edu.sg/r/checkin")
                .contentType(MediaType.MULTIPART_FORM_DATA) // Set content type to multipart/form-data
                .bodyValue(builder.build()).retrieve().onStatus(status -> status.value() == 400, // Handle 400 Bad
                                                                                                 // Request
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorMessage -> Mono.error(new APIException(errorMessage))))
                .bodyToMono(String.class) // Parse the response body as a string
                .doOnNext(response -> {
                    System.out.println("Response: " + response); // Log the response
                }).doOnError(error -> System.err.println("Error occurred: " + error.getMessage()))
                .switchIfEmpty(Mono.error(new APIException("Response is null")));
    }

    public Mono<Void> checkOut(String code) {
        return null;
    }
}
