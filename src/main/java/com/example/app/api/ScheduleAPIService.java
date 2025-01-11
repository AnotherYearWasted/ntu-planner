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

    private static final String url = "https://wish.wis.ntu.edu.sg/webexe/owa/AUS_SCHEDULE.main_display1";

    public ScheduleAPIService() {
        super(url);
    }

    public Mono<List<Module>> getSchedule() throws APIException {
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

    private Mono<List<Module>> parseHtml(String html) throws APIException {
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
                    module.setModuleCode(courseTable.select("td b font[color=\"#0000FF\"]").eq(0).text().trim()).setName(courseTable.select("td b font[color=\"#0000FF\"]").eq(1).text().trim()).setCredits(Float.parseFloat(courseTable.select("td b font[color=\"#0000FF\"]").eq(2).text().trim().replace(" AU", "")));

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

                            if (columns.get(1).text().trim().isEmpty() && lastSession.getIndexId() != null) {
                                session.setGroup(lastSession.getGroup());
                            } else {
                                switch (columns.get(1).text().trim()) {
                                    case "LEC/STUDIO":
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
                            }

                            if (columns.get(2).text().trim().isEmpty() && lastSession.getIndexId() != null) {
                                session.setGroup(lastSession.getGroup());
                            } else {
                                session.setGroup(columns.get(2).text().trim());
                            }

                            if (columns.get(3).text().trim().isEmpty() && lastSession.getIndexId() != null) {
                                session.setDay(lastSession.getDay());
                            } else {
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
                            }

                            String time = columns.get(4).text().trim();

                            if (time.isEmpty() && lastSession.getIndexId() != null) {

                                session.setStartHour(lastSession.getStartHour());
                                session.setStartMinute(lastSession.getStartMinute());
                                session.setEndHour(lastSession.getEndHour());
                                session.setEndMinute(lastSession.getEndMinute());

                            } else {
                                String[] timeSplit = time.split("-");
                                String startHour = timeSplit[0].substring(0, 2);
                                String startMinute = timeSplit[0].substring(2);
                                String endHour = timeSplit[1].substring(0, 2);
                                String endMinute = timeSplit[1].substring(2);

                                session.setStartHour(Long.parseLong(startHour));
                                session.setStartMinute(Long.parseLong(startMinute));
                                session.setEndHour(Long.parseLong(endHour));
                                session.setEndMinute(Long.parseLong(endMinute));
                            }

                            if (columns.get(5).text().trim().isEmpty() && lastSession.getIndexId() != null) {
                                session.setVenue(lastSession.getVenue());
                            } else {
                                session.setVenue(columns.get(5).text().trim());
                            }

                            if (columns.get(6).text().trim().isEmpty() && lastSession.getIndexId() != null) {
                                session.setRemark(lastSession.getRemark());
                            } else {
                                session.setRemark(columns.get(6).text().trim());
                            }

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
     * @param courses The list of courses to save to a JSON file
     * @param path    The string path to save the JSON file to
     * @return A Mono that completes when the data is saved to the file
     * @see Mono
     */
    public Mono<Void> saveToJsonFile(List<Module> courses, String path) {
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper

        return Mono.fromRunnable(() -> {
            try {
                // Serialize the list of courses into JSON and write to a file
                objectMapper.writeValue(new File(path), courses);
                System.out.println("Data saved to courses_data.json");
            } catch (IOException e) {
                throw new RuntimeException("Error saving data to file: " + e.getMessage(), e);
            }
        });
    }


}
