// Java Card imports
import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.*;

public class SeedCardApplet extends Applet {

    // Instruction codes
    private static final byte STORE_ENCRYPTED_SEED = 0x01;
    private static final byte DECRYPT_SEED = 0x02;

    // Status words for errors
    private static final short SW_NO_DATA_STORED = 0x6A83;
    private static final short SW_BAD_LENGTH = 0x6700;

    // Storage for encrypted seed
    private byte[] encryptedSeedStorage;

    // Cipher for RSA decryption
    private Cipher rsaCipher;

    // RSA private key
    private RSAPrivateKey rsaPrivateKey;

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new SeedCardApplet().register();
    }

    private SeedCardApplet() {
        // Initialize private key, cipher, and storage
        KeyPair rsaKeyPair = new KeyPair(KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_2048);
        rsaKeyPair.genKeyPair();
        rsaPrivateKey = (RSAPrivateKey) rsaKeyPair.getPrivate();
        rsaCipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);
        encryptedSeedStorage = JCSystem.makeTransientByteArray((short) 256, JCSystem.CLEAR_ON_DESELECT);
    }

    public void process(APDU apdu) {
        if (selectingApplet()) return;

        byte[] buffer = apdu.getBuffer();

        switch (buffer[ISO7816.OFFSET_INS]) {
            case STORE_ENCRYPTED_SEED:
                short incomingLength = (short) (buffer[ISO7816.OFFSET_LC] & 0xFF);
                apdu.setIncomingAndReceive();
                Util.arrayCopyNonAtomic(buffer, ISO7816.OFFSET_CDATA, encryptedSeedStorage, (short) 0, incomingLength);
                break;

            case DECRYPT_SEED:
                if (encryptedSeedStorage == null || encryptedSeedStorage.length == 0) {
                    ISOException.throwIt(SW_NO_DATA_STORED);
                }
                rsaCipher.init(rsaPrivateKey, Cipher.MODE_DECRYPT);
                short decryptedLength = rsaCipher.doFinal(encryptedSeedStorage, (short) 0, (short) encryptedSeedStorage.length, buffer, (short) 0);
                apdu.setOutgoingAndSend((short) 0, decryptedLength);
                break;

            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }
}
