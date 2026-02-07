package repository;

import config.DbConfig;
import domain.Participant;

import java.sql.*;
import java.util.*;

public class ParticipantRepositoryJdbc implements ParticipantRepository {

    @Override
    public Participant save(Participant p) {
        String sql = """
                INSERT INTO participants(name,email)
                VALUES (?,?) RETURNING id
                """;
        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getEmail());

            ResultSet rs = ps.executeQuery();
            rs.next();
            return new Participant(rs.getInt("id"), p.getName(), p.getEmail());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Participant> findById(int id) {
        String sql = "SELECT * FROM participants WHERE id=?";
        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return Optional.empty();

            return Optional.of(new Participant(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email")
            ));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Participant> findAll() {
        List<Participant> list = new ArrayList<>();
        try (Connection c = DbConfig.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM participants")) {

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

    @Override
    public boolean existsById(int id) {
        try (Connection c = DbConfig.getConnection();
             PreparedStatement ps =
                     c.prepareStatement("SELECT 1 FROM participants WHERE id=?")) {
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
                     c.prepareStatement("DELETE FROM participants WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
