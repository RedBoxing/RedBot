package fr.redboxing.redbot.database.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ai_history")
@AllArgsConstructor
@NoArgsConstructor
public class AIEntry {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Getter
    @Setter
    @Column(name = "guildId", nullable = false)
    private String guildId;

    @Getter
    @Setter
    @Column(name = "authorId", nullable = false)
    private String authorId;

    @Getter
    @Setter
    @Column(name = "request", nullable = false)
    private String request;

    @Getter
    @Setter
    @Column(name = "response", nullable = false)
    private String response;
}
