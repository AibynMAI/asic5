package repository;

import config.DbConfig;
import domain.Organizer;

import java.sql.*;
import java.util.*;

public class OrganizerRepositoryJdbc implements OrganizerRepository {

    @Override
    public Organizer save(Organizer o) {
        String sql = """
                INSERT INTO organizers(name,email,organization)
                VALUES (?,?,?) RETURNING id
                """;
        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, o.getName());
            ps.setString(2, o.getEmail());
            ps.setString(3, o.getOrganization());

            ResultSet rs = ps.executeQuery();
            rs.next();
            return new Organizer(rs.getInt("id"),
                    o.getName(), o.getEmail(), o.getOrganization());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Organizer> findById(int id) {
        String sql = "SELECT * FROM organizers WHERE id=?";
        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return Optional.empty();

            return Optional.of(new Organizer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("organization")
            ));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Organizer> findAll() {
        List<Organizer> list = new ArrayList<>();
        try (Connection c = DbConfig.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM organizers")) {

            while (rs.next()) {
                list.add(new Organizer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("organization")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public boolean existsById(int id) {
        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps =
                     c.prepareStatement("SELECT 1 FROM organizers WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(int id) {
        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps =
                     c.prepareStatement("DELETE FROM organizers WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
