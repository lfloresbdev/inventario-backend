package pe.albrugroup.inventario_service.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cifra/descifra transparentemente el campo contrasena de InventarioLogico
 * usando AES-256-GCM. Los valores cifrados se almacenan con el prefijo "ENC:"
 * para distinguirlos de filas previas en texto plano (compatibilidad hacia atrás).
 */
@Converter
@Component
public class SecretEncryptionConverter implements AttributeConverter<String, String> {

    private static final String PREFIX = "ENC:";
    private static final int GCM_TAG_BITS = 128;
    private static final int IV_BYTES = 12;

    @Value("${app.encrypt.secret}")
    private String rawSecret;

    private volatile SecretKey cachedKey;

    private SecretKey key() {
        if (cachedKey != null) return cachedKey;
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(rawSecret.getBytes(StandardCharsets.UTF_8));
            cachedKey = new SecretKeySpec(hash, "AES");
            return cachedKey;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo derivar la clave de cifrado", e);
        }
    }

    @Override
    public String convertToDatabaseColumn(String plaintext) {
        if (plaintext == null) return null;
        try {
            byte[] iv = new byte[IV_BYTES];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key(), new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            Base64.Encoder b64 = Base64.getEncoder();
            return PREFIX + b64.encodeToString(iv) + ":" + b64.encodeToString(ciphertext);
        } catch (Exception e) {
            throw new IllegalStateException("Error al cifrar el campo", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        // Filas anteriores en texto plano: se devuelven tal cual
        if (!dbValue.startsWith(PREFIX)) return dbValue;

        try {
            String encoded = dbValue.substring(PREFIX.length());
            String[] parts = encoded.split(":", 2);
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] ciphertext = Base64.getDecoder().decode(parts[1]);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key(), new GCMParameterSpec(GCM_TAG_BITS, iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Error al descifrar el campo", e);
        }
    }
}
