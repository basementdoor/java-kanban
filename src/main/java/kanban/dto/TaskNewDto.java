package kanban.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskNewDto {
    String name;
    String description;
    LocalDateTime startTime;
    LocalDateTime endTime;
}
