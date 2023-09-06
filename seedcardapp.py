from smartcard.System import readers
from smartcard.util import toHexString
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives.asymmetric import padding

# Constants
APDU_CLA = 0x00
STORE_ENCRYPTED_SEED = 0x01
DECRYPT_SEED = 0x02

def send_apdu(connection, apdu):
    response, sw1, sw2 = connection.transmit(apdu)
    if sw1 != 0x90 or sw2 != 0x00:
        raise Exception(f"APDU failed with SW1={hex(sw1)} SW2={hex(sw2)}")
    return response

def store_encrypted_seed(connection, encrypted_seed):
    apdu = [APDU_CLA, STORE_ENCRYPTED_SEED, 0x00, 0x00, len(encrypted_seed)] + encrypted_seed
    send_apdu(connection, apdu)

def decrypt_seed(connection):
    apdu = [APDU_CLA, DECRYPT_SEED, 0x00, 0x00]
    response = send_apdu(connection, apdu)
    return bytes(response).decode('utf-8')

if __name__ == '__main__':
    # Assume the public key is loaded from the YubiKey
    public_key_pem = b"""-----BEGIN PUBLIC KEY-----
    ... (Omitted for brevity)
    -----END PUBLIC KEY-----"""

    public_key = serialization.load_pem_public_key(
        public_key_pem,
        backend=default_backend()
    )

    seed = input("Enter the seed phrase you want to encrypt and store: ")

    # Encrypt the seed with the public key
    encrypted_seed = public_key.encrypt(
        seed.encode('utf-8'),
        padding.OAEP(
            mgf=padding.MGF1(algorithm=hashes.SHA256()),
            algorithm=hashes.SHA256(),
            label=None
        )
    )

    # Connect to smart card
    r = readers()
    if not r:
        print("No smartcard reader detected.")
        exit()

    connection = r[0].createConnection()
    connection.connect()

    # Store encrypted seed on card
    store_encrypted_seed(connection, list(encrypted_seed))

    # Decrypt and print seed for demo purposes
    decrypted_seed = decrypt_seed(connection)
    print(f"Decrypted Seed: {decrypted_seed}")
