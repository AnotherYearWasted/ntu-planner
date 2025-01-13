package com.example.app.api;

import com.example.app.Factories.IndexFactory;
import com.example.app.Factories.ModuleFactory;
import com.example.app.Factories.SessionFactory;
import com.example.app.exceptions.APIException;
import com.example.app.models.Index;
import com.example.app.models.Module;
import com.example.app.models.Session;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleAPIService extends APIService {

    private static final String url = "https://wish.wis.ntu.edu.sg/webexe/owa/";

    public ScheduleAPIService() {
        super(url);
    }

    /**
     * Retrieves the schedule data from the API
     *
     * @return A mono that emits a list of modules
     *
     * @throws APIException
     */
    public Mono<List<Module>> getSchedule() throws APIException {
        String formData = "acadsem=2024;2&boption=CLoad&staff_access=false&r_search_type=F&acadsem=2023;2&r_course_yr=CSC;;1;F";
        System.out.println("Getting schedule data...");
        return this.getWebClient().post().uri("AUS_SCHEDULE.main_display1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(BodyInserters.fromValue(formData)).retrieve()
                .bodyToMono(String.class).flatMap(html -> {
                    try {
                        return parseSchedules(html);
                    } catch (Exception e) {
                        return Mono.error(new APIException("Error parsing HTML: " + e.getMessage(), e));
                    }
                }).onErrorMap(error -> new APIException("Error getting schedule data: " + error.getMessage(), error));
    }

    private int parseWeeks(String weeksStr) throws APIException {
        if (weeksStr.isEmpty()) {
            return (1 << 15) - 2; // Default: all weeks (bits 1-14 set to 1)
        }

        if (weeksStr.equals("Not conducted during Teaching Weeks")) {
            return 0; // No weeks
        }

        int bitmask = 0;
        try {
            String[] ranges = weeksStr.substring(11).split(","); // Remove "Teaching " prefix
            for (String range : ranges) {
                String[] bounds = range.split("-");
                if (bounds.length == 1) {
                    bitmask |= 1 << Integer.parseInt(bounds[0]);
                } else {
                    int start = Integer.parseInt(bounds[0]);
                    int end = Integer.parseInt(bounds[1]);
                    for (int i = start; i <= end; i++) {
                        bitmask |= 1 << i;
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid weeks format: " + weeksStr);
        }
        return bitmask;
    }

    /**
     * Parses the HTML response from the schedule API The HTML response is parsed using Jsoup
     *
     * @param html
     *            The HTML response from the schedule API
     *
     * @return A mono that emits a list of modules
     *
     * @see Mono
     * @see Jsoup
     */
    private Mono<List<Module>> parseSchedules(String html) throws APIException {
        return Mono.fromSupplier(() -> {
            List<Module> courses = new ArrayList<>();
            System.out.print("Parsing HTML...");
            try {

                // Parse HTML with Jsoup
                Document doc = Jsoup.parse(html);

                // Select course sections
                Elements courseTables = doc.select("hr + table");
                for (Element courseTable : courseTables) {
                    // Extract course info
                    Module module = ModuleFactory.createModule();
                    module.setModuleCode(courseTable.select("td b font[color=\"#0000FF\"]").eq(0).text().trim())
                            .setName(courseTable.select("td b font[color=\"#0000FF\"]").eq(1).text().trim())
                            .setCredits(Float.parseFloat(courseTable.select("td b font[color=\"#0000FF\"]").eq(2).text()
                                    .trim().replace(" AU", "")));

                    // Extract prerequisites
                    Elements prerequisiteElements = courseTable.select("td[colspan=\"2\"] b font[color=\"#FF00FF\"]");
                    for (Element element : prerequisiteElements) {
                        String text = element.text().trim();
                        if (!text.isEmpty() && !text.startsWith("&nbsp;")) {
                            module.addPrerequisite(text.replace("&nbsp;", "").trim());
                        }
                    }

                    // Extract schedule
                    Elements scheduleRows = courseTable.nextElementSibling().select("table[border] tr");

                    Index dummy = IndexFactory.createIndex(module, null);
                    Session lastSession = SessionFactory.createSession(dummy);

                    for (int i = 1; i < scheduleRows.size(); i++) { // Skip header row
                        Elements columns = scheduleRows.get(i).select("td b");
                        if (columns.size() == 7) {

                            Map<String, String> scheduleEntry = new HashMap<>();

                            Long indexId;
                            // Set the index of the last entry
                            if (columns.get(0).text().trim().isEmpty() && lastSession.getIndexId() != null) {
                                indexId = lastSession.getIndexId();
                            } else {
                                indexId = Long.parseLong(columns.get(0).text().trim());
                            }

                            if (module.getIndexes().get(indexId) == null) {
                                Index index = IndexFactory.createIndex(module, indexId);
                                module.addIndex(index);
                            }

                            Index index = module.getIndexes().get(indexId);

                            Session session = SessionFactory.createSession(index);

                            switch (columns.get(1).text().trim()) {
                            case "LEC/00":
                                session.setSessionType(Session.SessionType.LECTURE);
                                break;
                            case "TUT":
                                session.setSessionType(Session.SessionType.TUTORIAL);
                                break;
                            case "LAB":
                                session.setSessionType(Session.SessionType.LAB);
                                break;
                            case "SEM":
                                session.setSessionType(Session.SessionType.SEMINAR);
                                break;
                            default:
                                session.setSessionType(Session.SessionType.UNKNOWN);
                                break;
                            }

                            session.setGroup(columns.get(2).text().trim());

                            switch (columns.get(3).text().trim()) {
                            case "MON":
                                session.setDay(DayOfWeek.MONDAY);
                                break;
                            case "TUE":
                                session.setDay(DayOfWeek.TUESDAY);
                                break;
                            case "WED":
                                session.setDay(DayOfWeek.WEDNESDAY);
                                break;
                            case "THU":
                                session.setDay(DayOfWeek.THURSDAY);
                                break;
                            case "FRI":
                                session.setDay(DayOfWeek.FRIDAY);
                                break;
                            case "SAT":
                                session.setDay(DayOfWeek.SATURDAY);
                                break;
                            default:
                                session.setDay(DayOfWeek.SUNDAY);
                                break;
                            }

                            String time = columns.get(4).text().trim();

                            String[] timeSplit = time.split("-");
                            String startHour = timeSplit[0].substring(0, 2);
                            String startMinute = timeSplit[0].substring(2);
                            String endHour = timeSplit[1].substring(0, 2);
                            String endMinute = timeSplit[1].substring(2);

                            session.setStartHour(Long.parseLong(startHour));
                            session.setStartMinute(Long.parseLong(startMinute));
                            session.setEndHour(Long.parseLong(endHour));
                            session.setEndMinute(Long.parseLong(endMinute));

                            session.setVenue(columns.get(5).text().trim());

                            String weeksStr = columns.get(6).text().trim();

                            session.setWeeks(parseWeeks(weeksStr));

                            session.setRemark(columns.get(6).text().trim());

                            lastSession = session;
                            index.addSession(session);

                            module.addIndex(index);

                        } else {
                            System.out.println("Skipping row with " + columns.size() + " columns");
                        }
                    }

                    courses.add(module);
                }

            } catch (Exception e) {
                Mono.error(new APIException("Error parsing HTML: " + e.getMessage(), e));
            }
            return courses;
        });
    }

    /**
     * @param courses
     *            The list of courses to save to a JSON file
     * @param path
     *            The string path to save the JSON file to
     *
     * @return A Mono that completes when the data is saved to the file
     *
     * @see Mono
     */
    public Mono<Void> saveToJsonFile(List<Module> courses, String path) {
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper

        return Mono.fromRunnable(() -> {
            try {
                // Serialize the list of courses into JSON and write to a file
                objectMapper.writeValue(new File(path), courses);
                System.out.println("Data saved to file: " + path);
            } catch (IOException e) {
                throw new RuntimeException("Error saving data to file: " + e.getMessage(), e);
            }
        });
    }

    public Mono<Module> getVacancies(String moduleCode) throws APIException {
        String formData = String.format("subj=%s", moduleCode);
        System.out.println("Getting vacancies for " + moduleCode + "...");

        return this.getWebClient().post().uri("aus_vacancy.check_vacancy2")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(BodyInserters.fromValue(formData)).retrieve()
                .onStatus(status -> status.value() == 400, clientResponse -> {
                    System.err.println("Client error occurred: " + clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorMessage -> Mono.error(new APIException(errorMessage)));
                }).bodyToMono(String.class).flatMap(html -> {
                    try {
                        return parseVacancies(html);
                    } catch (Exception e) {
                        return Mono.error(new APIException("Error parsing vacancies: " + e.getMessage(), e));
                    }
                }).onErrorMap(error -> {
                    System.err.println("Error getting vacancies: " + error.getMessage());
                    return new APIException("Error getting vacancies: " + error.getMessage(), error);
                });
    }

    /**
     * Parses the HTML response from the vacancies API
     *
     * @param html
     *            The HTML response from the vacancies API
     *
     * @return A mono that emits a module and its index vacancies
     */
    private Mono<Module> parseVacancies(String html) {
        return Mono.fromSupplier(() -> {
            try {
                // Create a new module instance
                Module module = ModuleFactory.createModule();

                // Parse the HTML document using Jsoup
                Document doc = Jsoup.parse(html);

                // Locate the table rows
                Elements rows = doc.select("table[border] tr");

                // Loop through each row (skipping the header row)
                for (int i = 1; i < rows.size(); i++) { // Start from index 1 to skip the header
                    Element row = rows.get(i);
                    Elements columns = row.select("td");

                    // Check if the row contains sufficient columns
                    if (columns.size() >= 8) {
                        // Parse the columns into meaningful data
                        String indexStr = columns.get(0).text().trim();
                        if (indexStr.isEmpty()) {
                            // Skip rows without index numbers
                            continue;
                        }

                        Long index = Long.parseLong(indexStr);
                        Long vacancies = columns.get(1).text().trim().isEmpty() ? 0
                                : Long.parseLong(columns.get(1).text().trim());
                        Long waitlist = columns.get(2).text().trim().isEmpty() ? 0
                                : Long.parseLong(columns.get(2).text().trim());
                        String classType = columns.get(3).text().trim();
                        String group = columns.get(4).text().trim();
                        String day = columns.get(5).text().trim();
                        String time = columns.get(6).text().trim();
                        String venue = columns.get(7).text().trim();

                        // Create an Index object and populate its fields
                        Index indexObj = IndexFactory.createIndex(module, index).setVacant(vacancies)
                                .setWaitlist(waitlist);

                        // Add the Index object to the Module
                        module.addIndex(indexObj);
                    }
                }

                return module; // Return the populated module
            } catch (Exception e) {
                throw new RuntimeException("Error parsing vacancies: " + e.getMessage(), e);
            }
        });
    }

}
