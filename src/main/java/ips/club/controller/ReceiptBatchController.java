// src/main/java/ips/club/controller/ReceiptBatchController.java
package ips.club.controller;

import ips.club.dao.ReceiptBatchDao;
import ips.club.model.ReceiptBatch;
import ips.club.model.ReceiptBatchStatus;
import ips.club.service.ReceiptExportService;
import ips.util.ApplicationException;

import java.nio.file.Path;
import java.time.LocalDateTime;

public class ReceiptBatchController {

    private final ReceiptBatchDao batchDao = new ReceiptBatchDao();
    private final ReceiptExportService exportService = new ReceiptExportService();

    public ReceiptBatch createBatch(String chargeMonth, String bankEntity, String fileName) {
        if (chargeMonth == null || chargeMonth.trim().isEmpty()) {throw new ApplicationException("chargeMonth (YYYYMM) es obligatorio.");}

        ReceiptBatch newBatch = new ReceiptBatch(null, chargeMonth, bankEntity, LocalDateTime.now(),
                ReceiptBatchStatus.GENERATED, fileName, 0, 0);

        return batchDao.insert(newBatch);
    }


    public Path exportBatch(int batchId) {
        return exportService.exportBatchToCsvUsingBatchFileName(batchId);
    }
}
