FROM python:3.11-slim

WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY main.py .

# Download NSFW detection model at build time (caches it in the image)
RUN python -c "from transformers import AutoModelForImageClassification, ViTImageProcessor; \
    AutoModelForImageClassification.from_pretrained('Falconsai/nsfw_image_detection'); \
    ViTImageProcessor.from_pretrained('Falconsai/nsfw_image_detection')"

# Expose port
EXPOSE 8000

# Run the application
CMD ["python", "main.py"]