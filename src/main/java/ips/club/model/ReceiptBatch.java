package ips.club.model;

import java.time.LocalDateTime;

public class ReceiptBatch {
    private Integer id;
    private String chargeMonth;
    private String bankEntity;
    private LocalDateTime createdAt;
    private ReceiptBatchStatus status = ReceiptBatchStatus.GENERATED;
    private String fileName;
    private int totalAmount;
    private int receiptsCnt;

    public ReceiptBatch(Integer id, String chargeMonth, String bankEntity,
                        LocalDateTime createdAt, ReceiptBatchStatus status,
                        String fileName, int totalAmount, int receiptsCnt) {
        this.id = id;
        this.chargeMonth = chargeMonth;
        this.bankEntity = bankEntity;
        this.createdAt = createdAt;
        this.status = status;
        this.fileName = fileName;
        this.totalAmount = totalAmount;
        this.receiptsCnt = receiptsCnt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getChargeMonth() { return chargeMonth; }
    public void setChargeMonth(String chargeMonth) { this.chargeMonth = chargeMonth; }

    public String getBankEntity() { return bankEntity; }
    public void setBankEntity(String bankEntity) { this.bankEntity = bankEntity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public ReceiptBatchStatus getStatus() { return status; }
    public void setStatus(ReceiptBatchStatus status) { this.status = status; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }

    public int getReceiptsCnt() { return receiptsCnt; }
    public void setReceiptsCnt(int receiptsCnt) { this.receiptsCnt = receiptsCnt; }

    @Override public String toString() {
        return String.format("ReceiptBatch{id=%d, month=%s, status=%s, total=%d, cnt=%d}",
                                id, chargeMonth, status.name(), totalAmount, receiptsCnt);
    }
}
