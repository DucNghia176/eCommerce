package ecommerce.notificationservice.entity;

import ecommerce.notificationservice.status.NotificationStatus;
import ecommerce.notificationservice.status.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NOTIFICATION", schema = "notification_db")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
    @SequenceGenerator(name = "notification_seq", sequenceName = "SEQ_NOTIFICATION_ID", allocationSize = 1)
    Long notificationId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "CONTENT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 100)
    private NotificationStatus status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @Column(name = "ENTITY_ID")
    private Long entityId;

}
