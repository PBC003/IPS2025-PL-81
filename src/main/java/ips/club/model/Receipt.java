package ips.club.model;

import java.time.LocalDate;

public class Receipt {
    private Integer id;
    private String receiptNumber;
    private int userId;
    private int amountCents;
    private LocalDate issueDate;
    private LocalDate valueDate;
    private String chargeMonth;
    private String concept;
    private ReceiptStatus status;
    private Integer batchId;

    public Receipt(Integer id, String receiptNumber, int userId, int amountCents,
                   LocalDate issueDate, LocalDate valueDate, String chargeMonth,
                   String concept, ReceiptStatus status, Integer batchId) {
        this.id = id;
        this.receiptNumber = receiptNumber;
        this.userId = userId;
        this.amountCents = amountCents;
        this.issueDate = issueDate;
        this.valueDate = valueDate;
        this.chargeMonth = chargeMonth;
        this.concept = concept;
        this.status = status;
        this.batchId = batchId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getAmountCents() { return amountCents; }
    public void setAmountCents(int amountCents) { this.amountCents = amountCents; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getValueDate() { return valueDate; }
    public void setValueDate(LocalDate valueDate) { this.valueDate = valueDate; }

    public String getChargeMonth() { return chargeMonth; }
    public void setChargeMonth(String chargeMonth) { this.chargeMonth = chargeMonth; }

    public String getConcept() { return concept; }
    public void setConcept(String concept) { this.concept = concept; }

    public ReceiptStatus getStatus() { return status; }
    public void setStatus(ReceiptStatus status) { this.status = status; }

    public Integer getBatchId() { return batchId; }
    public void setBatchId(Integer batchId) { this.batchId = batchId; }

    @Override public String toString() {
       return String.format("Receipt{id=%s, num=%s, user=%s, amount=%s, month=%s, status=%s}",
                                id, receiptNumber, userId, amountCents, chargeMonth, status.name());
    }
}
