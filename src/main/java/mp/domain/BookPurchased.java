package mp.domain;

import java.util.Date;
import lombok.*;
import mp.domain.MyBook;
import mp.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class BookPurchased extends AbstractEvent {

    private Long id;
    private String userId;
    private String bookId;
    private Integer point;
    private Date purchaseDate;

    public BookPurchased(MyBook aggregate) {
        super(aggregate);
        this.id = aggregate.getId();
        this.userId = aggregate.getUserId();
        this.bookId = aggregate.getBookId();
        this.point = aggregate.getUsedPoints();
        this.purchaseDate = aggregate.getCreatedAt();
    }

    public BookPurchased() {
        super();
    }
}
//>>> DDD / Domain Event


