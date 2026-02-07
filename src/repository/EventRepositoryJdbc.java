package repository;

import config.DbConfig;
import domain.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventRepositoryJdbc implements EventRepository {

    @Override
    public Event save(Event e) {
        String sql = """
                INSERT INTO events(title, location, start_time, end_time, organizer_id)
                VALUES (?, ?, ?, ?, ?) RETURNING id
                """;

        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, e.getTitle());
            ps.setString(2, e.getLocation());
            ps.setTimestamp(3, Timestamp.valueOf(e.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(e.getEndTime()));
            ps.setInt(5, e.getOrganizerId());

            ResultSet rs = ps.executeQuery();
            rs.next();

            return new Event(
                    rs.getInt("id"),
                    e.getTitle(),
                    e.getLocation(),
                    e.getStartTime(),
                    e.getEndTime(),
                    e.getOrganizerId()
            );

        } catch (SQLException ex) {
            throw new RuntimeException("Save event error", ex);
        }
    }

    @Override
    public Optional<Event> findById(int id) {
        String sql = "SELECT * FROM events WHERE id=?";

        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return Optional.empty();
            return Optional.of(map(rs));

        } catch (SQLException ex) {
            throw new RuntimeException("Find event error", ex);
        }
    }

    @Override
    public List<Event> findAll() {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY id";

        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException ex) {
            throw new RuntimeException("List events error", ex);
        }

        return list;
    }

    @Override
    public boolean existsById(int id) {
        String sql = "SELECT 1 FROM events WHERE id=?";

        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeQuery().next();

        } catch (SQLException ex) {
            throw new RuntimeException("Exists event error", ex);
        }
    }

    @Override
    public void updateLocation(int eventId, String location) {
        String sql = "UPDATE events SET location=? WHERE id=?";

        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, location);
            ps.setInt(2, eventId);
            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException("Update event location error", ex);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM events WHERE id=?";

        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException("Delete event error", ex);
        }
    }

    private Event map(ResultSet rs) throws SQLException {
        return new Event(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("location"),
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getTimestamp("end_time").toLocalDateTime(),
                rs.getInt("organizer_id")
        );
    }
}
