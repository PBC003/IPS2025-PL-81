package ips.club.service;

import ips.club.dao.ReceiptDao;
import ips.club.model.Receipt;
import ips.club.model.ReceiptStatus;
import ips.util.ApplicationException;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ReceiptService {

    private final ReceiptDao receiptDao = new ReceiptDao();

    public Receipt createMonthlyReceipt(
            int userId,
            int amountCents,
            LocalDate valueDate,
            String chargeMonth,
            String conceptOverride
    ) {
        if (userId <= 0) throw new ApplicationException("userId inválido.");
        if (amountCents <= 0) throw new ApplicationException("El importe debe ser positivo.");
        LocalDate issueDate = LocalDate.now();
        if (valueDate == null) valueDate = issueDate;

        if (chargeMonth == null || chargeMonth.trim().isEmpty()) {
            chargeMonth = toYYYYMM(valueDate);
        }
        String concept = (conceptOverride != null && !conceptOverride.trim().isEmpty())
                ? conceptOverride
                : defaultConceptFromMonth(chargeMonth);

        Receipt r = new Receipt(null, null, userId, amountCents, issueDate, valueDate, chargeMonth, concept, ReceiptStatus.GENERATED, null);

        return receiptDao.insert(r);
    }

    public String toYYYYMM(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    private String defaultConceptFromMonth(String chargeMonth) {
        if (chargeMonth == null || chargeMonth.length() != 6) {throw new ApplicationException("chargeMonth inválido: " + chargeMonth);}
        int month = Integer.parseInt(chargeMonth.substring(4, 6));
        Month m = Month.of(month);
        return "Cuota Club " + m.getDisplayName(java.time.format.TextStyle.FULL, new Locale("es", "ES"));
    }

    public List<Receipt> listByMonth(String chargeMonth) {
        if (chargeMonth == null || chargeMonth.length() != 6) {
            throw new ApplicationException("chargeMonth inválido: " + chargeMonth);
        }
        return receiptDao.listByMonth(chargeMonth);
    }
}
