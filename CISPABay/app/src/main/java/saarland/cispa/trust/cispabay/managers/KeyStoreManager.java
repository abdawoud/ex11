package saarland.cispa.trust.cispabay.managers;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import com.attestation.server.AttestationServer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;


import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class KeyStoreManager {

    private static KeyStoreManager instance = null;
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "cispabay-key";
    private Cipher cipher = null;
    KeyStore keystore = null;

    private AlgorithmParameterSpec specification = new KeyGenParameterSpec.Builder(KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT |
                    KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
            .setAttestationChallenge(AttestationServer.getChallenge().getBytes(StandardCharsets.UTF_8))
            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA1)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
            .setRandomizedEncryptionRequired(true)
            .build();

    public static KeyStoreManager getInstance() {
        if (instance == null) {
            instance = new KeyStoreManager();
        }
        return instance;
    }

    private KeyStoreManager() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA,
                    ANDROID_KEY_STORE
            );
            keyPairGenerator.initialize(specification);
            keyPairGenerator.generateKeyPair();

            keystore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keystore.load(null);

            cipher = Cipher.getInstance(TRANSFORMATION);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException |
                KeyStoreException | CertificateException | IOException |
                NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public PrivateKey getPrivateKey() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return (PrivateKey) keystore.getKey(KEY_ALIAS, null);
    }

    private RSAPublicKey getPublicKey() throws KeyStoreException {
        Certificate certificate = keystore.getCertificate(KEY_ALIAS);
        return (RSAPublicKey) certificate.getPublicKey();
    }

    public Certificate[] getCertificateChain() throws KeyStoreException {
        return keystore.getCertificateChain(KEY_ALIAS);
    }

    public String encrypt(String data) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
            byte[] encryptBytes = cipher.doFinal(data.getBytes());

            return android.util.Base64.encodeToString(encryptBytes, android.util.Base64.DEFAULT);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String encryptedData) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
            byte[] decryptedBytes = cipher.doFinal(
                    android.util.Base64.decode(encryptedData, android.util.Base64.DEFAULT)
            );
            return new String(decryptedBytes);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException |
                UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] sign(byte[] data) {
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(getPrivateKey());
            signature.update(data);

            return signature.sign();
        } catch (InvalidKeyException | UnrecoverableKeyException | KeyStoreException |
                NoSuchAlgorithmException | SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean verify(byte[] data, byte[] sig) {
        try {
            if (sig == null)
                return false;

            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(getPublicKey());
            signature.update(data);

            return signature.verify(sig);
        } catch (InvalidKeyException | KeyStoreException | NoSuchAlgorithmException |
                SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Certificate decodeCertificate(byte[] certificate) {
        InputStream in = new ByteArrayInputStream(certificate);
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return (Certificate) certFactory.generateCertificate(in);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] encodeCertificate(Certificate certificate) {
        try {
            return certificate.getEncoded();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}