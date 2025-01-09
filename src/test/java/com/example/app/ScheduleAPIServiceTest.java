package com.example.app;

import com.example.app.api.ScheduleAPIService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class ScheduleAPIServiceTest {

    private final ScheduleAPIService scheduleAPIService = new ScheduleAPIService();


    @Test
    void getSchedule() {
        System.out.println("ScheduleAPIServiceTest.getSchedule");

        List<Map<String, Object>> result = scheduleAPIService.getSchedule().block();

    }
}
