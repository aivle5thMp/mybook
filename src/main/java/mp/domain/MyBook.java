package mp.domain;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import mp.MybookApplication;

@Entity
@Table(name = "MyBook_table")
@Data
//<<< DDD / Aggregate Root
public class MyBook {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String userId;

    private String bookId;

    private Date createdAt;

    private Integer usedPoints;     // 도서 구매에 사용한 포인트

    private String type;            // "ebook", "audiobook" 등

    @PostPersist
    public void onPostPersist() {
        // ✅ 도서 구매 이벤트만 발행
        BookPurchased bookPurchased = new BookPurchased(this);
        bookPurchased.publishAfterCommit();
    }

    public static MyBookRepository repository() {
        return MybookApplication.applicationContext.getBean(MyBookRepository.class);
    }
}
//>>> DDD / Aggregate Root



