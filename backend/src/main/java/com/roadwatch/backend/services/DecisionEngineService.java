package com.roadwatch.backend.services;

import com.roadwatch.backend.dto.AiAnalysisResponseDto;
import com.roadwatch.backend.dto.DetectionResultDto;
import com.roadwatch.backend.models.Complaint;
import org.springframework.stereotype.Service;

@Service
public class DecisionEngineService {

    public void assignSeverityAndDepartment(Complaint complaint, AiAnalysisResponseDto aiResponse) {
        // Defaults
        complaint.setSeverity("LOW");
        complaint.setDepartment("Civic Maintenance");

        if (aiResponse == null || !aiResponse.isSuccess() || aiResponse.getDetections() == null || aiResponse.getDetections().isEmpty()) {
            return;
        }

        // Evaluate the primary detection (or just the first one for simplicity)
        DetectionResultDto primaryDetection = aiResponse.getDetections().get(0);
        String label = primaryDetection.getLabel() != null ? primaryDetection.getLabel().toLowerCase() : "";
        double confidence = primaryDetection.getConfidence();
        String roadType = complaint.getRoadType() != null ? complaint.getRoadType().toUpperCase() : "UNKNOWN";

        boolean isNationalHighway = "NH".equals(roadType);

        switch (label) {
            case "pothole":
                complaint.setDepartment("Roads Authority");
                if (isNationalHighway) {
                    complaint.setSeverity("HIGH");
                } else if (confidence > 0.85) {
                    complaint.setSeverity("HIGH");
                } else {
                    complaint.setSeverity("MEDIUM");
                }
                break;
                
            case "broken_divider":
                complaint.setDepartment("Civic Maintenance");
                if (isNationalHighway) {
                    complaint.setSeverity("HIGH");
                } else {
                    complaint.setSeverity("MEDIUM");
                }
                break;
                
            case "street_lighting":
                complaint.setDepartment("Street Lighting");
                if (isNationalHighway) {
                    complaint.setSeverity("HIGH");
                } else {
                    complaint.setSeverity("MEDIUM");
                }
                break;
                
            default:
                complaint.setSeverity("LOW");
                complaint.setDepartment("Civic Maintenance");
                break;
        }
    }
}
