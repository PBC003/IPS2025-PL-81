package ips.club.controller;

import ips.club.model.Receipt;
import ips.club.service.ReceiptService;
import ips.club.dao.ReceiptDao;
import ips.util.ApplicationException;

import java.time.LocalDate;
import java.util.List;

public class ReceiptController {

    private final ReceiptService receiptService = new ReceiptService();
    private final ReceiptDao receiptDao = new ReceiptDao();

    public Receipt createMonthlyReceipt(int userId, int amountCents, LocalDate valueDate,String chargeMonth, String conceptOverride) throws ApplicationException {
        return receiptService.createMonthlyReceipt(userId, amountCents, valueDate, chargeMonth, conceptOverride);
    }

    public List<Receipt> listUnbatchedByMonth(String chargeMonth) throws ApplicationException {
        if (chargeMonth == null || chargeMonth.trim().isEmpty()) {throw new ApplicationException("Debes indicar el mes en formato YYYYMM.");}
        return receiptDao.listByMonthNotInBatch(chargeMonth);
    }
}
