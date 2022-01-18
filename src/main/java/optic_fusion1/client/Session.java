package optic_fusion1.client;

import java.security.PublicKey;
import java.util.UUID;

public class Session {
    private final UUID id;
    private final PublicKey serverPublicKey;
    private final byte[] hmacKey;

    public Session(String id, PublicKey serverPublicKey, byte[] hmacKey) {
        this.id = UUID.fromString(id);
        this.serverPublicKey = serverPublicKey;
        this.hmacKey = hmacKey;
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
}
