import requests

file_path = 'test.jpg'

# Send the file using a POST request with multipart/form-data
with open(file_path, 'rb') as file:
    files = {'file': file}  # 'file' must match the $_FILES['file'] in PHP
    response = requests.post("http://10.0.0.142/upload.php", files=files)

if response.status_code == 201:
    print("File uploaded successfully!")
else:
    print(f"Failed to upload file. Status code: {response.status_code}")
    print(response.text)