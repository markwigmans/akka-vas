package com.chessix.vas.db;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A closed loop accounting system
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "A_CLAS")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CLAS extends BaseModel {

    @Column(name = "EXTERNAL_ID", nullable = false, unique = true)
    @NonNull
    private String externalId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "NOSTRO_ACCOUNT")
    private Account nostroAccount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXCEPTION_ACCOUNT")
    private Account exceptionAccount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "OUTBOUND_ACCOUNT")
    private Account outboundAccount;
}
