package ecommerce.userservice.entity;

import ecommerce.aipcommon.model.status.GenderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "USER_PROFILE", schema = "user_db")
@Data
public class UserInfo {

    @Id
    @Column(name = "USER_ID")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "USER_ID")
    private UserAcc userAcc;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "AVATAR")
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "GENDER", length = 10)
    private GenderStatus gender;

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "PHONE", length = 10)
    private String phone;
}

