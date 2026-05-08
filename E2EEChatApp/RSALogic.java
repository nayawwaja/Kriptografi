import java.util.Random;

public class RSALogic {

    private static final String CHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final Random RNG = new Random();

    /**
     * Returns a fake Base64-like ciphertext string
     * to simulate RSA-encrypted output in the UI.
     */
    public static String getDummyCipher() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 132; i++) {
            sb.append(CHARS.charAt(RNG.nextInt(CHARS.length())));
            if ((i + 1) % 22 == 0 && i < 131) sb.append('\n');
        }
        return sb.toString();
    }
}