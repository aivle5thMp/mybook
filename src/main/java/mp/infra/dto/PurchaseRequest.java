package mp.infra.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class PurchaseRequest {
    private UUID bookId;
    private Integer point;
    private String title;
    private String authorName;
    private String category;
    private String imageUrl;
}


