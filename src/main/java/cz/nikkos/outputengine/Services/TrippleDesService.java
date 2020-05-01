package cz.nikkos.outputengine.Services;

import cz.nikkos.outputengine.ExceptionHandling.CustomException;
import cz.nikkos.outputengine.ExceptionHandling.ResponseError;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.spec.KeySpec;

@Service
public class TrippleDesService {

    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";

    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher cipher;
    byte[] arrayBytes;
    private final String myEncryptionKey = "StrcPrstSkrKrkAnebKrtPrdNaskrzKrb1234567890";
    private String myEncryptionScheme;
    SecretKey key;


    public TrippleDesService() throws Exception {
        myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
        ks = new DESedeKeySpec(arrayBytes);
        skf = SecretKeyFactory.getInstance(myEncryptionScheme);
        cipher = Cipher.getInstance(myEncryptionScheme);
        key = skf.generateSecret(ks);
    }


    public String encrypt(String unencryptedString) throws CustomException {
        String encryptedString;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = new byte[0];
            try {
                encryptedText = cipher.doFinal(plainText);
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                throw new CustomException(ResponseError.ErrorType.INVALID_ENCRYPTION, e.getClass().toString(), "there is some problem with padding encryption");

            }
            encryptedString = new String(Base64.encodeBase64(encryptedText));
        } catch (InvalidKeyException | UnsupportedEncodingException e) {
            throw new CustomException(ResponseError.ErrorType.INVALID_ENCRYPTION, e.getClass().toString(), "there is some problem with unsupported code base encryption");

        }
        return encryptedString;
    }

    public String decrypt(String encryptedString) throws CustomException {
        String decryptedText;
        try {
            try {
                cipher.init(Cipher.DECRYPT_MODE, key);
            } catch (InvalidKeyException e) {
                throw new CustomException(ResponseError.ErrorType.INVALID_DECRYPTION, e.getClass().toString(), "there is some problem with decryption key inicializing during decryption");

            }
            byte[] encryptedText = Base64.decodeBase64(encryptedString);
            byte[] plainText;
            try {
                plainText = cipher.doFinal(encryptedText);
            } catch (BadPaddingException e) {
                throw new CustomException(ResponseError.ErrorType.INVALID_DECRYPTION, e.getClass().toString(), "there is some problem with bad padding during decryption ");
            }
            decryptedText = new String(plainText);
        } catch (IllegalBlockSizeException e) {
            throw new CustomException(ResponseError.ErrorType.INVALID_DECRYPTION, e.getClass().toString(), "there is some problem with block size during decrypting");

        }
        return decryptedText;
    }
}
