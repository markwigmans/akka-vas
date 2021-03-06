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

import lombok.*;

import javax.persistence.*;

/**
 * An account in Pure SVA has a single currency and is bound to exactly 1 bank
 * account
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "T_ACCOUNT", uniqueConstraints = @UniqueConstraint(columnNames = {"CLAS_ID", "EXTERNAL_ID"}))
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"clas"})
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
    @NonNull
    private Integer balance;
}
