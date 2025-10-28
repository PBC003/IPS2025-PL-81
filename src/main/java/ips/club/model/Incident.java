package ips.club.model;

import java.time.LocalDateTime;

/** Created incident (ticket) by a member. */
public class Incident {
    private Integer id;
    private int userId;
    private int incCode;
    private String description;
    private LocalDateTime createdAt;

    public Incident(Integer id, int userId, int incCode, String description, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.incCode = incCode;
        this.description = description;
        this.createdAt = createdAt;
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

    @Override public String toString() { return "Incident{id="+getId()+", user="+getUserId()+", incCode="+getIncCode()+"}"; }
}
