package fr.redboxing.redbot.database.entities;

import fr.redboxing.redbot.manager.GuildConfiguration;

import javax.persistence.*;

@Entity
@Table(name = "guilds_config")
public class GuildConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "guildId", nullable = false)
    private String guildId;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private GuildConfiguration name;

    @Column(name = "value")
    private String value;

    public void setName(GuildConfiguration name) {
        this.name = name;
    }

    public GuildConfiguration getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
