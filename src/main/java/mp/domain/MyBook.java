package mp.domain;

import java.util.Date;
import java.util.UUID;
import javax.persistence.*;
import lombok.Data;
import mp.MybookApplication;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "MyBook_table", indexes = {
    @Index(name = "idx_mybook_userid", columnList = "userId"),
    @Index(name = "idx_mybook_bookid", columnList = "bookId")
})
@Data
//<<< DDD / Aggregate Root
public class MyBook {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID userId;

    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID bookId;

    @Column(nullable = false)
    private String title;        // 도서 제목

    @Column(nullable = false)
    private String authorName;   // 작가 이름

    @Column(nullable = false)
    private String category;     // 카테고리

    @Column(nullable = false, columnDefinition = "VARCHAR(2000)")
    private String imageUrl;     // 표지 이미지 URL

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(nullable = false)
    private Integer point = 0;     // 도서 구매에 사용한 포인트

    public MyBook() {
        this.createdAt = new Date();  // 생성 시 현재 시간으로 초기화
    }

    @PostPersist
    public void onPostPersist() {
        BookPurchased bookPurchased = new BookPurchased(this);
        bookPurchased.publishAfterCommit();
    }
    
    public static MyBookRepository repository() {
        return MybookApplication.applicationContext.getBean(MyBookRepository.class);
    }
}
//>>> DDD / Aggregate Root



