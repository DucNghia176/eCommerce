package ecommerce.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USER_ACCOUNT", schema = "user_db")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserAcc {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_acc_seq")
    @SequenceGenerator(name = "user_acc_seq", sequenceName = "SEQ_USER_ACC_ID", allocationSize = 1)
    @Column(name = "USER_ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "GOOGLE_ID")
    private String googleId;

    @Column(name = "FACEBOOK_ID")
    private String facebookId;

    @Column(name = "USERNAME", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "EMAIL", length = 100, unique = true)
    private String email;

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

    @OneToOne(mappedBy = "userAcc", cascade = CascadeType.ALL)
    @JsonIgnore
    private UserInfo userInfo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();
}
