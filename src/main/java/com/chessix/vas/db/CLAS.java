package com.chessix.vas.db;

import lombok.*;

import javax.persistence.*;

/**
 * A closed loop accounting system
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "T_CLAS")
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
