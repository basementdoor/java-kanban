package kanban.reposritory;

import kanban.model.Epic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpicRepository extends JpaRepository<Epic, Long> {
}
