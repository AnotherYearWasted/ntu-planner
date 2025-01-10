package com.example.app;

import com.example.app.api.ScheduleAPIService;
import com.example.app.exceptions.APIException;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ScheduleAPIServiceTest {

    private final ScheduleAPIService scheduleAPIService = new ScheduleAPIService();


    @Test
    void getSchedule() throws APIException {
        System.out.println("ScheduleAPIServiceTest.getSchedule");

        List<Map<String, Object>> result = scheduleAPIService.getSchedule().block();
    }

    @Test
    void shouldSaveScheduleToFile() throws APIException {

        List<Map<String, Object>> result = scheduleAPIService.getSchedule().block();

        System.out.println("Saving to file...");
        scheduleAPIService.saveToJsonFile(result).block();

        // Check if file exists
        System.out.println("Checking if file exists...");
        System.out.println("File exists?: " + Files.exists(Paths.get("courses_data.json")));
        assert(Files.exists(Paths.get("courses_data.json")));


    }
}
