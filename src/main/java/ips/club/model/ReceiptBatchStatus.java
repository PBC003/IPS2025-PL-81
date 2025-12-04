package ips.club.model;

public enum ReceiptBatchStatus {
    GENERATED,
    EXPORTED,
    PROCESSED,
    CANCELED;

    public static ReceiptBatchStatus fromDb(String s) {return s == null ? GENERATED : ReceiptBatchStatus.valueOf(s);}
    public String toDb() {return name();}
}

