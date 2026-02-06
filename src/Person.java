package domain;
import java.util.Objects;

public abstract class Person {
    private final int id;
    private String name;
    private String email;

    protected Person(int id, String name, String email) {
        this.id = id;
        setName(name);
        setEmail(email);
    }

    public int getId(){ return id; }
    public String getName(){ return name; }
    public void setName(String name){
        if(name == null || name.isBlank()) throw new IllegalArgumentException("Empty name");
        this.name = name;
    }
    public String getEmail(){ return email; }
    public void setEmail(String email){
        if(email == null || !email.contains("@")) throw new IllegalArgumentException("Bad email");
        this.email = email;
    }

    public abstract String role(); // polymorphism

    @Override public String toString(){ return role()+": "+name; }
    @Override public boolean equals(Object o){
        return (o instanceof Person p) && p.id == id;
    }
    @Override public int hashCode(){ return Objects.hash(id); }
}
