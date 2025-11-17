package ips.club.model;

import java.time.LocalDateTime;

public class Assembly {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
    private AssemblyStatus status;
    private AssemblyType type;
    private String minutesText;

    public Assembly(Integer id,
                    String title,
                    String description,
                    LocalDateTime scheduledAt,
                    LocalDateTime createdAt,
                    AssemblyStatus status,
                    AssemblyType type,
                    String minutesText) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.scheduledAt = scheduledAt;
        this.createdAt = createdAt;
        this.status = status == null ? AssemblyStatus.SCHEDULED : status;
        this.type = type == null ? AssemblyType.ORDINARY : type;
        this.minutesText = minutesText;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public AssemblyStatus getStatus() { return status; }
    public void setStatus(AssemblyStatus status) { this.status = status; }

    public AssemblyType getType() { return type; }
    public void setType(AssemblyType type) { this.type = type; }

    public String getMinutesText() { return minutesText; }
    public void setMinutesText(String minutesText) { this.minutesText = minutesText; }

    @Override
    public String toString() {
        return "Assembly{id=" + id + ", title=" + title + ", status=" + status + ", type=" + type + "}";
    }
}
