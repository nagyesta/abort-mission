package com.github.nagyesta.abortmission.reporting.json;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TimeJson {
    private int hour;
    private int minute;
    private int second;
    private int nano;
}
