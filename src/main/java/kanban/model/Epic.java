package kanban.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "epics")
public class Epic extends AbstractTask {

    ArrayList<Integer> subtasksId;
    LocalDateTime endTime;

    public void addSubtask(int subtaskId) {
        if (this.getId() == subtaskId) {
            return;
        }
        subtasksId.add(subtaskId);
    }
}
