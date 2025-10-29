package ips.club.service;

import java.time.LocalDateTime;

import ips.club.dao.IncidentDao;
import ips.club.model.Incident;
import ips.util.ApplicationException;

public class IncidentService {

    private final IncidentDao dao = new IncidentDao();

    public Incident createTicket(int userId, int incCode, String details) {

        if (userId <= 0) throw new ApplicationException("userId inválido.");
        if(incCode < 0 ) throw new ApplicationException("Tipo de incidencia inválido.");
        if (details == null || details.isEmpty()) {throw new ApplicationException("Debes proporcionar el texto requerido para este tipo de incidencia.");}

        Incident inc = new Incident(null, userId, incCode,details, LocalDateTime.now());

        return dao.insert(inc);
    }
}
