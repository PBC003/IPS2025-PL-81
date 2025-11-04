package ips.club.model;

public class User {
    private Integer id;
    private String name;
    private String surname;
    private String email;
    private String iban;

    public User(Integer id, String name, String surname, String email, String iban) {
        this.id = id; this.name = name; this.surname = surname; this.email = email; this.iban = iban;
    }

    public Integer getId()              { return id; }
    public void setId(Integer id)       { this.id = id; }

    public String getName()             { return name; }
    public void setName(String name)    { this.name = name; }

    public String getSurname()         { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail()            { return email; }
    public void setEmail(String email)  { this.email = email; }

    public String getIban()            { return iban; }
    public void setIban(String iban)  { this.iban = iban; }

    @Override public String toString() { return "User{id="+getId()+", name="+getName()+"}"; }

}
