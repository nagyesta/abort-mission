// noinspection TypeScriptUMDGlobal
const ko = require('knockout');

class LogViewTestRunModel {
    constructor(testRunModel) {
        this.run = testRunModel;
        this.startsAt = -1;
        this.rowSpan = []; //initialized by LogViewTimelineModel.calculateRowSpans()
    }
}

class LogViewCellPlaceholder {
    constructor(rowSpan) {
        this.rowSpan = rowSpan;
        this.run = null;
    }
}

class LogViewThreadCell {
    constructor(run, rowSpan) {
        this.rowSpan = rowSpan;
        this.css = run.colorClass();
        this.selected = ko.observable(false);
        this.title = run.threadName + '\n' + run.displayName;
        this.run = run;
        this.run.addCell(this);
    }

    toggleSelected() {
        this.run.toggleHighlight();
    }

}

class LogViewRow {
    constructor(parent, timeStamp, timeStampRowSpan, runModel, isStart) {
        const run = runModel.run;
        this.parent = parent;
        this.timeStampAsLong = timeStamp !== null ? timeStamp.getTime() : -1;
        this.timeStampAsString = timeStamp !== null ? timeStamp.toISOString().slice(11, 23) : null;
        this.timeStampRowSpan = timeStampRowSpan;
        this.threads = [];
        this.isStart = isStart;
        this.detailsHtml = isStart ? run.startString() : run.endString();
        this.detailsTitle = run.className;
        this.detailsCss = run.textColorClass();
        this.visible = ko.observable(true);
        this.selected = ko.observable(false);
        this.lastStarting = this.isLastStartingProcessRow(run);
        this.lastEnding = this.isLastEndingProcessRow(run);
        this.firstOfTimeStamp = this.isFirstEndingProcessRow(run) || this.hasNoEndingProcessesAndIsFirstStartingProcessRow(run);
        this.lastOfTimeStamp = this.isLastStartingProcessRow(run) || this.hasNoStartingProcessesAndIsLastEndingProcessRow(run);
        this.run = run;
        this.run.addRow(this);
    }

    isFirstStartingProcessRow(run) {
        return this.isStart && this.parent.started[0].run.launchId === run.launchId;
    }

    isLastStartingProcessRow(run) {
        return this.isStart && this.parent.started[this.parent.started.length - 1].run.launchId === run.launchId;
    }

    isFirstEndingProcessRow(run) {
        return !this.isStart && this.parent.ended[0].run.launchId === run.launchId;
    }

    isLastEndingProcessRow(run) {
        return !this.isStart && this.parent.ended[this.parent.ended.length - 1].run.launchId === run.launchId;
    }

    hasNoEndingProcessesAndIsFirstStartingProcessRow(run) {
        return this.parent.ended.length === 0 && this.isFirstStartingProcessRow(run);
    }

    hasNoStartingProcessesAndIsLastEndingProcessRow(run) {
        return this.parent.started.length === 0 && this.isLastEndingProcessRow(run);
    }

    rowCss = ko.pureComputed(function () {
        let result = '';
        if (this.visible() === false) {
            result += 'filtered ';
        }
        const filterStart = this.parent.rootModel.filter.startTime().getTime();
        const filterEnd = this.parent.rootModel.filter.endTime().getTime();
        const hasVisibleTimeStamp = this.timeStampAsLong !== -1 && !this.parent.noneVisible();
        const isOutsideOfFilterRange = this.parent.timeStamp < filterStart || this.parent.timeStamp > filterEnd;
        if ((hasVisibleTimeStamp || this.visible()) && isOutsideOfFilterRange) {
            result += 'filter-out-of-range ';
        }
        if (this.parent.isBoundaryStart() && this.firstOfTimeStamp) {
            result += 'filter-range-start ';
        }
        if (this.parent.isBoundaryEnd() && this.lastOfTimeStamp) {
            result += 'filter-range-end ';
        }
        return result.trim();
    }, this);

    categoryBoundaryCss() {
        let result = '';
        if (this.lastStarting) {
            result += 'border-bottom ';
        } else if (this.lastEnding) {
            result += 'soft-border-bottom ';
        }
        return result.trim();
    }

    addThread(aRun) {
        this.threads.push(aRun);
    }

    addEndingThreads(aRun, threadRowSpan) {
        for (let t = 0; t < aRun.rowSpan.length; t++) {
            const rowSpan = aRun.rowSpan[t];
            if (rowSpan === 0) {
                this.addThread(new LogViewCellPlaceholder(threadRowSpan));
            }
        }
    }

    addStartingThreads(runWithDetails, parent, index) {
        for (let t = 0; t < runWithDetails.rowSpan.length; t++) {
            const rowSpan = runWithDetails.rowSpan[t];
            if (rowSpan === 0) {
                this.addThread(new LogViewCellPlaceholder(1));
            } else if (rowSpan > 0) {
                this.addThread(new LogViewThreadCell(parent.runsStarting[t][index], rowSpan));
            }
        }
    }

    toggleSelected() {
        this.run.toggleHighlight();
    }

    toggleDetails() {
        this.parent.rootModel.toggleDetails(this);
        const element = document.getElementById("detail-view");
        if (element) {
            const offsetTop = element.offsetTop;
            scroll({
                top: offsetTop,
                behavior: "smooth"
            });
        }
    }
}

module.exports.LogViewRow = LogViewRow;
module.exports.LogViewTestRunModel = LogViewTestRunModel;

