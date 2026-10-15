
package statemachine.app;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import statemachine.model.Event;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public class DistributedEventMediator {
    private static final String BASE_DIR = "event_queue";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void initialize() {
        Path path = Paths.get(BASE_DIR);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("Created event queue directory: " + BASE_DIR);
            } catch (IOException e) {
                System.err.println("Failed to create event queue directory: " + e.getMessage());
               e.printStackTrace();
            }
        }

        clearEventFiles();
    }

    private static void clearEventFiles() {
        Path path = Paths.get(BASE_DIR);
        try (Stream<Path> files = Files.list(path)) {
            files.filter(Files::isRegularFile)
                 .forEach(p -> {
                     try {
                         Files.delete(p);
                         System.out.println("Deleted old event file: " + p.getFileName());
                     } catch (IOException e) {
                         System.err.println("Failed to delete old event file " + p.getFileName() + ": " + e.getMessage());
                     }
                 });
        } catch (IOException e) {
            System.err.println("Error listing files in event queue directory: " + e.getMessage());
        }
    }

    public static void publishEvent(String receiverName, Event event) {
        Path filePath = Paths.get(BASE_DIR, receiverName + "_events.json");
        List<Event> existingEvents = readEventsFromFile(filePath);
        Queue<Event> eventQueue = new ConcurrentLinkedQueue<>(existingEvents);
        eventQueue.add(event);

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            GSON.toJson(eventQueue.stream().collect(Collectors.toList()), writer);
            System.out.println("[MEDIATOR] Published event '" + event.getEventType() + "' for " + receiverName);
        } catch (IOException e) {
            System.err.println("Failed to publish event to " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Event> consumeEvents(String receiverName) {
        Path filePath = Paths.get(BASE_DIR, receiverName + "_events.json");
        List<Event> events = readEventsFromFile(filePath);
        if (!events.isEmpty()) {
            try {
                Files.write(filePath, "[]".getBytes()); 
                System.out.println("[MEDIATOR] Cleared events from " + receiverName + "'s queue.");
            } catch (IOException e) {
                System.err.println("Failed to clear event file " + filePath + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
          return events;
    }

    private static List<Event> readEventsFromFile(Path filePath) {
        if (!Files.exists(filePath)) {
            return Collections.emptyList();
        }
        try (FileReader reader = new FileReader(filePath.toFile())) {
       
            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<Event>>(){}.getType();
             List<Event> events = GSON.fromJson(reader, listType);
             return events != null ? events : Collections.emptyList();
        } catch (IOException e) {
            System.err.println("Error reading events from " + filePath + ": " + e.getMessage());
              return Collections.emptyList();
        }}}
    
