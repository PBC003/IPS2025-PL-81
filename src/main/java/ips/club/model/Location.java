package ips.club.model;

public class Location {
    private final Integer id;
    private final String name;

    public Location(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name;
    }
}
