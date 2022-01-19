package optic_fusion1.client;

import java.security.PublicKey;
import java.util.UUID;

public class Session {
    private final UUID id;
    private final PublicKey serverPublicKey;
    private final byte[] hmacKey;
    private final boolean authRequired;

    public Session(String id, PublicKey serverPublicKey, byte[] hmacKey, boolean authRequired) {
        this.id = UUID.fromString(id);
        this.serverPublicKey = serverPublicKey;
        this.hmacKey = hmacKey;
        this.authRequired = authRequired;
    }

    public PublicKey getServerPublicKey() {
        return serverPublicKey;
    }

    public byte[] getHmacKey() {
        return hmacKey;
    }

    public UUID getId() {
        return id;
    }

    public boolean isAuthRequired() {
        return authRequired;
    }
}
