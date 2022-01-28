package com.attestation.server;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.cert.Certificate;
import java.util.UUID;

import saarland.cispa.trust.cispabay.managers.AttestationResult;

public class AttestationServer {

    // CHALLENGE and NONCE should be unique for every session. However, for the sake of simplicity,
    // we fix them here.
    private static final String CHALLENGE = "this-is-a-challenge";
    private static final byte[] NONCE = UUID.randomUUID().toString().getBytes();

    public static String getChallenge() {
        return CHALLENGE;
    }

    public static byte[] getNonce() {
        return NONCE;
    }

    public static String authenticate(Payload payload, byte[] signature) {
        if (payload == null || signature == null)
            return null;

        // @TODO: Verify the payload using the public key impeded in payload
        // You might need to use KeyStoreManager.decodeCertificate to get the certificate.

        boolean isAuthenticated = payload.getUsername().equals("user") &&
                payload.getPassword().equals("password");

        if (isAuthenticated) {
            String base = payload.getUsername() + ":" + payload.getPassword();
            return android.util.Base64.encodeToString(
                    base.getBytes(UTF_8),
                    android.util.Base64.DEFAULT
            );
        }
        return null;
    }

    private static AttestationResult parseAttestationResponse(Certificate certificate) {
        // @TODO: IMPLEMENT ME (get inspired by ParsedAttestationRecord)
        return null;
    }
}
