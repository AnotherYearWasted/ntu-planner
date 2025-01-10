package com.example.app;

import com.example.app.api.ScheduleAPIService;
import com.example.app.exceptions.APIException;
import com.example.app.models.Module;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ScheduleAPIServiceTest {

    private final ScheduleAPIService scheduleAPIService = new ScheduleAPIService();


    @Test
    void getSchedule() throws APIException {
        System.out.println("ScheduleAPIServiceTest.getSchedules");

        List<Module> result = scheduleAPIService.getSchedule().block();
    }

    @Test
    void shouldSaveScheduleToFile() throws APIException {

        List<Module> result = scheduleAPIService.getSchedule().block();

        System.out.println("Saving to file...");
        scheduleAPIService.saveToJsonFile(result,"./courses_data.json").block();

        // Check if file exists
        System.out.println("Checking if file exists...");
        System.out.println("File exists?: " + Files.exists(Paths.get("courses_data.json")));
        assert(Files.exists(Paths.get("courses_data.json")));


    }
}
