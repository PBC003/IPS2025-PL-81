package ips.club.dto;

import ips.club.model.IncidentStatus;
import ips.club.model.Location;
import ips.util.ApplicationException;

public class IncidentDTO {

    private int userId;
    private int incidentTypeCode;
    private String text;
    private IncidentStatus status;
    private Integer locationId;

    public IncidentDTO(int userId, int incidentTypeCode, String text, Location location) {
        this.userId = userId;
        this.incidentTypeCode = incidentTypeCode;
        setText(text);
        status = IncidentStatus.OPEN;
        if(location != null)this.locationId = location.getId();
        validate();
    }

    public IncidentDTO(int userId, int incidentTypeCode, String text, IncidentStatus status, Location location) {
        this.userId = userId;
        this.incidentTypeCode = incidentTypeCode;
        setText(text);
        this.status = status;
        if(location != null)this.locationId = location.getId();
        validate();
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getIncidentTypeCode() { return incidentTypeCode; }
    public void setIncidentTypeCode(int incidentTypeCode) { this.incidentTypeCode = incidentTypeCode; }

    public String getText() { return text; }
    public void setText(String text) {this.text = (text == null) ? null : text.trim();}

    public IncidentStatus getStatus() { return status; }
    public void setIncidentStatus(IncidentStatus status) { this.status = status; }

    public Integer getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }

    public void validate() throws IllegalArgumentException {
        if (userId <= 0) {throw new ApplicationException("El id de socio no es valido.");}
        if (incidentTypeCode <= 0) { throw new ApplicationException("La incidencia selecionada no es valida");}
        if (text == null || text.isEmpty()) {throw new ApplicationException("La descripcion no puede estar vacía.");}
        if (status == null) {throw new ApplicationException("El estatus no puede estar vacío.");}
    }

    @Override
    public String toString() {
        return "IncidentDTO{userId=" + userId + ", incidentTypeCode=" + incidentTypeCode +", text='" + text +", satus='" + status + "'}";
    }
}
