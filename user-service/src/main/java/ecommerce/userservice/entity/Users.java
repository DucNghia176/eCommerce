package ecommerce.userservice.entity;

import ecommerce.aipcommon.model.status.GenderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "USERS", schema = "user_db")
@Data
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "SEQ_USER_ID", allocationSize = 1)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "USERNAME", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    @Column(name = "FULL_NAME", length = 100)
    private String fullName;

    @Column(name = "EMAIL", length = 100, unique = true)
    private String email;

    @Column(name = "ROLE", length = 20)
    private String role;

    @Column(name = "AVATAR", length = 255)
    private String avatar;

    @Column(name = "IS_ACTIVE")
    private Integer isActive = 1;

    @Column(name = "IS_LOCK", nullable = false)
    private Integer isLock = 0;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "LAST_LOGIN")
    private LocalDateTime lastLogin;

    @Enumerated(EnumType.STRING)
    @Column(name = "GENDER", length = 10)
    private GenderStatus gender;

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;
}

