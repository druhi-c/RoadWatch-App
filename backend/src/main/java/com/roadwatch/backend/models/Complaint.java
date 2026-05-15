package com.roadwatch.backend.models;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.roadwatch.backend.config.PointToJsonSerializer;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String imageUrl;

    // Geographic coordinates
    @Column(columnDefinition = "geometry(Point,4326)")
    @JsonSerialize(using = PointToJsonSerializer.class)
    private Point location;

    private LocalDateTime timestamp;

    private String severity; // e.g., HIGH, MEDIUM, LOW

    private String status; // e.g., PENDING, ASSIGNED, RESOLVED

    private String roadType; // e.g., NH, SH, MDR

    private String department; // e.g., Roads Authority, Civic Maintenance

    public Complaint() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Point getLocation() { return location; }
    public void setLocation(Point location) { this.location = location; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRoadType() { return roadType; }
    public void setRoadType(String roadType) { this.roadType = roadType; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
