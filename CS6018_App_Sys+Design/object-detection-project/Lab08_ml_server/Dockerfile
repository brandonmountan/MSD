# Use Python 3.11 slim image as base
FROM python:3.11-slim

# Set working directory in container
WORKDIR /app

# Copy requirements file
COPY requirements.txt .

# Install Python dependencies
# --no-cache-dir reduces image size
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY main.py .

# Expose port 8000 (ML server port)
EXPOSE 8000

# Command to run the application
# Use 0.0.0.0 to accept connections from any network interface
CMD ["python", "main.py"]
