package kanban.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Epic extends Task {

    ArrayList<Integer> subtasksId;
    LocalDateTime endTime;

    public void addSubtask(int subtaskId) {
        if (this.getId() == subtaskId) {
            return;
        }
        subtasksId.add(subtaskId);
    }
}
