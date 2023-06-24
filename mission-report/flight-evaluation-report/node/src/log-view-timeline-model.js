// noinspection TypeScriptUMDGlobal
const {LogViewTimeStampModel} = require('../src/log-view-time-stamp-model');

class LogViewTimelineModel {
    constructor(rootModel) {
        this.rootModel = rootModel;
        this.logEntryMultiMap = [];
        this.totalThreadWidthLookup = {
            1: 20,
            2: 40,
            4: 60,
            8: 120,
            16: 240,
            32: 320,
            64: 360
        };
        this.threadCategoryCache = null;
    }

    threadCategory() {
        if (this.threadCategoryCache === null) {
            this.threadCategoryCache = Math.pow(2, Math.ceil(Math.log2(this.rootModel.threadNames.length)));
        }
        return this.threadCategoryCache;
    }

    threadColumnCategoryCss() {
        return 'threads-t' + this.threadCategory();
    }

    totalThreadWidth() {
        return this.totalThreadWidthLookup[this.threadCategory()];
    }

    markBoundaries = async function () {
        for (let i = 1; i < this.logEntryMultiMap.length - 1; i++) {
            const currentItem = this.logEntryMultiMap[i];
            const previousItem = this.logEntryMultiMap[i - 1];
            const nextItem = this.logEntryMultiMap[i + 1];
            const boundaryStart = this.rootModel.filter.startTime().getTime();
            const boundaryEnd = this.rootModel.filter.endTime().getTime();
            if (currentItem.timeStamp >= boundaryStart && previousItem.timeStamp < boundaryStart) {
                currentItem.isBoundaryStart(true);
            } else {
                if (currentItem.isBoundaryStart()) {
                    currentItem.isBoundaryStart(false);
                }
            }
            if (currentItem.timeStamp <= boundaryEnd && nextItem.timeStamp > boundaryEnd) {
                currentItem.isBoundaryEnd(true);
            } else {
                if (currentItem.isBoundaryEnd()) {
                    currentItem.isBoundaryEnd(false);
                }
            }
        }
    }

    addLogEntryToTimeStamp(timeStamp, logEntry) {
        const epochMillis = timeStamp.getTime();
        const index = this.logEntryMultiMap.findIndex(element => element.timeStamp === epochMillis)
        let item;
        if (index === -1) {
            item = new LogViewTimeStampModel(this, timeStamp);
            this.logEntryMultiMap.push(item);
            //must sort the time stamps and keep started and ended arrays sorted BY run.start, run.end ASC
            this.logEntryMultiMap.sort((a, b) => a.timeStamp - b.timeStamp);
        } else {
            item = this.logEntryMultiMap[index];
        }
        item.addEntry(logEntry);
    }

    addLogEntry(testRunModel) {
        this.addLogEntryToTimeStamp(testRunModel.start, testRunModel);
        if (testRunModel.start.getTime() !== testRunModel.end.getTime()) {
            this.addLogEntryToTimeStamp(testRunModel.end, testRunModel);
        }
    }

    initThreadCounters() {
        const threadCounters = {};
        for (let i = 0; i < this.rootModel.threadNames.length; i++) {
            const threadName = this.rootModel.threadNames[i].value;
            threadCounters[threadName] = {
                name: threadName,
                index: i,
                counter: 0,
                run: null,
                shouldEnd: false,
                lastStart: null,
                nextFreeIndex: 0
            };
        }
        return threadCounters;
    }

    isRunningOnCurrentThread(currentLogViewRun, aThreadName) {
        const threadNameOfCurrentRun = currentLogViewRun.run.threadName;
        return threadNameOfCurrentRun === aThreadName;
    }

    isActiveThread(counter) {
        return counter.run !== null;
    }

    resetFreeIndexCounters(threadCounters) {
        for (const aThreadName of Object.keys(threadCounters)) {
            const counter = threadCounters[aThreadName];
            counter.nextFreeIndex = 0;
        }
    }

    updateCounterOnStart(counter, counterValue, nextFreeIndex, currentLogViewRun) {
        counter.counter = counterValue;
        counter.run = counterValue > 0 ? currentLogViewRun : null;
        counter.lastStart = currentLogViewRun.run.start.getTime();
        counter.nextFreeIndex = nextFreeIndex;
    }

    countRunsEndingInCurrentMilliSecond(logEntry, threadCounters) {
        this.resetFreeIndexCounters(threadCounters);
        const ended = logEntry.ended;
        for (const currentLogViewRun of ended) {
            for (const aThreadName of Object.keys(threadCounters)) {
                const counter = threadCounters[aThreadName];
                if (this.isRunningOnCurrentThread(currentLogViewRun, aThreadName)) {
                    counter.shouldEnd = true;
                    logEntry._temporary.threadsEnding[counter.index] = counter.name;
                }
                if (this.isActiveThread(counter)) {
                    counter.counter++;
                    counter.nextFreeIndex++;
                }
            }
        }
        this.cleanUpThreadCounters(threadCounters);
    }

    countRunsStartingInCurrentMilliSecond(logEntry, threadCounters) {
        this.resetFreeIndexCounters(threadCounters);
        const started = logEntry.started;
        for (let startIndex = 0; startIndex < started.length; startIndex++) {
            const currentLogViewRun = started[startIndex];
            for (const aThreadName of Object.keys(threadCounters)) {
                const counter = threadCounters[aThreadName];
                if (this.isActiveThread(counter)) {
                    counter.counter++;
                    counter.nextFreeIndex++;
                } else if (this.isRunningOnCurrentThread(currentLogViewRun, aThreadName)) {
                    logEntry._temporary.threadsStarting[counter.index] = counter.name;
                    if (currentLogViewRun.run.endsInSameMilliSecond()) {
                        this.updateCounterOnStart(counter, 0, startIndex + 1, currentLogViewRun);
                        currentLogViewRun.run.rowSpan = 1;
                        logEntry._temporary.addRunStarting(counter.index, startIndex, currentLogViewRun.run);
                    } else {
                        const nextFreeIndex = counter.nextFreeIndex;
                        const lengthAlreadyAllocatedToRun = startIndex + 1 - nextFreeIndex;
                        this.updateCounterOnStart(counter, lengthAlreadyAllocatedToRun, startIndex + 1, currentLogViewRun);
                        logEntry._temporary.addRunStarting(counter.index, nextFreeIndex, currentLogViewRun.run);
                    }
                }
            }
        }
    }

    cleanUpThreadCounters(threadCounters) {
        //clean-up threads which should end in current millisecond
        for (const aThreadName of Object.keys(threadCounters)) {
            const counter = threadCounters[aThreadName];
            if (counter.run !== null && counter.shouldEnd) {
                counter.run.run.rowSpan = counter.counter;
                counter.run = null;
                counter.counter = 0;
                counter.shouldEnd = false;
                counter.nextFreeIndex = 0;
            }
        }
    }

    markThreadsWithRunsStartedBeforeAndEndingAfterCurrentMilliSecond(logEntry, threadCounters) {
        for (const aThreadName of Object.keys(threadCounters)) {
            const counter = threadCounters[aThreadName];
            if (this.isActiveThread(counter)) {
                if (counter.lastStart === null || counter.lastStart < logEntry.timeStamp) {
                    logEntry._temporary.threadsContinuing[counter.index] = counter.name;
                }
            }
        }
    }

    markThreadsWhereNoActivityWasSeenDuringCurrentMilliSecond(logEntry, threadCounters) {
        for (const aThreadName of Object.keys(threadCounters)) {
            const counter = threadCounters[aThreadName];
            if (logEntry._temporary.threadsContinuing[counter.index] === undefined) {
                if (logEntry._temporary.threadsEnding[counter.index] === undefined) {
                    logEntry._temporary.threadsWithNoActivityDuringEnds[counter.index] = counter.name;
                }
                if (logEntry._temporary.threadsStarting[counter.index] === undefined) {
                    logEntry._temporary.threadsWithNoActivityDuringStarts[counter.index] = counter.name;
                    if (counter.lastStart === logEntry.timeStamp) {
                        console.error("ERROR: " + new Date(logEntry.timeStamp).toISOString() + ":" + counter.name + " should have been marked as starting but was not!");
                    } else if (this.isActiveThread(counter)) {
                        console.error("ERROR: " + new Date(logEntry.timeStamp).toISOString() + ":" + counter.name + " should have been marked as continuing but was not!");
                    }
                }
            }
        }
    }

    calculateThreadUtilization() {
        const startTime = new Date().getTime();
        const threadCounters = this.initThreadCounters();
        for (let i = 0; i < this.logEntryMultiMap.length; i++) {
            const logEntry = this.logEntryMultiMap[i];
            this.countRunsEndingInCurrentMilliSecond(logEntry, threadCounters);
            this.countRunsStartingInCurrentMilliSecond(logEntry, threadCounters);
            this.markThreadsWithRunsStartedBeforeAndEndingAfterCurrentMilliSecond(logEntry, threadCounters);
            this.markThreadsWhereNoActivityWasSeenDuringCurrentMilliSecond(logEntry, threadCounters);
        }
        const endTime = new Date().getTime();
        console.debug("calculateThreadUtilization took " + (endTime - startTime) + "ms");
    }

    calculateRowSpans() {
        const startTime = new Date().getTime();
        for (let i = 0; i < this.logEntryMultiMap.length; i++) {
            const logEntry = this.logEntryMultiMap[i];
            logEntry._temporary.calculateRowSpans();
        }
        const endTime = new Date().getTime();
        console.debug("calculateRowSpans took " + (endTime - startTime) + "ms");
    }

    generateRows() {
        const startTime = new Date().getTime();
        for (let i = 0; i < this.logEntryMultiMap.length; i++) {
            const logEntry = this.logEntryMultiMap[i];
            logEntry.generateRows();
        }
        const endTime = new Date().getTime();
        console.debug("generateRows took " + (endTime - startTime) + "ms");
    }
}

module.exports.LogViewTimelineModel = LogViewTimelineModel;

