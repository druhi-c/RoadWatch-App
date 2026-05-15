from fastapi import FastAPI, File, UploadFile
from pydantic import BaseModel
from typing import List

app = FastAPI(title="RoadWatch AI Microservice")

class BoundingBox(BaseModel):
    xMin: float
    yMin: float
    xMax: float
    yMax: float

class DetectionResult(BaseModel):
    label: str # "pothole", "broken_divider", "street_lighting"
    confidence: float
    bbox: BoundingBox

class AnalysisResponse(BaseModel):
    success: bool
    detections: List[DetectionResult]

@app.post("/analyze_surface", response_model=AnalysisResponse)
async def analyze_surface(image: UploadFile = File(...)):
    # Mocked YOLOv8 Model 1 response for surface defects
    return AnalysisResponse(
        success=True,
        detections=[
            DetectionResult(
                label="pothole",
                confidence=0.89,
                bbox=BoundingBox(xMin=10.5, yMin=20.0, xMax=50.5, yMax=60.0)
            )
        ]
    )

@app.post("/analyze_infrastructure", response_model=AnalysisResponse)
async def analyze_infrastructure(image: UploadFile = File(...)):
    # Mocked YOLOv8 Model 2 response for infrastructure damage
    return AnalysisResponse(
        success=True,
        detections=[
            DetectionResult(
                label="broken_divider",
                confidence=0.92,
                bbox=BoundingBox(xMin=100.0, yMin=200.0, xMax=150.0, yMax=250.0)
            )
        ]
    )

@app.get("/health")
def health_check():
    return {"status": "healthy"}
