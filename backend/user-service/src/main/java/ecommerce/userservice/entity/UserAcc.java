package ecommerce.userservice.entity;

import ecommerce.aipcommon.model.status.RoleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_ACCOUNT", schema = "user_db")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAcc {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_acc_seq")
    @SequenceGenerator(name = "user_acc_seq", sequenceName = "SEQ_USER_ACC_ID", allocationSize = 1)
    @Column(name = "USER_ID")
    private Long id;

    @OneToOne(mappedBy = "userAcc", cascade = CascadeType.ALL)
    private UserInfo userInfo;

    @Column(name = "USERNAME", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    @Column(name = "EMAIL", length = 100, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", length = 20)
    private RoleStatus role;

    @Column(name = "IS_ACTIVE")
    private int isActive = 1;

    @Column(name = "IS_LOCK", nullable = false)
    private int isLock = 0;

    @CreationTimestamp
    @Column(name = "CREATE_AT", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATE_AT")
    private LocalDateTime updatedAt;

    @Column(name = "LAST_LOGIN")
    private LocalDateTime lastLogin;
}
