package mp.domain;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;

//<<< EDA / CQRS
@Entity
@Table(name = "BookView_table")
@Data
public class BookView {

    @Id
    private Long id;

    private String userId;
    private String bookId;
    private String bookStatus;     // 예: "읽는 중", "완독", "열람 실패"

    private String title;
    private String category;
    private String summary;

    private String audioUrl;
    private String imageUrl;

    private Integer viewCount;

    private Date createdAt;        // 최초 등록 시각
    private Date lastViewedAt;     // 마지막 열람 시각

    private String authorId;
    private String authorName;
}


