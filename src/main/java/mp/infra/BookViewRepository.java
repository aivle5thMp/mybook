package mp.infra;

import java.util.List;
import mp.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "bookViews", path = "bookViews")
public interface BookViewRepository
    extends PagingAndSortingRepository<BookView, Long> {
    List<BookView> findByUserId(String userId);
}
