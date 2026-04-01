package model;

public abstract class Person {
    protected int id;
    protected String name;
    protected String phone;
    protected String email;

    public Person(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public int getId()       { return id; }
    public String getName()  { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public abstract String getRole();
    public abstract String getInfo();

    @Override
    public String toString() {
        return "[" + getRole() + " #" + id + "] " + name + " | " + phone + " | " + email;
    }
}
