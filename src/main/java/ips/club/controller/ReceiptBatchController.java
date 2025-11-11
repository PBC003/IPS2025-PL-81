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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ips.club.dao.UserDao;
import ips.club.model.User;
import ips.club.service.ReceiptService;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class ReceiptBatchController {

    private final ReceiptBatchDao batchDao = new ReceiptBatchDao();
    private final ReceiptDao receiptDao = new ReceiptDao();
    private final ReceiptExportService exportService = new ReceiptExportService();
    private final ReceiptBatchService receiptService = new ReceiptBatchService();
    private final UserDao userDao = new UserDao();
    private final ReceiptService receiptSrv = new ReceiptService();

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

    public List<User> findUsersWithoutReceiptFor(String yyyymm) {
        if (yyyymm == null || yyyymm.length() != 6) {
            throw new ApplicationException("YYYYMM obligatorio.");
        }
        return userDao.findAllWithoutReceiptFor(yyyymm);
    }

    public String defaultConceptFor(String yyyymm) {
        return receiptSrv.defaultConceptFromMonth(yyyymm);
    }

    public Path createBatchGeneratingReceiptsAndExport(
            String chargeMonth,
            String bankEntity,
            String fileName,
            List<Integer> userIds) {

        if (chargeMonth == null || chargeMonth.length() != 6)
            throw new ApplicationException("chargeMonth (YYYYMM) es obligatorio.");
        if (bankEntity == null || bankEntity.trim().isEmpty())
            throw new ApplicationException("La entidad bancaria es obligatoria.");
        if (fileName == null || fileName.trim().isEmpty())
            throw new ApplicationException("El nombre de fichero es obligatorio.");
        if (userIds == null || userIds.isEmpty())
            throw new ApplicationException("Debe seleccionar al menos un usuario.");

        ReceiptBatch batch = new ReceiptBatch(
                null, chargeMonth, bankEntity, LocalDateTime.now(),
                ReceiptBatchStatus.GENERATED, fileName, 0, 0);
        int batchId = batchDao.insert(batch).getId();

        List<Integer> receiptIds = new ArrayList<>();
        for (Integer uid : userIds) {
            User u = userDao.findBasicById(uid);
            if (u == null)
                continue;
            int amount = u.getMonthlyFeeCents();
            Receipt r = receiptSrv.createMonthlyReceipt(uid, amount, null, chargeMonth, null);
            receiptIds.add(r.getId());
        }

        receiptDao.assignToBatch(batchId, receiptIds);
        batchDao.recalcTotals(batchId);

        Path csv = exportService.exportBatchToCsvUsingBatchFileName(batchId);

        return csv;
    }

    public String toYYYYMM(YearMonth ym) {
        return ym.format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    public Path createOneBatchGeneratingReceiptsForMultipleMonthsAndExport(
            String bankEntity,
            String fileName,
            Map<String, List<Integer>> userIdsByMonth) {

        if (bankEntity == null || bankEntity.trim().isEmpty())
            throw new ApplicationException("La entidad bancaria es obligatoria.");
        if (fileName == null || fileName.trim().isEmpty())
            throw new ApplicationException("El nombre de fichero es obligatorio.");
        if (userIdsByMonth == null || userIdsByMonth.isEmpty())
            throw new ApplicationException("No hay usuarios a procesar.");

        Set<String> months = new HashSet<String>();

        for (String m : userIdsByMonth.keySet()) {
            if (m == null || m.length() != 6) {
                throw new ApplicationException("Mes inválido (YYYYMM): " + m);
            }
            months.add(m);
        }

        String batchChargeMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

        ReceiptBatch batch = new ReceiptBatch(
                null,
                batchChargeMonth,
                bankEntity,
                LocalDateTime.now(),
                ReceiptBatchStatus.GENERATED,
                fileName,
                0,
                0);
        int batchId = batchDao.insert(batch).getId();

        List<Integer> receiptIds = new ArrayList<Integer>();

        for (Map.Entry<String, List<Integer>> en : userIdsByMonth.entrySet()) {
            String yyyymm = en.getKey();
            List<Integer> userIds = en.getValue();
            if (userIds == null || userIds.isEmpty())
                continue;

            for (Integer uid : userIds) {
                User u = userDao.findBasicById(uid);
                if (u == null)
                    continue;
                int amount = u.getMonthlyFeeCents();
                Receipt r = receiptSrv.createMonthlyReceipt(uid, amount, null, yyyymm, null);
                receiptIds.add(r.getId());
            }
        }

        if (!receiptIds.isEmpty()) {
            receiptDao.assignToBatch(batchId, receiptIds);
            batchDao.recalcTotals(batchId);
        }

        Path out = exportService.exportBatchToCsvUsingBatchFileName(batchId);
        return out;
    }
}
