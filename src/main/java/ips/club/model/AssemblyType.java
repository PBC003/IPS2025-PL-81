package ips.club.model;

public enum AssemblyType {
    ORDINARY,
    EXTRAORDINARY;

    public static AssemblyType fromDb(String s) {
        return s == null ? ORDINARY : AssemblyType.valueOf(s);
    }

    public String toDb() {
        return name();
    }
}
