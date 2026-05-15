package com.roadwatch.backend.controllers;

import com.roadwatch.backend.dto.AiAnalysisResponseDto;
import com.roadwatch.backend.models.Complaint;
import com.roadwatch.backend.repositories.ComplaintRepository;
import com.roadwatch.backend.services.AiServiceClient;
import com.roadwatch.backend.services.DecisionEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private AiServiceClient aiServiceClient;

    @Autowired
    private DecisionEngineService decisionEngineService;

    @GetMapping
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Complaint> createComplaint(
            @ModelAttribute Complaint complaint,
            @RequestParam("image") MultipartFile image) {
            
        try {
            // Determine endpoint based on some logic, defaulting to surface analysis
            String endpoint = "/analyze_surface"; 
            if (complaint.getDescription() != null && complaint.getDescription().toLowerCase().contains("divider")) {
                endpoint = "/analyze_infrastructure";
            }

            AiAnalysisResponseDto aiResponse = aiServiceClient.analyzeImage(image, endpoint);
            
            decisionEngineService.assignSeverityAndDepartment(complaint, aiResponse);
            
            complaint.setStatus("PENDING");
            complaint.setTimestamp(LocalDateTime.now());
            // Here you would typically upload the image to S3 or a local dir and set the URL
            complaint.setImageUrl("dummy_url_for_now");

            Complaint saved = complaintRepository.save(complaint);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
