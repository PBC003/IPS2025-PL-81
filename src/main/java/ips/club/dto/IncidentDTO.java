package ips.club.dto;

public class IncidentDTO {

    private int userId;
    private int incidentTypeCode;
    private String text;

    public IncidentDTO(int userId, int incidentTypeCode, String text) {
        this.userId = userId;
        this.incidentTypeCode = incidentTypeCode;
        setText(text);
        validate();
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getIncidentTypeCode() { return incidentTypeCode; }
    public void setIncidentTypeCode(int incidentTypeCode) { this.incidentTypeCode = incidentTypeCode; }

    public String getText() { return text; }
    public void setText(String text) {this.text = (text == null) ? null : text.trim();}

    public void validate() throws IllegalArgumentException {
        if (userId <= 0) {throw new IllegalArgumentException("El id de socio no es valido.");}
        if (incidentTypeCode <= 0) { throw new IllegalArgumentException("La incidencia selecionada no es valida");}
        if (text == null || text.isEmpty()) {throw new IllegalArgumentException("La descripcion no puede estar vacía.");}
    }

    @Override
    public String toString() {
        return "IncidentDTO{userId=" + userId + ", incidentTypeCode=" + incidentTypeCode +", text='" + text + "'}";
    }
}
