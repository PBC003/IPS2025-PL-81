package ips.club.controller;

import ips.club.dto.IncidentDTO;
import ips.club.service.IncidentService;

public class IncidentsController {

    private final IncidentService service = new IncidentService();

    public void createTicket(IncidentDTO dto) {
        dto.validate();
        service.createTicket(dto.getUserId(), dto.getIncidentTypeCode(), dto.getText());
    }
}
