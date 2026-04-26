package kanban.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kanban.util.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Duration;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Task {

    long id;
    String name;
    String description;

    @Enumerated(EnumType.STRING)
    Status status;

    LocalDateTime startTime;
    Duration duration;
}
