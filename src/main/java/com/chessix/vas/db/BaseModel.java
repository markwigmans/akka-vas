package com.chessix.vas.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
