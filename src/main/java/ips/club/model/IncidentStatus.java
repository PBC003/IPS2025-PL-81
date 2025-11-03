package ips.club.model;

public enum IncidentStatus {
    OPEN,
    ASSIGNED,
    WAITING_REPLY,
    CLOSED;

    public static IncidentStatus fromDb(String s) {return s == null ? OPEN : IncidentStatus.valueOf(s);}
    public String toDb() {return name();}
}
