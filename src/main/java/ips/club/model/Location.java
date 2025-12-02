package ips.club.model;

public class Location {
    private final Integer id;
    private final String name;
    private final boolean outdoor;

    public Location(Integer id, String name, boolean outdoor) {
        this.id = id;
        this.name = name;
        this.outdoor = outdoor;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public boolean isOutdoor() { return outdoor; }

    @Override
    public String toString() {
        return name;
    }
}
