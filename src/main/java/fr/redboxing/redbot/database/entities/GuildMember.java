package fr.redboxing.redbot.database.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "guilds_members")
@AllArgsConstructor
@NoArgsConstructor
public class GuildMember {
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
    @Column(name = "memberId", nullable = false)
    private String memberId;

    @Getter
    @Setter
    @Column(name = "experience", nullable = false)
    @ColumnDefault("0")
    private int experience;
}
