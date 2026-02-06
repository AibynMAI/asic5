package repository;
import domain.Event;
import config.DbConfig;
import java.sql.*;
import java.util.*;

public class EventRepositoryJdbc implements EventRepository {

    public Event save(Event e){
        String sql="INSERT INTO events(title,location,organizer_id) VALUES(?,?,?) RETURNING id";
        try(Connection c=DbConfig.getConnection();
            PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,e.getTitle());
            ps.setString(2,e.getLocation());
            ps.setInt(3,e.getOrganizerId());
            ResultSet rs=ps.executeQuery(); rs.next();
            return new Event.Builder()
                    .id(rs.getInt(1))
                    .title(e.getTitle())
                    .location(e.getLocation())
                    .organizerId(e.getOrganizerId())
                    .build();
        }catch(Exception ex){ throw new RuntimeException(ex); }
    }

    public Optional<Event> findById(int id){
        requirePositive(id);
        try(Connection c=DbConfig.getConnection();
            PreparedStatement ps=c.prepareStatement("SELECT * FROM events WHERE id=?")){
            ps.setInt(1,id);
            ResultSet rs=ps.executeQuery();
            if(!rs.next()) return Optional.empty();
            return Optional.of(new Event.Builder()
                    .id(rs.getInt("id"))
                    .title(rs.getString("title"))
                    .location(rs.getString("location"))
                    .organizerId(rs.getInt("organizer_id"))
                    .build());
        }catch(Exception ex){ throw new RuntimeException(ex); }
    }

    public List<Event> findAll(){
        List<Event> list=new ArrayList<>();
        try(Connection c=DbConfig.getConnection();
            Statement st=c.createStatement();
            ResultSet rs=st.executeQuery("SELECT * FROM events")){
            while(rs.next()){
                list.add(new Event.Builder()
                        .id(rs.getInt("id"))
                        .title(rs.getString("title"))
                        .location(rs.getString("location"))
                        .organizerId(rs.getInt("organizer_id"))
                        .build());
            }
        }catch(Exception ex){ throw new RuntimeException(ex); }
        return list;
    }

    public void delete(int id){
        requirePositive(id);
        try(Connection c=DbConfig.getConnection();
            PreparedStatement ps=c.prepareStatement("DELETE FROM events WHERE id=?")){
            ps.setInt(1,id); ps.executeUpdate();
        }catch(Exception ex){ throw new RuntimeException(ex); }
    }
}
