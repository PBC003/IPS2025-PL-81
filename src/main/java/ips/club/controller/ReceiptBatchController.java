package ips.club.controller;

import ips.club.dao.ReceiptBatchDao;
import ips.club.dao.ReceiptDao;
import ips.club.dao.UserDao;
import ips.club.model.Receipt;
import ips.club.model.ReceiptBatch;
import ips.club.model.ReceiptBatchStatus;
import ips.club.model.ReceiptStatus;
import ips.club.model.User;
import ips.club.service.ReceiptBatchService;
import ips.club.service.ReceiptExportService;
import ips.club.service.ReceiptService;
import ips.util.ApplicationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReceiptBatchController {

    private final ReceiptBatchDao batchDao = new ReceiptBatchDao();
    private final ReceiptDao receiptDao = new ReceiptDao();
    private final ReceiptExportService exportService = new ReceiptExportService();
    private final ReceiptBatchService receiptBatchService = new ReceiptBatchService();
    private final UserDao userDao = new UserDao();
    private final ReceiptService receiptService = new ReceiptService();

    private static final int REISSUE_SURCHARGE_PERCENT = 15;

    private String lastImportSummary;

    public String getLastImportSummary() {return lastImportSummary;}

    public ReceiptBatch createBatch(String chargeMonth, String bankEntity, String fileName) {
        if (chargeMonth == null || chargeMonth.length() != 6) {throw new ApplicationException("chargeMonth (YYYYMM) es obligatorio.");}
        if (bankEntity == null || bankEntity.trim().isEmpty()) {throw new ApplicationException("bankEntity es obligatorio.");}
        if (fileName == null || fileName.trim().isEmpty()) {throw new ApplicationException("fileName es obligatorio.");}

        ReceiptBatch rb = new ReceiptBatch(null,chargeMonth,bankEntity,LocalDateTime.now(),ReceiptBatchStatus.GENERATED,fileName,0,0);
        return batchDao.insert(rb);
    }

    public List<ReceiptBatch> listBatches() {return batchDao.findAll();}

    public void cancelBatch(int batchId) {
        batchDao.markCanceled(batchId);
        receiptDao.cancelAndReleaseReceipts(batchId);
    }

    public Path exportBatch(int batchId) {return exportService.exportBatchToCsvUsingBatchFileName(batchId);}

    public void fillBatchWithUnbatchedReceipts(String chargeMonth, int batchId) {receiptBatchService.fillBatchWithUnbatchedReceipts(chargeMonth, batchId);}

    public void createBatchWithReceipts(String chargeMonth,String bankEntity,String fileName,List<Integer> receiptIds) {
        if (bankEntity == null || bankEntity.trim().isEmpty()) {throw new ApplicationException("La entidad bancaria es obligatoria.");}
        if (fileName == null || fileName.trim().isEmpty()) {throw new ApplicationException("El nombre de fichero es obligatorio.");}
        if (receiptIds == null || receiptIds.isEmpty()) {throw new ApplicationException("Debe seleccionar al menos un recibo.");}
        if (chargeMonth == null || chargeMonth.length() != 6) {throw new ApplicationException("El mes de cargo (YYYYMM) es obligatorio.");}

        ReceiptBatch batch = new ReceiptBatch(null,chargeMonth,bankEntity,null,null,fileName,0,0);
        int batchId = batchDao.insert(batch).getId();

        receiptDao.assignToBatch(batchId, receiptIds);
        batchDao.recalcTotals(batchId);
    }

    public List<Receipt> findUnbatchedReceiptsAll() {
        List<Receipt> list = receiptDao.findUnbatchedAll();
        return list == null ? new ArrayList<Receipt>() : list;
    }

    public List<User> findUsersWithoutReceiptFor(String yyyymm) {
        if (yyyymm == null || yyyymm.length() != 6) {throw new ApplicationException("YYYYMM obligatorio.");}
        return userDao.findAllWithoutReceiptFor(yyyymm);
    }

    public String defaultConceptFor(String chargeMonth) {return receiptService.defaultConceptFromMonth(chargeMonth);}

    public Path createBatchGeneratingReceiptsAndExport(String chargeMonth,String bankEntity,String fileName,List<Integer> userIds) {

        if (chargeMonth == null || chargeMonth.length() != 6) {throw new ApplicationException("chargeMonth (YYYYMM) es obligatorio.");}
        if (bankEntity == null || bankEntity.trim().isEmpty()) {throw new ApplicationException("La entidad bancaria es obligatoria.");}
        if (fileName == null || fileName.trim().isEmpty()) {throw new ApplicationException("El nombre de fichero es obligatorio.");}
        if (userIds == null || userIds.isEmpty()) {throw new ApplicationException("Debe seleccionar al menos un usuario.");}

        ReceiptBatch batch = new ReceiptBatch(null,chargeMonth,bankEntity,LocalDateTime.now(),ReceiptBatchStatus.GENERATED,fileName,0,0);
        int batchId = batchDao.insert(batch).getId();

        List<Integer> receiptIds = new ArrayList<>();
        for (Integer uid : userIds) {
            User u = userDao.findBasicById(uid);
            if (u == null) {continue;}

            int amount = u.getMonthlyFeeCents();
            Receipt r = receiptService.createMonthlyReceipt(uid, amount, null, chargeMonth, null);
            receiptIds.add(r.getId());
        }

        if (!receiptIds.isEmpty()) {
            receiptDao.assignToBatch(batchId, receiptIds);
            batchDao.recalcTotals(batchId);
        }

        return exportService.exportBatchToCsvUsingBatchFileName(batchId);
    }

    public String toYYYYMM(YearMonth ym) {return ym.format(DateTimeFormatter.ofPattern("yyyyMM"));}

    public Path createOneBatchGeneratingReceiptsForMultipleMonthsAndExport(String bankEntity,String fileName,Map<String, List<Integer>> userIdsByMonth) {

        if (bankEntity == null || bankEntity.trim().isEmpty()) {throw new ApplicationException("La entidad bancaria es obligatoria.");}
        if (fileName == null || fileName.trim().isEmpty()) {throw new ApplicationException("El nombre de fichero es obligatorio.");}
        if (userIdsByMonth == null || userIdsByMonth.isEmpty()) {throw new ApplicationException("No hay usuarios a procesar.");}

        Set<String> months = new HashSet<>();
        for (String m : userIdsByMonth.keySet()) {
            if (m == null || m.length() != 6) {throw new ApplicationException("Mes inválido (YYYYMM): " + m);}
            months.add(m);
        }

        String batchChargeMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

        ReceiptBatch batch = new ReceiptBatch(null,batchChargeMonth,bankEntity,LocalDateTime.now(),ReceiptBatchStatus.GENERATED,fileName,0,0);
        int batchId = batchDao.insert(batch).getId();

        List<Integer> receiptIds = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> en : userIdsByMonth.entrySet()) {
            String yyyymm = en.getKey();
            List<Integer> userIds = en.getValue();
            if (userIds == null || userIds.isEmpty()) {continue;}

            for (Integer uid : userIds) {
                User u = userDao.findBasicById(uid);
                if (u == null) {continue;}
                int amount = u.getMonthlyFeeCents();
                Receipt r = receiptService.createMonthlyReceipt(uid, amount, null, yyyymm, null);
                receiptIds.add(r.getId());
            }
        }

        if (!receiptIds.isEmpty()) {
            receiptDao.assignToBatch(batchId, receiptIds);
            batchDao.recalcTotals(batchId);
        }

        return exportService.exportBatchToCsvUsingBatchFileName(batchId);
    }

    public void importReturnedBatch(int batchId, Path csvPath) {
        if (batchId <= 0) {throw new ApplicationException("batchId inválido.");}
        if (csvPath == null) {throw new ApplicationException("Debe seleccionar un fichero.");}

        lastImportSummary = null;

        ReceiptBatch batch = batchDao.findById(batchId);
        if (batch == null) {throw new ApplicationException("No existe el lote " + batchId);}
        if (batch.getStatus() != ReceiptBatchStatus.EXPORTED) {throw new ApplicationException("Solo se pueden importar devoluciones de un lote exportado.");}

        Set<String> returnedNumbers = readReturnedReceiptNumbers(csvPath);
        if (returnedNumbers.isEmpty()) {throw new ApplicationException("El fichero de devoluciones no contiene recibos.");}

        List<Receipt> receipts = receiptDao.listByBatch(batchId);
        if (receipts.isEmpty()) {throw new ApplicationException("El lote no tiene recibos.");}

        LocalDate today = LocalDate.now();

        int paidCount = 0;
        int returnedCount = 0;
        int reissuedCount = 0;
        int reissuedTotalCents = 0;

        for (Receipt r : receipts) {
            String number = r.getReceiptNumber();
            if (number == null) {continue;}

            if (returnedNumbers.contains(number)) {
                receiptDao.updateStatus(r.getId(), ReceiptStatus.CANCELED);

                int baseAmount = r.getAmountCents();
                int surcharge = baseAmount * REISSUE_SURCHARGE_PERCENT / 100;
                int newAmount = baseAmount + surcharge;
                String newConcept = r.getConcept() == null ? "Reliquidación" : r.getConcept() + " (reliquidación)";

                Receipt newReceipt = new Receipt(null,null,r.getUserId(),newAmount,today,r.getValueDate(),r.getChargeMonth(),newConcept,ReceiptStatus.REISSUED,null);

                receiptDao.insert(newReceipt);

                returnedCount++;
                reissuedCount++;
                reissuedTotalCents += newAmount;
            } else {
                receiptDao.updateStatus(r.getId(), ReceiptStatus.PAID);
                paidCount++;
            }
        }

        int totalProcessed = paidCount + returnedCount;
        double reissuedTotalEuros = reissuedTotalCents / 100.0;

        lastImportSummary =
                "Importación completada.\n" +
                "Recibos procesados: " + totalProcessed + "\n" +
                "Pagados: " + paidCount + "\n" +
                "Devueltos: " + returnedCount + "\n" +
                "Reliquidados: " + reissuedCount + "\n" +
                "Importe total reliquidado: " + String.format("%.2f", reissuedTotalEuros) + " €";

        batchDao.markProcessed(batchId);
    }

    private Set<String> readReturnedReceiptNumbers(Path csvPath) {
        Set<String> out = new HashSet<>();
        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {continue;}

                String[] parts = trimmed.split(",", -1);
                if (parts.length == 0) {continue;}

                String number = parts[0].trim();
                if (!number.isEmpty()) {out.add(number);}
            }
        } catch (IOException e) {
            throw new ApplicationException("No se pudo leer el fichero de devoluciones: " + e.getMessage());
        }
        return out;
    }
}
