# Download the model
import kagglehub

# Download latest version
path = kagglehub.model_download("tensorflow/efficientdet/tfLite/lite4-detection-metadata")

print("Path to model files:", path)

# List the downloaded files to see what's available
import os
for file in os.listdir(path):
    print(f"Downloaded file: {file}")
    print(f"File size: {os.path.getsize(os.path.join(path, file)) / (1024*1024):.1f} MB")