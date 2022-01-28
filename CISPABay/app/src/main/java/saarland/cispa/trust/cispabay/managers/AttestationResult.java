package saarland.cispa.trust.cispabay.managers;

public class AttestationResult {
    public int getAttestationVersion() {
        return attestationVersion;
    }

    public void setAttestationVersion(int attestationVersion) {
        this.attestationVersion = attestationVersion;
    }

    public String getKeymasterSecurityLevel() {
        return keymasterSecurityLevel;
    }

    public void setKeymasterSecurityLevel(String keymasterSecurityLevel) {
        this.keymasterSecurityLevel = keymasterSecurityLevel;
    }

    public String getAttestationChallenge() {
        return attestationChallenge;
    }

    public void setAttestationChallenge(String attestationChallenge) {
        this.attestationChallenge = attestationChallenge;
    }

    public boolean isNoAuthRequired() {
        return noAuthRequired;
    }

    public void setNoAuthRequired(boolean noAuthRequired) {
        this.noAuthRequired = noAuthRequired;
    }

    public String getUserAuthType() {
        return userAuthType;
    }

    public void setUserAuthType(String userAuthType) {
        this.userAuthType = userAuthType;
    }

    private int attestationVersion;
    private String keymasterSecurityLevel;
    private String attestationChallenge;
    private boolean noAuthRequired;
    private String userAuthType;

    public AttestationResult() {}
}
