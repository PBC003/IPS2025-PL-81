package ips.club.model;

public class User {
    private Integer id;
    private String name;
    private String surname;
    private String email;
    private String iban;
    private Integer monthlyFeeCents;

    public User(Integer id, String name, String surname, String email, String iban, Integer monthlyFeeCents) {
        this.id = id; this.name = name; this.surname = surname; this.email = email; this.iban = iban; this.monthlyFeeCents = monthlyFeeCents;
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

    public Integer getMonthlyFeeCents()         { return monthlyFeeCents; }
    public void setMonthlyFeeCents(Integer monthlyFeeCents) { this.monthlyFeeCents = monthlyFeeCents; }

    @Override public String toString() { return "User{id="+getId()+", name="+getName()+", monthlyFeeCents="+getMonthlyFeeCents()+"}"; }

}
