package com.chessix.vas.db;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * An account in Pure SVA has a single currency and is bound to exactly 1 bank
 * account
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "T_ACCOUNT", uniqueConstraints = @UniqueConstraint(columnNames = { "CLAS_ID", "EXTERNAL_ID" }))
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "clas" })
@ToString(exclude = "clas")
public class Account extends BaseModel {

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "CLAS_ID", updatable = false, nullable = false)
    @NonNull
    private CLAS clas;

    @Column(name = "EXTERNAL_ID", nullable = false)
    @NonNull
    private String externalId;

    @Column(name = "BALANCE", nullable = false)
    private long balance = 0L;
}
