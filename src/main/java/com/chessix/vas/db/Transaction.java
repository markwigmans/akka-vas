package com.chessix.vas.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Entity
@Table(name = "A_TRANSACTION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Transaction extends BaseModel {

    @ManyToOne(optional = false)
    @JoinColumn(name = "CLAS_ID", updatable = false, nullable = false)
    private CLAS clas;

    @ManyToOne(optional = false)
    @JoinColumn(name = "FROM_ACCOUNT_ID", updatable = false, nullable = false)
    private Account fromAccount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "TO_ACCOUNT_ID", updatable = false, nullable = false)
    private Account toAccount;

    @Column(name = "AMOUNT", nullable = false)
    private int amount;

    @Column(name = "DATE", nullable = false)
    private Date date;
}
