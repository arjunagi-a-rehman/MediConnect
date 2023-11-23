package com.Arjunagi.DoctorApp.models.authTokens;

import com.Arjunagi.DoctorApp.models.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class DefaultAuthToken implements IAuthToken{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String value;
    private LocalDateTime creationDateTime;
    private LocalDateTime sessionEndDateTime;
    @OneToOne
    @JoinColumn(name = "fkUserId")
    private User user;
    public DefaultAuthToken(User user){
        this.value= UUID.randomUUID().toString();
        this.user=user;
        creationDateTime=LocalDateTime.now();
        sessionEndDateTime=creationDateTime.plusDays(2);
    }

}
