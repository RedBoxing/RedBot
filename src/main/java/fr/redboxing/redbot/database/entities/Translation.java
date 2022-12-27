package fr.redboxing.redbot.database.entities;

import fr.redboxing.redbot.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "translations")
@AllArgsConstructor
@NoArgsConstructor
public class Translation {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "lang", nullable = false)
    private Language lang;

    @Getter
    @Setter
    @Column(name = "keyName", nullable = false)
    private String keyName;

    @Getter
    @Setter
    @Column(name = "value", nullable = false)
    private String value;
}
