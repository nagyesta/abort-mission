// noinspection TypeScriptUMDGlobal
const {LogViewRow} = require('./log-view-time-stamp-row-model');

class LogViewTempCalculationHelper {
    constructor(parent) {
        this.parent = parent;
        this.threadsWithNoActivityDuringStarts = [];
        this.threadsWithNoActivityDuringEnds = [];
        this.threadsStarting = [];
        this.threadsEnding = [];
        this.threadsContinuing = [];
        this.runsStarting = [];
        this.rows = [];
        this.calculationDone = false;
        this.generationDone = false;
    }

    doForEachDefinedValue(list, converterFunction, rowSpanResult) {
        const result = rowSpanResult || [];
        for (let i = 0; i < list.length; i++) {
            if (list[i] !== undefined) {
                result[i] = converterFunction(list[i]);
            }
        }
        return result;
    }

    nextRunOnThreadBetweenIndexes(started, minIndexInclusive, maxIndexExclusive, threadName) {
        for (let anyIndex = minIndexInclusive; anyIndex < maxIndexExclusive; anyIndex++) {
            if (started[anyIndex].run.threadName === threadName) {
                return anyIndex;
            }
        }
        return -1;
    }

    findRowSpanForCurrentRowIfMultipleStartsAreOnSameThread(started, minIndexInclusive, threadName, currenStartRowIndex) {
        const nextRunIndex = this.nextRunOnThreadBetweenIndexes(started, minIndexInclusive, started.length, threadName)
        let result;
        if (nextRunIndex === -1) {
            result = 0;
        } else {
            result = this.findRowSpanForCurrentRowInStartingSection(started, nextRunIndex, currenStartRowIndex, minIndexInclusive, threadName);
        }
        return result;
    }

    findRowSpanForCurrentRowInStartingSection(started, nextRunIndex, currenStartRowIndex, minIndexInclusive, threadName) {
        let result;
        const nextRunOnThread = started[nextRunIndex];
        if (!nextRunOnThread.run.endsInSameMilliSecond()) {
            result = this.calculateRowSpanForStartingLongRun(started, nextRunIndex, currenStartRowIndex, minIndexInclusive);
        } else {
            result = this.calculateRowSpanForStartingThreadEndingInSameMillisecond(started, nextRunIndex, currenStartRowIndex, threadName);
        }
        return result;
    }

    calculateRowSpanForStartingThreadEndingInSameMillisecond(started, nextRunIndex, currenStartRowIndex, threadName) {
        let result;
        const nextRunOnThread = started[nextRunIndex];
        if (nextRunIndex === currenStartRowIndex) {
            nextRunOnThread.startsAt = currenStartRowIndex;
            result = 1;
        } else if (nextRunIndex > currenStartRowIndex) {
            result = 0;
        } else {
            result = this.findRowSpanForCurrentRowIfMultipleStartsAreOnSameThread(started, nextRunIndex + 1, threadName, currenStartRowIndex);
        }
        return result;
    }

    calculateRowSpanForStartingLongRun(started, nextRunIndex, currenStartRowIndex, minIndexInclusive) {
        const nextRunOnThread = started[nextRunIndex];
        if (currenStartRowIndex === minIndexInclusive) {
            nextRunOnThread.startsAt = currenStartRowIndex;
            return nextRunOnThread.run.rowSpan;
        } else {
            return -1;
        }
    }

    calculateRowSpans() {
        if (!this.calculationDone) {
            this.calculateRowSpansForEndingRuns();
            this.calculateRowSpansForStartingRuns();
            this.calculationDone = true;
        }
    }

    // noinspection JSUnusedLocalSymbols
    calculateRowSpansForStartingRuns() {
        const self = this;
        const started = this.parent.started;
        const ended = this.parent.ended;
        for (let startIndex = 0; startIndex < started.length; startIndex++) {
            const logViewRun = started[startIndex];
            const absoluteIndex = ended.length + startIndex;
            const result = this.doForEachDefinedValue(this.threadsWithNoActivityDuringStarts, () => 0);
            this.doForEachDefinedValue(this.threadsContinuing, () => -1, result);
            this.doForEachDefinedValue(this.threadsStarting, this.populateValuesForStartingThreads(started, startIndex), result);
            logViewRun.rowSpan = result;
        }
    }

    calculateRowSpansForEndingRuns() {
        const ended = this.parent.ended;
        for (const logViewRun of ended) {
            const result = this.doForEachDefinedValue(this.threadsWithNoActivityDuringEnds, () => 0);
            this.doForEachDefinedValue(this.threadsEnding, () => -1, result);
            this.doForEachDefinedValue(this.threadsContinuing, () => -1, result);
            logViewRun.rowSpan = result;
        }
    }

    populateValuesForStartingThreads(started, startIndex) {
        const self = this;
        return (threadName) => {
            const firstRunIndex = self.nextRunOnThreadBetweenIndexes(started, 0, started.length, threadName)
            if (firstRunIndex === -1) {
                console.error('ERROR: ' + self.parent.timeStampAsDate.toISOString() + ': found missing "start" entry on thread ' + threadName + ' at row ' + absoluteIndex);
            } else {
                return self.rowSpanForStartingRunOnThread(started, startIndex, firstRunIndex, threadName);
            }
        };
    }

    rowSpanForStartingRunOnThread(started, startIndex, firstRunIndex, threadName) {
        const firstRunOnThread = started[firstRunIndex];
        let rowSpan;
        if (!firstRunOnThread.run.endsInSameMilliSecond()) {
            rowSpan = this.rowSpanForStartingLongRunOnThread(startIndex, firstRunOnThread);
        } else {
            rowSpan = this.rowSpanForStartingShortRunOnThread(started, startIndex, firstRunIndex, threadName);
        }
        return rowSpan;
    }

    rowSpanForStartingShortRunOnThread(started, startIndex, firstRunIndex, threadName) {
        const firstRunOnThread = started[firstRunIndex];
        if (firstRunIndex === startIndex) {
            firstRunOnThread.startsAt = startIndex;
            return 1;
        } else if (firstRunIndex > startIndex) {
            return 0;
        } else {
            return this.findRowSpanForCurrentRowIfMultipleStartsAreOnSameThread(started, firstRunIndex + 1, threadName, startIndex);
        }
    }

    rowSpanForStartingLongRunOnThread(startIndex, firstRunOnThread) {
        if (startIndex === 0) {
            firstRunOnThread.startsAt = startIndex;
            return firstRunOnThread.run.rowSpan;
        } else {
            return -1;
        }
    }

    generateRows() {
        if (!this.calculationDone) {
            throw new Error('ERROR: calculation must be done before row generation');
        }
        if (this.generationDone) {
            return this.rows;
        }
        let time = this.parent.timeStampAsDate;
        let timeStampRowSpan = this.parent.started.length + this.parent.ended.length;
        const hadAny = this.generateRowsForProcessesThatAreEnding(this.parent.ended, time, timeStampRowSpan);
        if (hadAny) {
            //the timestamp is already taken care of if there were ended processes
            time = null;
            timeStampRowSpan = null;
        }
        this.generateRowsForProcessesThatAreStarting(this.parent.started, time, timeStampRowSpan);
        return this.rows;
    }

    generateRowsForProcessesThatAreStarting(started, time, timeStampRowSpan) {
        let t = time;
        let ts = timeStampRowSpan;
        for (let i = 0; i < started.length; i++) {
            const runWithDetails = started[i];
            const row = new LogViewRow(this.parent, t, ts, runWithDetails, true);
            row.addStartingThreads(runWithDetails, this, i);
            this.rows.push(row);
            t = null;
            ts = null;
        }
    }

    generateRowsForProcessesThatAreEnding(ended, time, timeStampRowSpan) {
        let t = time;
        let ts = timeStampRowSpan;
        for (let i = 0; i < ended.length; i++) {
            const runWithDetails = ended[i];
            const row = new LogViewRow(this.parent, t, ts, runWithDetails, false);
            if (i === 0) {
                row.addEndingThreads(runWithDetails, ended.length)
                t = null;
                ts = null;
            }
            this.rows.push(row);
        }
        return t === null;
    }

    addRunStarting(threadIndex, startIndex, run) {
        if (this.runsStarting[threadIndex] === undefined) {
            this.runsStarting[threadIndex] = [];
        }
        this.runsStarting[threadIndex][startIndex] = run;
    }

}

module.exports.LogViewTempCalculationHelper = LogViewTempCalculationHelper;

