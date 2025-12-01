package ips.club.model;

public enum ReceiptStatus {
    GENERATED,
    PAID,
    REISSUED,
    CANCELED;

    public static ReceiptStatus fromDb(String s) {return s == null ? GENERATED : ReceiptStatus.valueOf(s);}
    public String toDb() {return name();}
}
