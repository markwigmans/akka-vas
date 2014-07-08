package com.chessix.vas.db;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The base for JPA entities.
 */
@SuppressWarnings("serial")
@MappedSuperclass
@Data
public abstract class BaseModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Version
    @Column(name = "version")
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private int version;
}
