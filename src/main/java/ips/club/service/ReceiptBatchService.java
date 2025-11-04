package ips.club.service;

import ips.club.dao.ReceiptBatchDao;
import ips.club.dao.ReceiptDao;
import ips.club.model.Receipt;
import ips.club.model.ReceiptBatch;
import ips.util.ApplicationException;

import java.util.List;

public class ReceiptBatchService {

    private final ReceiptBatchDao batchDao = new ReceiptBatchDao();
    private final ReceiptDao receiptDao = new ReceiptDao();

    public ReceiptBatch createBatch(ReceiptBatch receiptBatch) {

        if (receiptBatch.getId() != null) {throw new ApplicationException("El lote ya tiene un ID asignado.");}
        if (receiptBatch.getChargeMonth() == null || receiptBatch.getChargeMonth().trim().isEmpty()) {throw new ApplicationException("chargeMonth es obligatorio.");}
        if (receiptBatch.getBankEntity() == null || receiptBatch.getBankEntity().trim().isEmpty()) {throw new ApplicationException("bankEntity es obligatorio.");}
        if (receiptBatch.getStatus() == null) {throw new ApplicationException("status es obligatorio.");}
        if (receiptBatch.getTotalAmount() < 0) {throw new ApplicationException("totalAmount no puede ser negativo.");}
        if (receiptBatch.getReceiptsCnt() < 0) {throw new ApplicationException("receiptsCnt no puede ser negativo.");}

        return batchDao.insert(receiptBatch);
    }

    public void fillBatchWithUnbatchedReceipts(String chargeMonth, int batchId) {

        if (batchId <= 0) throw new ApplicationException("batchId inválido.");
        if (chargeMonth == null || chargeMonth.trim().isEmpty()) {throw new ApplicationException("chargeMonth es obligatorio.");}

        List<Receipt> pending = receiptDao.listByMonthNotInBatch(chargeMonth);
        for (Receipt r : pending) {
            if (!chargeMonth.equals(r.getChargeMonth())) continue;
            receiptDao.assignToBatch(r.getId(), batchId);
        }
    }
}
