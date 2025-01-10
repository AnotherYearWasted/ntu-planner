package com.example.app.api;

import com.example.app.exceptions.APIException;
import com.example.app.models.ClassModule;
import com.example.app.models.Module;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScheduleAPIService extends APIService {

    private static final String url = "https://wish.wis.ntu.edu.sg/webexe/owa/AUS_SCHEDULE.main_display1";

    public ScheduleAPIService() {
        super(url);
    }

    public Mono<List<Map<String, Object>>> getSchedule() throws APIException {
        String formData = "acadsem=2024;2&boption=CLoad&staff_access=false&r_search_type=F&acadsem=2023;2&r_course_yr=CSC;;1;F";
        System.out.println("Getting schedule data...");
        return this.getWebClient().post().uri(url).contentType(MediaType.APPLICATION_FORM_URLENCODED).body(BodyInserters.fromValue(formData)).retrieve().bodyToMono(String.class).flatMap(html -> {
            try {
                return parseHtml(html);
            } catch (Exception e) {
                return Mono.error(new APIException("Error parsing HTML: " + e.getMessage(), e));
            }
        }).onErrorMap(error -> new APIException("Error getting schedule data: " + error.getMessage(), error));
    }

    private Mono<List<Map<String, Object>>> parseHtml(String html) throws APIException {
        return Mono.fromSupplier(() -> {
            // Parse HTML with Jsoup
            Document doc = Jsoup.parse(html);
            List<Map<String, Object>> courses = new ArrayList<>();

            // Select course sections
            Elements courseTables = doc.select("hr + table");
            for (Element courseTable : courseTables) {
                // Extract course info
                Module module = new ClassModule();
                module.setModuleCode(courseTable.select("td b font[color=\"#0000FF\"]").eq(0).text().trim()).setName(courseTable.select("td b font[color=\"#0000FF\"]").eq(1).text().trim()).setCredits(Float.parseFloat(courseTable.select("td b font[color=\"#0000FF\"]").eq(2).text().trim().replace(" AU", "")));

                System.out.println(module.getModuleCode());
                System.out.println(module.getName());
                System.out.println(module.getCredits());

                // Extract prerequisites
                List<String> prerequisites = new ArrayList<>();
                Elements prerequisiteElements = courseTable.select("td[colspan=\"2\"] b font[color=\"#FF00FF\"]");
                for (Element element : prerequisiteElements) {
                    String text = element.text().trim();
                    if (!text.isEmpty() && !text.startsWith("&nbsp;")) {
                        prerequisites.add(text.replace("&nbsp;", "").trim());
                    }
                }

                // Extract schedule
                List<Map<String, String>> schedule = new ArrayList<>();
                Elements scheduleRows = courseTable.nextElementSibling().select("table[border] tr");
                for (int i = 1; i < scheduleRows.size(); i++) { // Skip header row
                    Elements columns = scheduleRows.get(i).select("td b");
                    if (columns.size() > 0) {
                        try {
                            Map<String, String> scheduleEntry = new HashMap<>();

                            ClassModule classModule = (ClassModule) module;

                            scheduleEntry.put("index", columns.get(0).text().trim());
                            scheduleEntry.put("type", columns.get(1).text().trim());
                            scheduleEntry.put("group", columns.get(2).text().trim());
                            scheduleEntry.put("day", columns.get(3).text().trim());
                            scheduleEntry.put("time", columns.get(4).text().trim());
                            scheduleEntry.put("venue", columns.get(5).text().trim());
                            scheduleEntry.put("remark", columns.get(6).text().trim());
                            schedule.add(scheduleEntry);

                        } catch (Exception e) {
                            System.out.println("Error parsing schedule entry: " + e.getMessage());
                            System.out.println("Columns: " + columns);
                        }
                    }
                }

                // Combine extracted data
                Map<String, Object> courseData = new HashMap<>();
                courseData.put("courseInfo", courseInfo);
                courseData.put("prerequisites", prerequisites);
                courseData.put("schedule", schedule);

                courses.add(courseData);
            }

            return courses;
        });
    }

    public Mono<Void> saveToJsonFile(List<Map<String, Object>> courses) {
        return Mono.fromRunnable(() -> {
            try (FileWriter file = new FileWriter("courses_data.json")) {
                file.write(courses.toString()); // Use ObjectMapper for prettier output if needed
                System.out.println("Data saved to courses_data.json");
            } catch (IOException e) {
                throw new RuntimeException("Error saving data to file: " + e.getMessage(), e);
            }
        });
    }


}
