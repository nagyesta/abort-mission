// noinspection TypeScriptUMDGlobal

const ko = require('knockout');

const formatDuration = function(duration, secondPrecision) {
    let result = '';
    if (duration === null) {
        result = "N/A";
    } else {
        const hasMinutes = duration >= 60000;
        if (hasMinutes) {
            result = (duration / 60000).toFixed(0) + "<small>m</small>";
            duration = duration % 60000;
        }
        const hasSeconds = duration >= 1000 || hasMinutes;
        if (hasSeconds) {
            const fractionalDigits = secondPrecision || (hasMinutes ? 0 : 1);
            result += (duration / 1000).toFixed(fractionalDigits) + "<small>s</small>";
        } else {
            result += duration.toFixed(0) + "<small>ms</small>";
        }
    }
    return result;
}
class StatModel {
    constructor(rootModel, filter) {
        this.rootModel = rootModel;
        this.filter = filter;
        this.runs = 0;
        this.start = null;
        this.end = null;
        this.min = null;
        this.max = null;
        this.sum = 0;
        this.success = 0;
        this.failure = 0;
        this.aborted = 0;
        this.suppressed = 0;
        this.matcherKeys = [];
        this.matchers = [];
    }

    merge(other) {
        if (other.runs <= 0) {
            return;
        }
        this.runs += other.runs;
        this.start = this.start === null ? other.start : Math.min(other.start, this.start);
        this.end = this.end === null ? other.end : Math.max(other.end, this.end);
        this.min = this.min === null ? other.min : Math.min(this.min, other.min);
        this.max = this.max === null ? other.max : Math.max(this.max, other.max);
        this.sum += other.sum;
        this.success += other.success;
        this.failure += other.failure;
        this.aborted += other.aborted;
        this.suppressed += other.suppressed;
    }

    startTimeAsString() {
        return this.start === null ? 'N/A' : this.rootModel.filter.formatDateAsString(new Date(this.start));
    }

    canFilterStartTime() {
        return this.start !== null && this.rootModel.filter.startTime().getTime() !== this.start;
    }

    filterStartTime() {
        return this.rootModel.filter.setStartTime(new Date(this.start));
    }

    endTimeAsString() {
        return this.end === null ? 'N/A' : this.rootModel.filter.formatDateAsString(new Date(this.end));
    }

    canFilterEndTime() {
        return this.end !== null && this.rootModel.filter.endTime().getTime() !== this.end;
    }

    filterEndTime() {
        return this.rootModel.filter.setEndTime(new Date(this.end));
    }

    average() {
        return formatDuration(this.runs === 0 ? null : this.sum / this.runs);
    }

    minimum() {
        return formatDuration(this.min, 0);
    }

    maximum() {
        return formatDuration(this.max, 0);
    }

    totalSum() {
        return formatDuration(this.sum, 0);
    }

    visible() {
        return this.runs > 0;
    }

    worstResult() {
        let result;
        if (this.failure > 0) {
            result = "failure";
        } else if (this.aborted > 0) {
            result = "aborted";
        } else if (this.suppressed > 0) {
            result = "suppressed";
        } else if (this.success > 0) {
            result = "success";
        } else {
            result = "empty";
        }
        return result;
    }

    addRun(run) {
        if (this.filter(run)) {
            return;
        }
        this.runs += 1;
        this.start = this.start === null ? run.start.getTime() : Math.min(this.start, run.start.getTime());
        this.end = this.end === null ? run.end.getTime() : Math.max(this.end, run.end.getTime());
        const duration = run.end - run.start;
        this.min = this.min === null ? duration : Math.min(this.min, duration);
        this.max = this.max === null ? duration : Math.max(this.max, duration);
        this.sum += duration;
        const matcherEntries = this.rootModel.matchers;
        for (const item of run.matcherKeys) {
            if (!this.matcherKeys.includes(item)) {
                this.matcherKeys.push(item);
                this.matchers.push(matcherEntries.find(m => m.key === item));
            }
        }
        if (run.isSuccess()) {
            this.success++;
        } else if (run.isFailure()) {
            this.failure++;
        } else if (run.isAborted()) {
            this.aborted++;
        } else if (run.isSuppressed()) {
            this.suppressed++;
        }
    }
}

module.exports.formatDuration = formatDuration;
module.exports.StatModel = StatModel;
