package ips.club.controller;

import ips.club.dao.ReceiptBatchDao;
import ips.club.dao.ReceiptDao;
import ips.club.model.Receipt;
import ips.club.model.ReceiptBatch;
import ips.club.model.ReceiptBatchStatus;
import ips.club.service.ReceiptBatchService;
import ips.club.service.ReceiptExportService;
import ips.util.ApplicationException;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReceiptBatchController {

    private final ReceiptBatchDao batchDao = new ReceiptBatchDao();
    private final ReceiptDao receiptDao = new ReceiptDao();
    private final ReceiptExportService exportService = new ReceiptExportService();
    private final ReceiptBatchService receiptService = new ReceiptBatchService();

    public ReceiptBatch createBatch(String chargeMonth, String bankEntity, String fileName) {
        if (chargeMonth == null || chargeMonth.length() != 6)
            throw new ApplicationException("chargeMonth (YYYYMM) es obligatorio.");
        if (bankEntity == null || bankEntity.trim().isEmpty())
            throw new ApplicationException("bankEntity es obligatorio.");
        if (fileName == null || fileName.trim().isEmpty())
            throw new ApplicationException("fileName es obligatorio.");

        ReceiptBatch rb = new ReceiptBatch(
                null, chargeMonth, bankEntity, LocalDateTime.now(),
                ReceiptBatchStatus.GENERATED, fileName, 0, 0);
        return batchDao.insert(rb);
    }

    public List<ReceiptBatch> listBatches() {
        return batchDao.findAll();
    }

    public void cancelBatch(int batchId) {
        batchDao.markCanceled(batchId);
        receiptDao.cancelAndReleaseReceipts(batchId);
    }

    public Path exportBatch(int batchId) {
        return exportService.exportBatchToCsvUsingBatchFileName(batchId);
    }

    public void fillBatchWithUnbatchedReceipts(String chargeMonth, int batchId) {
        receiptService.fillBatchWithUnbatchedReceipts(chargeMonth, batchId);
    }

    public void createBatchWithReceipts(String chargeMonth,
            String bankEntity,
            String fileName,
            List<Integer> receiptIds) throws ApplicationException {
        if (bankEntity == null || bankEntity.trim().isEmpty()) {
            throw new ApplicationException("La entidad bancaria es obligatoria.");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new ApplicationException("El nombre de fichero es obligatorio.");
        }
        if (receiptIds == null || receiptIds.isEmpty()) {
            throw new ApplicationException("Debe seleccionar al menos un recibo.");
        }
        if (chargeMonth == null || chargeMonth.length() != 6) {
            throw new ApplicationException("El mes de cargo (YYYYMM) es obligatorio.");
        }
        ReceiptBatch r = new ReceiptBatch(null, chargeMonth, bankEntity, null, null, fileName, 0, 0);
        int batchId = batchDao.insert(r).getId();
        receiptDao.assignToBatch(batchId, receiptIds);
        batchDao.recalcTotals(batchId);
    }

    public List<Receipt> findUnbatchedReceiptsAll() {
        List<Receipt> list = receiptDao.findUnbatchedAll();
        return list == null ? new ArrayList<Receipt>() : list;
    }

}
