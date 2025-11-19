package ips.club.model;

public enum MinutesStatus {
    PENDING_UPLOAD,
    UPLOADED,
    APPROVED;

    public static MinutesStatus fromDb(String s) {
        return s == null ? PENDING_UPLOAD : MinutesStatus.valueOf(s);
    }

    public String toDb() {
        return name();
    }
}
