package com.github.nagyesta.abortmission.core.telemetry;

import java.time.LocalDateTime;

public final class FromTo {
    private LocalDateTime from;
    private LocalDateTime to;

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(final LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(final LocalDateTime to) {
        this.to = to;
    }
}
