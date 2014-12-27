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

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

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

    public static Date fromLocalDateTime(final LocalDateTime date) {
        return Date.from(date.toInstant(ZoneOffset.UTC));
    }
}
