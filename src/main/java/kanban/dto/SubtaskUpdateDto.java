package kanban.dto;

import kanban.util.Status;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubtaskUpdateDto {
    String name;
    String description;
    Status status;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Long epicId;
}
