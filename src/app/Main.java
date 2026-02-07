package app;

import service.EventConsoleService;

import repository.*;
import domain.Event;
import domain.Participant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {

        OrganizerRepository organizerRepo = new OrganizerRepositoryJdbc();
        EventRepository eventRepo = new EventRepositoryJdbc();
        ParticipantRepository participantRepo = new ParticipantRepositoryJdbc();
        RegistrationRepository regRepo = new RegistrationRepositoryJdbc();

        EventConsoleService service = new EventConsoleService(
                organizerRepo, eventRepo, participantRepo, regRepo
        );

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("""
                    
                    ===== EVENT SCHEDULING SYSTEM =====
                    1) Create Organizer
                    2) List Organizers
                    3) Create Event
                    4) List Events
                    5) Update Event Location
                    6) Delete Event
                    7) Create Participant
                    8) List Participants
                    9) Register Participant to Event
                    10) Show Participants of Event
                    11) Delete Participant
                    0) Exit
                    """);

            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> {
                        System.out.print("Name: ");
                        String name = sc.nextLine();
                        System.out.print("Email: ");
                        String email = sc.nextLine();
                        System.out.print("Organization: ");
                        String org = sc.nextLine();
                        System.out.println("Saved: " + service.createOrganizer(name, email, org));
                    }
                    case "2" -> service.listOrganizers().forEach(System.out::println);

                    case "3" -> {
                        System.out.print("Title: ");
                        String title = sc.nextLine();
                        System.out.print("Location: ");
                        String loc = sc.nextLine();
                        System.out.print("Start (yyyy-MM-dd HH:mm): ");
                        LocalDateTime start = LocalDateTime.parse(sc.nextLine().trim(), FMT);
                        System.out.print("End (yyyy-MM-dd HH:mm): ");
                        LocalDateTime end = LocalDateTime.parse(sc.nextLine().trim(), FMT);
                        System.out.print("Organizer ID: ");
                        int orgId = Integer.parseInt(sc.nextLine().trim());

                        Event e = service.createEvent(title, loc, start, end, orgId);
                        System.out.println("Saved: " + e);
                    }
                    case "4" -> service.listEvents().forEach(System.out::println);

                    case "5" -> {
                        System.out.print("Event ID: ");
                        int eventId = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("New Location: ");
                        String newLoc = sc.nextLine();
                        service.updateEventLocation(eventId, newLoc);
                        System.out.println("Updated.");
                    }

                    case "6" -> {
                        System.out.print("Event ID: ");
                        int eventId = Integer.parseInt(sc.nextLine().trim());
                        service.deleteEvent(eventId);
                        System.out.println("Deleted.");
                    }

                    case "7" -> {
                        System.out.print("Name: ");
                        String name = sc.nextLine();
                        System.out.print("Email: ");
                        String email = sc.nextLine();
                        Participant p = service.createParticipant(name, email);
                        System.out.println("Saved: " + p);
                    }

                    case "8" -> service.listParticipants().forEach(System.out::println);

                    case "9" -> {
                        System.out.print("Event ID: ");
                        int eventId = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Participant ID: ");
                        int pid = Integer.parseInt(sc.nextLine().trim());
                        service.registerParticipant(eventId, pid);
                        System.out.println("Registered.");
                    }

                    case "10" -> {
                        System.out.print("Event ID: ");
                        int eventId = Integer.parseInt(sc.nextLine().trim());
                        service.participantsOfEvent(eventId).forEach(System.out::println);
                    }

                    case "11" -> {
                        System.out.print("Participant ID: ");
                        int pid = Integer.parseInt(sc.nextLine().trim());
                        service.deleteParticipant(pid);
                        System.out.println("Deleted.");
                    }

                    case "0" -> {
                        System.out.println("Bye!");
                        return;
                    }

                    default -> System.out.println("Unknown option.");
                }
            } catch (Exception e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }
}
