package client;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Random;
import javax.crypto.Cipher;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import tools.HexTool;

public class LoginCrypto {

    private static final Logger log = Logger.getLogger(LoginCrypto.class);
    protected static final int extralength = 6;
    private static final String[] Alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private static final String[] Number = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static final Random rand = new Random();
    private static KeyFactory RSAKeyFactory;

    public static String Generate_13DigitAsiasoftPassport() {
        StringBuilder sb = new StringBuilder();
        sb.append(Alphabet[rand.nextInt(Alphabet.length)]);
        for (int i = 0; i < 11; i++) {
            sb.append(Number[rand.nextInt(Number.length)]);
        }
        sb.append(Alphabet[rand.nextInt(Alphabet.length)]);
        return sb.toString();
    }

    private static String toSimpleHexString(byte[] bytes) {
        return HexTool.toString(bytes).replace(" ", "").toLowerCase();
    }

    private static String hashWithDigest(String in, String digest) {
        try {
            MessageDigest Digester = MessageDigest.getInstance(digest);
            Digester.update(in.getBytes("UTF-8"), 0, in.length());
            byte[] sha1Hash = Digester.digest();
            return toSimpleHexString(sha1Hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Hashing the password failed", ex);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding the string failed", e);
        }
        
    }
    
    

    private static String hexSha1(String in) {
        return hashWithDigest(in, "SHA-1");
    }

    private static String hexSha512(String in) {
        return hashWithDigest(in, "SHA-512");
    }

    public static boolean checkSha1Hash(String hash, String password) {
        return hash.equals(hexSha1(password));
    }

    public static boolean checkSaltedSha512Hash(String hash, String password, String salt) {
        return hash.equals(makeSaltedSha512Hash(password, salt));
    }

    public static String makeSaltedSha512Hash(String password, String salt) {
        return hexSha512(new StringBuilder().append(password).append(salt).toString());
    }

    public static String makeSalt() {
        byte[] salt = new byte[16];
        rand.nextBytes(salt);
        return toSimpleHexString(salt);
    }

    public static String rand_s(String in) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(rand.nextBoolean() ? Alphabet[rand.nextInt(Alphabet.length)] : Number[rand.nextInt(Number.length)]);
        }
        return new StringBuilder().append(sb.toString()).append(in).toString();
    }

    public static String rand_r(String in) {
        return in.substring(6, 134);
    }

    public static String decryptRSA(String EncryptedPassword) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPPadding", "BC");
            BigInteger modulus = new BigInteger("107657795738756861764863218740655861479186575385923787150128619142132921674398952720882614694082036467689482295621654506166910217557126105160228025353603544726428541751588805629215516978192030682053419499436785335057001573080195806844351954026120773768050428451512387703488216884037312069441551935633523181351");
            BigInteger privateExponent = new BigInteger("5550691850424331841608142211646492148529402295329912519344562675759756203942720314385192411176941288498447604817211202470939921344057999440566557786743767752684118754789131428284047255370747277972770485804010629706937510833543525825792410474569027516467052693380162536113699974433283374142492196735301185337");
            RSAPrivateKeySpec privKey1 = new RSAPrivateKeySpec(modulus, privateExponent);
            PrivateKey privKey = RSAKeyFactory.generatePrivate(privKey1);

            byte[] bytes = Hex.decode(EncryptedPassword);
            cipher.init(2, privKey);
            return new String(cipher.doFinal(bytes));
        } catch (InvalidKeyException ike) {
            log.error("[LoginCrypto] Error initalizing the encryption cipher.  Make sure you're using the Unlimited Strength cryptography jar files.");
        } catch (NoSuchProviderException nspe) {
            log.error("[LoginCrypto] Security provider not found");
        } catch (Exception e) {
            log.error("[LoginCrypto] Error occured with RSA password decryption.");
        }
        return "";
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            RSAKeyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException nsa) {
            log.error("[LoginCrypto] Error occured with RSA KeyFactory");
        }
    }
}