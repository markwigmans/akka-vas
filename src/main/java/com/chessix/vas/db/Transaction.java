/******************************************************************************
 Copyright 2014 Mark Wigmans

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ******************************************************************************/
package com.chessix.vas.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@SuppressWarnings("serial")
@Entity
@Table(name = "T_TRANSACTION")
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
