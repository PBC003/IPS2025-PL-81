package ips.club.controller;

import java.util.List;

import ips.club.dao.IncidentTypeDao;
import ips.club.dto.IncidentDTO;
import ips.club.service.IncidentService;
import ips.util.ApplicationException;
import ips.club.model.Incident;
import ips.club.model.IncidentType;

public class IncidentsController {

    private final IncidentService service = new IncidentService();
    private final IncidentTypeDao dao = new IncidentTypeDao();

    public Incident createTicket(IncidentDTO dto) {
        if (dto == null) throw new ApplicationException("Datos de incidencia no válidos.");
    	dto.validate();
        return service.createTicket(dto.getUserId(), dto.getIncidentTypeCode(), dto.getText());
    }
    
    public List<IncidentType> loadIncidentTypes() {
        return dao.findAll();
    }
}
