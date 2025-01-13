package com.example.app;

import com.example.app.api.ScheduleAPIService;
import com.example.app.exceptions.APIException;
import com.example.app.models.Module;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        scheduleAPIService.saveToJsonFile(result, "./courses_data.json").block();

        // Check if file exists
        System.out.println("Checking if file exists...");
        System.out.println("File exists?: " + Files.exists(Paths.get("courses_data.json")));
        assert (Files.exists(Paths.get("courses_data.json")));

    }

    @Test
    void getVacancies() throws APIException {
        System.out.println("ScheduleAPIServiceTest.getVacancies");

        Module result = scheduleAPIService.getVacancies("SC2002").block();

        assert result != null;

    }

    @Test
    void shouldSaveVacancyToFile() throws APIException {
        // Fetch the vacancy data
        Module module = scheduleAPIService.getVacancies("SC2002").block();

        // Convert the single module to a list
        List<Module> moduleList = Collections.singletonList(module);

        // Save the result to a file
        System.out.println("Saving to file...");
        scheduleAPIService.saveToJsonFile(moduleList, "./vacancy_data.json").block();

        // Check if the file exists
        System.out.println("Checking if file exists...");
        assertTrue(Files.exists(Paths.get("./vacancy_data.json")));
    }
}
