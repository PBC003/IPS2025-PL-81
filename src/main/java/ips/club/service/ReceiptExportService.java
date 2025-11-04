package ips.club.service;

import ips.club.dao.ReceiptBatchDao;
import ips.club.dao.ReceiptDao;
import ips.club.dao.UserDao;
import ips.club.model.Receipt;
import ips.club.model.ReceiptBatch;
import ips.club.model.User;
import ips.util.ApplicationException;

import java.io.BufferedWriter;
import java.nio.file.*;

import java.util.List;

public class ReceiptExportService {

    private static final Path EXPORT_DIR = Paths.get("exports");

    private final ReceiptDao receiptDao = new ReceiptDao();
    private final ReceiptBatchDao batchDao = new ReceiptBatchDao();
    private final UserDao userDao = new UserDao();

    public Path exportBatchToCsvUsingBatchFileName(int batchId) {
        if (batchId <= 0) throw new ApplicationException("batchId inválido.");

        ReceiptBatch batch = batchDao.findById(batchId);
        if (batch == null) throw new ApplicationException("Lote no encontrado: " + batchId);

        String fileName = batch.getFileName();
        if (fileName == null || fileName.trim().isEmpty()) {throw new ApplicationException("El lote " + batchId + " no tiene 'file_name'. Asigna uno antes de exportar.");}

        try { Files.createDirectories(EXPORT_DIR); }
        catch (Exception e) { throw new ApplicationException("No se pudo crear la carpeta de exportación: " + EXPORT_DIR); }

        Path csvPath = EXPORT_DIR.resolve(fileName);

        List<Receipt> items = receiptDao.listByBatch(batchId);
        if (items.isEmpty()) throw new ApplicationException("El lote " + batchId + " no tiene recibos.");

        try (BufferedWriter w = Files.newBufferedWriter(csvPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            w.write("receipt_number,member_name,member_surname,member_iban,amount_cents,value_date,issue_date,charge_month,concept");
            w.newLine();

            for (Receipt r : items) {
                User u = userDao.findBasicById(r.getUserId());

                if (u.getIban() == null || u.getIban().trim().isEmpty()) {throw new ApplicationException("El socio id=" + u.getId() + " no tiene IBAN; no se puede exportar el lote.");}

                String concept = r.getConcept().replace("\"", "\"\"");
                String line = String.join(",",
                        safe(r.getReceiptNumber()),
                        safe(u.getName()),
                        safe(u.getSurname()),
                        safe(u.getIban()),
                        Integer.toString(r.getAmountCents()),
                        r.getValueDate().toString(),
                        r.getIssueDate().toString(),
                        r.getChargeMonth(),
                        "\"" + concept + "\""
                );

                w.write(line);
                w.newLine();
            }
        } catch (Exception e) {
            throw new ApplicationException("No se pudo escribir el CSV en " + csvPath);
        }

        batchDao.updateTotalsAndMarkExported(batchId, fileName);

        return csvPath;
    }

    private String safe(String s) { return s == null ? "" : s; }
}
