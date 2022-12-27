package fr.redboxing.redbot.database.entities;

import fr.redboxing.redbot.config.GuildConfiguration;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "guilds_config")
public class GuildConfigEntity {
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
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private GuildConfiguration name;

    @Getter
    @Setter
    @Column(name = "value")
    private String value;
}
