package ips.club.model;

import java.time.LocalDateTime;

public class Incident {
    private Integer id;
    private int userId;
    private int incCode;
    private String description;
    private LocalDateTime createdAt;
    private IncidentStatus status;
    private Integer locationId;

    public Incident(Integer id, int userId, int incCode, String description, LocalDateTime createdAt, Integer locationId) {
        this.id = id;
        this.userId = userId;
        this.incCode = incCode;
        this.description = description;
        this.status = IncidentStatus.OPEN;
        this.createdAt = createdAt;
    }

    public Incident(Integer id, int userId, int incCode, String description, LocalDateTime createdAt, IncidentStatus status, Integer locationId) {
        this.id = id;
        this.userId = userId;
        this.incCode = incCode;
        this.description = description;
        this.createdAt = createdAt;
        this.status = status;
        this.locationId = locationId;
    }

    public Integer getId()                     { return id; }
    public void setId(Integer id)              { this.id = id; }

    public int getUserId()                   { return userId; }
    public void setUserId(int userId)      { this.userId = userId; }

    public int getIncCode()                    { return incCode; }
    public void setIncCode(int incCode)        { this.incCode = incCode; }

    public String getDescription()             { return description; }
    public void setDescription(String d)       { this.description = d; }

    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime c)  { this.createdAt = c; }

    public IncidentStatus getStatus()        { return status; }
    public void setStatus(IncidentStatus s)  { this.status = s; }

    public Integer getLocationId()  { return locationId; }
    public void setLocationId(Integer l)  { this.locationId = l; }

    @Override public String toString() { return "Incident{id="+getId()+", user="+getUserId()+", incCode="+getIncCode()+"}"; }
}
