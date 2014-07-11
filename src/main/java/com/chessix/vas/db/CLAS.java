package com.chessix.vas.db;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
}
