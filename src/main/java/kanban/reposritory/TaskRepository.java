package kanban.reposritory;

import kanban.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
        SELECT COUNT(t) > 0
        FROM Task t
        WHERE t.id <> :taskId
          AND t.startTime IS NOT NULL
          AND t.endTime IS NOT NULL
          AND :startTime < t.endTime
          AND :endTime > t.startTime
    """)
    boolean existsIntersection(Long taskId, LocalDateTime startTime, LocalDateTime endTime);
}
