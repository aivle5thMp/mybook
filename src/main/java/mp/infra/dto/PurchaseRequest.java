package mp.infra.dto;

import lombok.Data;

@Data
public class PurchaseRequest {
    private String userId;
    private String bookId;
    private int pointUsed;
    private boolean isSubscribed;

    // getter/setter
}


