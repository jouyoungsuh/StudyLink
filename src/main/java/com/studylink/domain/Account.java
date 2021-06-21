package com.studylink.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean emailVerified;

    private String emailVerificationToken;

    private LocalDateTime createdAt;

    private String bio;

    private String url;

    private String occupation;

    private String location;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;

    public void generateEmailVerificationToken() {
        this.emailVerificationToken = UUID.randomUUID().toString();
    }
}