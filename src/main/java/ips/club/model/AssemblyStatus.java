package ips.club.model;

public enum AssemblyStatus {
    NOT_HELD,
    HELD;

    public static AssemblyStatus fromDb(String s) {
        if (s == null) return NOT_HELD;
        else return AssemblyStatus.valueOf(s);
    }

    public String toDb() {
        return name();
    }
}
