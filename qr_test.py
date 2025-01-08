import requests

# URL to generate the QR code
url = 'https://api.qrserver.com/v1/create-qr-code/'

# Parameters for the QR code (custom URL or data to encode)
params = {
    'data': 'https://www.ynet.co.il/home/0,7340,L-8,00.html',  # The URL you want the QR code to link to
    'size': '200x200'  # Size of the QR code image (optional)
}

# Send the GET request to the API
response = requests.get(url, params=params)

# Check if the request was successful
if response.status_code == 200:
    # Save the response as an image file
    with open('qr_code.png', 'wb') as f:
        f.write(response.content)  # Write the binary content to the file
    print("QR Code saved successfully!")
else:
    print(f"Error: {response.status_code}")