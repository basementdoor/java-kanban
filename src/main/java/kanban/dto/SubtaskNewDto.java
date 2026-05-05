package kanban.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubtaskNewDto {
    String name;
    String description;
    LocalDateTime startTime;
    LocalDateTime endTime;

    @NotNull
    Long epicId;
}
