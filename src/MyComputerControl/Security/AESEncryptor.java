package MyComputerControl.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AESEncryptor {
    public static String encrypt(String encodedSecretKey, String valueToBeEncrypted)
    {
        try {
            byte[] secretKeyBytes = Base64.decodeBase64(encodedSecretKey);
            SecretKeySpec mySecretKeySpec = new SecretKeySpec(secretKeyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            IvParameterSpec ivParameterSpec = new IvParameterSpec("ComputerControlV".getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, mySecretKeySpec, ivParameterSpec);

            byte[] encryptedValue = cipher.doFinal(valueToBeEncrypted.getBytes());

            return Base64.encodeBase64String(encryptedValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encodedSecretKey, String encryptedValue)
    {
        try {
            byte[] secretKeyBytes = Base64.decodeBase64(encodedSecretKey);
            SecretKeySpec mySecretKeySpec = new SecretKeySpec(secretKeyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            IvParameterSpec ivParameterSpec = new IvParameterSpec("ComputerControlV".getBytes());
            cipher.init(Cipher.DECRYPT_MODE, mySecretKeySpec, ivParameterSpec);

            byte[] decryptedValue = cipher.doFinal(Base64.decodeBase64(encryptedValue));

            return new String(decryptedValue);
        }catch(BadPaddingException e) {
            return "wrong\tkey";
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
