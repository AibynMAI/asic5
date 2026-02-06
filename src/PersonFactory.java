package domain;

public class PersonFactory {

    public static Organizer organizer(int id, String n, String e, String org) {
        return new Organizer(id, n, e, org);
    }
}
