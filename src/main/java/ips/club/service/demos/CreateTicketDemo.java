package ips.club.service.demos;

import ips.club.service.IncidentService;
import ips.club.model.Incident;
import ips.util.Database;

public class CreateTicketDemo {
    public static void main(String[] args) {
        Database db = new Database();
        db.createDatabase(true);

        IncidentService svc = new IncidentService();

        int userId = 1;
        int incCode = 1;
        String details = "Prueba";
        Incident created = svc.createTicket(userId, incCode, details);

        System.out.println("Incidencia creada: id=" + created.getId()
                + ", userId=" + created.getUserId()
                + ", incCode=" + created.getIncCode()
                + ", desc=\"" + created.getDescription() + "\""
                + ", createdAt=" + created.getCreatedAt());
    }
}
