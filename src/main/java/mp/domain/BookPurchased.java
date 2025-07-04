package mp.domain;

import java.util.UUID;
import lombok.Data;
import mp.infra.AbstractEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookPurchased extends AbstractEvent {
    private Long id;
    private UUID userId;
    private UUID bookId;
    private Integer point;
    private String title;
    private String authorName;
    private String category;
    private String imageUrl;

    public BookPurchased(MyBook aggregate) {
        super(aggregate);
    }

    public BookPurchased() {
        super();
    }
}


