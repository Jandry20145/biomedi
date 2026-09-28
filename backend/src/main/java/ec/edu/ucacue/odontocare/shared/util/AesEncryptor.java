package ec.edu.ucacue.odontocare.shared.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Converter
public class AesEncryptor implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    // For deterministic encryption, we use ECB. Key must be exactly 16 bytes.
    private static final byte[] KEY = System.getenv("ENCRYPTION_KEY") != null && System.getenv("ENCRYPTION_KEY").length() >= 16 
        ? System.getenv("ENCRYPTION_KEY").substring(0, 16).getBytes() 
        : "OdontoCareSecret".getBytes();

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            Key key = new SecretKeySpec(KEY, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            Key key = new SecretKeySpec(KEY, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            // If data is not encrypted (e.g. existing unencrypted data), return it as is
            return dbData;
        }
    }
}
