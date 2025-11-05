package ips.club.controller;

import ips.club.model.Receipt;
import ips.club.model.User;
import ips.club.service.ReceiptService;
import ips.club.dao.ReceiptDao;
import ips.club.dao.UserDao;
import ips.util.ApplicationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReceiptsController {

    private final ReceiptService receiptService = new ReceiptService();
    private final ReceiptDao receiptDao = new ReceiptDao();
    private final UserDao userDao = new UserDao();

    public Receipt createMonthlyReceipt(int userId, int amountCents, LocalDate valueDate, String chargeMonth,
            String conceptOverride) throws ApplicationException {
        return receiptService.createMonthlyReceipt(userId, amountCents, valueDate, chargeMonth, conceptOverride);
    }

    public List<Receipt> listUnbatchedByMonth(String chargeMonth) throws ApplicationException {
        if (chargeMonth == null || chargeMonth.trim().isEmpty()) {
            throw new ApplicationException("Debes indicar el mes en formato YYYYMM.");
        }
        return receiptDao.listByMonthNotInBatch(chargeMonth);
    }

    public List<Receipt> listByMonth(String chargeMonth) {
        if (chargeMonth == null || chargeMonth.length() != 6) {
            throw new ApplicationException("chargeMonth inválido: " + chargeMonth);
        }
        return receiptService.listByMonth(chargeMonth);
    }

    public int generateAllMonthlyReceipts(String yyyymm) {
        List<Receipt> monthReceipts = this.listByMonth(yyyymm);
        List<User> allUsers = userDao.findAll();
        List<Integer> userDoneListId = new ArrayList<>();
        LocalDate valueDate = LocalDate.now().plusMonths(3);
        Integer counter = 0;

        for (Receipt r : monthReceipts) {userDoneListId.add(r.getUserId());}

        for (User r : allUsers) {
            if (!userDoneListId.contains(r.getId())) {
                this.createMonthlyReceipt(r.getId(), r.getMonthlyFeeCents(), valueDate, yyyymm, null);
                counter++;
            }
        }

        return counter;
    }
}
