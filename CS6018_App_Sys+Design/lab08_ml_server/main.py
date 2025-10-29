"""
FastAPI server for NSFW Image Detection using HuggingFace ViT model
Runs on port 8000
"""

from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.responses import JSONResponse
from PIL import Image
import io
import torch
from transformers import AutoModelForImageClassification, ViTImageProcessor

app = FastAPI()

# Load NSFW detection model
print("🔄 Loading NSFW Detection model...")
model = AutoModelForImageClassification.from_pretrained("Falconsai/nsfw_image_detection")
processor = ViTImageProcessor.from_pretrained('Falconsai/nsfw_image_detection')
print("✅ Model loaded successfully!")

# Use GPU if available
device = "cuda" if torch.cuda.is_available() else "cpu"
model.to(device)
print(f"🖥️  Using device: {device}")

# Class labels
LABELS = {0: "normal", 1: "nsfw"}


@app.get("/")
def root():
    """Health check endpoint"""
    return {
        "status": "ML Server Running",
        "model": "NSFW Image Detection (ViT)",
        "classes": ["normal", "nsfw"]
    }


@app.post("/analyze")
async def analyze_image(file: UploadFile = File(...)):
    """
    Analyze an image for NSFW content

    Args:
        file: Image file (JPEG/PNG)

    Returns:
        JSON with classification result and confidence
    """
    try:
        # Read and process image
        image_data = await file.read()
        img = Image.open(io.BytesIO(image_data)).convert("RGB")

        print(f"📸 Analyzing image: {file.filename}")

        # Process image
        with torch.no_grad():
            inputs = processor(images=img, return_tensors="pt").to(device)
            outputs = model(**inputs)
            logits = outputs.logits

        # Get predictions
        probabilities = torch.nn.functional.softmax(logits, dim=-1)
        predicted_class_idx = logits.argmax(-1).item()
        confidence = probabilities[0][predicted_class_idx].item()

        predicted_label = model.config.id2label[predicted_class_idx]

        # Get both class probabilities
        normal_prob = probabilities[0][0].item()
        nsfw_prob = probabilities[0][1].item()

        print(f"✅ Classification: {predicted_label} ({confidence:.2%} confidence)")

        return {
            "success": True,
            "classification": predicted_label,
            "confidence": round(confidence, 4),
            "probabilities": {
                "normal": round(normal_prob, 4),
                "nsfw": round(nsfw_prob, 4)
            },
            "is_safe": predicted_label == "normal",
            "model": "Falconsai/nsfw_image_detection"
        }

    except Exception as e:
        print(f"❌ Error: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Analysis failed: {str(e)}")


@app.post("/analyze-batch")
async def analyze_batch(files: list[UploadFile] = File(...)):
    """
    Analyze multiple images for NSFW content

    Args:
        files: List of image files

    Returns:
        JSON with results for each image
    """
    try:
        results = []

        for file in files:
            image_data = await file.read()
            img = Image.open(io.BytesIO(image_data)).convert("RGB")

            with torch.no_grad():
                inputs = processor(images=img, return_tensors="pt").to(device)
                outputs = model(**inputs)
                logits = outputs.logits

            probabilities = torch.nn.functional.softmax(logits, dim=-1)
            predicted_class_idx = logits.argmax(-1).item()
            confidence = probabilities[0][predicted_class_idx].item()
            predicted_label = model.config.id2label[predicted_class_idx]

            results.append({
                "filename": file.filename,
                "classification": predicted_label,
                "confidence": round(confidence, 4),
                "is_safe": predicted_label == "normal"
            })

        return {
            "success": True,
            "count": len(results),
            "results": results
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Batch analysis failed: {str(e)}")


if __name__ == "__main__":
    import uvicorn

    # Run on port 8000 (different from KTOR's 8080)
    uvicorn.run(app, host="0.0.0.0", port=8000)