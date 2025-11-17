package ips.club.model;

public enum AssemblyStatus {
    SCHEDULED,
    WAITING,
    FINISHED;

    public static AssemblyStatus fromDb(String s) {
        return s == null ? SCHEDULED : AssemblyStatus.valueOf(s);
    }

    public String toDb() {
        return name();
    }
}
