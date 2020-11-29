package com.github.nagyesta.abortmission.reporting.json;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocalDateTimeJson {
    private TimeJson time;
    private DateJson date;
}
