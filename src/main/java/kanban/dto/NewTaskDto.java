package kanban.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewTaskDto {
    String name;
    String description;
    LocalDateTime startTime;
    Duration duration;
}
