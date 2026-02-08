package domain;

public class Organizer {

    private long id;
    private String name;
    private String email;
    private String organization;

    public Organizer(long id, String name, String email, String organization) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.organization = organization;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getOrganization() { return organization; }

    @Override
    public String toString() {
        return "Organizer{id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", organization='" + organization + '\'' +
                '}';
    }
}
