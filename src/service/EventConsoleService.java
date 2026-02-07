package service;

import domain.Event;
import domain.Organizer;
import domain.Participant;
import exception.NotFoundException;
import exception.ValidationException;
import repository.EventRepository;
import repository.OrganizerRepository;
import repository.ParticipantRepository;
import repository.RegistrationRepository;

import java.time.LocalDateTime;
import java.util.List;

public class EventConsoleService {

    private final OrganizerRepository organizerRepo;
    private final EventRepository eventRepo;
    private final ParticipantRepository participantRepo;
    private final RegistrationRepository regRepo;

    public EventConsoleService(OrganizerRepository organizerRepo,
                               EventRepository eventRepo,
                               ParticipantRepository participantRepo,
                               RegistrationRepository regRepo) {
        this.organizerRepo = organizerRepo;
        this.eventRepo = eventRepo;
        this.participantRepo = participantRepo;
        this.regRepo = regRepo;
    }

    // ---------- ORGANIZERS ----------
    public Organizer createOrganizer(String name, String email, String org) {
        if (name == null || name.isBlank()) throw new ValidationException("Name required");
        if (email == null || !email.contains("@")) throw new ValidationException("Valid email required");
        if (org == null || org.isBlank()) throw new ValidationException("Organization required");
        return organizerRepo.save(new Organizer(0, name.trim(), email.trim(), org.trim()));
    }

    public List<Organizer> listOrganizers() {
        return organizerRepo.findAll();
    }

    // ---------- EVENTS ----------
    public Event createEvent(String title, String location,
                             LocalDateTime start, LocalDateTime end,
                             int organizerId) {

        if (!organizerRepo.existsById(organizerId))
            throw new NotFoundException("Organizer not found: " + organizerId);

        if (title == null || title.isBlank()) throw new ValidationException("Title required");
        if (location == null || location.isBlank()) throw new ValidationException("Location required");
        if (end.isBefore(start) || end.isEqual(start))
            throw new ValidationException("End time must be after start time");

        return eventRepo.save(new Event(0, title.trim(), location.trim(), start, end, organizerId));
    }

    public List<Event> listEvents() {
        return eventRepo.findAll();
    }

    public void updateEventLocation(int eventId, String newLocation) {
        if (!eventRepo.existsById(eventId))
            throw new NotFoundException("Event not found: " + eventId);
        if (newLocation == null || newLocation.isBlank())
            throw new ValidationException("Location required");
        eventRepo.updateLocation(eventId, newLocation.trim());
    }

    public void deleteEvent(int eventId) {
        if (!eventRepo.existsById(eventId))
            throw new NotFoundException("Event not found: " + eventId);
        eventRepo.deleteById(eventId);
    }

    // ---------- PARTICIPANTS ----------
    public Participant createParticipant(String name, String email) {
        if (name == null || name.isBlank()) throw new ValidationException("Name required");
        if (email == null || !email.contains("@")) throw new ValidationException("Valid email required");
        return participantRepo.save(new Participant(0, name.trim(), email.trim()));
    }

    public List<Participant> listParticipants() {
        return participantRepo.findAll();
    }

    public void deleteParticipant(int participantId) {
        if (!participantRepo.existsById(participantId))
            throw new NotFoundException("Participant not found: " + participantId);
        participantRepo.deleteById(participantId);
    }

    // ---------- REGISTRATIONS ----------
    public void registerParticipant(int eventId, int participantId) {
        if (!eventRepo.existsById(eventId))
            throw new NotFoundException("Event not found: " + eventId);
        if (!participantRepo.existsById(participantId))
            throw new NotFoundException("Participant not found: " + participantId);
        regRepo.register(eventId, participantId);
    }

    public List<Participant> participantsOfEvent(int eventId) {
        if (!eventRepo.existsById(eventId))
            throw new NotFoundException("Event not found: " + eventId);
        return regRepo.participantsOfEvent(eventId);
    }
}
