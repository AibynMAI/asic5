package repository;

import domain.Participant;
import java.util.List;

public interface RegistrationRepository {
    void register(int eventId, int participantId);
    List<Participant> participantsOfEvent(int eventId);
}
