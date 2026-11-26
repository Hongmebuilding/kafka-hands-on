import socket

request_hex = "000000230012674a4f74d28b00096b61666b612d636c69000a6b61666b612d636c6904302e3100"
request_bytes = bytes.fromhex(request_hex)

print("connecting...")
s = socket.create_connection(("localhost", 9092), timeout=5)
print("connected, sending...")
s.sendall(request_bytes)
print("sent, waiting for response...")
s.settimeout(5)
try:
    response = s.recv(1024)
    print("response:", response.hex())
except socket.timeout:
    print("TIMEOUT: no response received")
s.close()
