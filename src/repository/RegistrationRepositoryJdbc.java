package repository;

import config.DbConfig;
import domain.Participant;

import java.sql.*;
import java.util.*;

public class RegistrationRepositoryJdbc implements RegistrationRepository {

    @Override
    public void register(int eventId, int participantId) {
        String sql = """
                INSERT INTO registrations(event_id,participant_id)
                VALUES (?,?)
                """;
        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.setInt(2, participantId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Participant> participantsOfEvent(int eventId) {
        String sql = """
                SELECT p.id,p.name,p.email
                FROM participants p
                JOIN registrations r ON p.id=r.participant_id
                WHERE r.event_id=?
                """;
        List<Participant> list = new ArrayList<>();

        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Participant(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
