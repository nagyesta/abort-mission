// noinspection TypeScriptUMDGlobal

class TestRunModel {

    constructor(root, source) {
        this.rootModel = root;
        this.matcherKeys = source.matcherKeys || [];
        this.classKey = source.classKey;
        this.className = root.classNames.find(item => item.key === this.classKey).value;
        this.methodKey = source.methodKey;
        this.methodName = this.methodKey === null ? null : root.methodNames.find(item => item.key === this.methodKey).value;
        this.countdown = source.countdown;
        this.displayName = source.displayName;
        this.start = new Date(source.start);
        this.end = new Date(source.end);
        this.launchId = source.launchId;
        this.threadName = source.threadName;
        this.result = source.result;
        this.highlight = false;
        this.rows = [];
        this.cells = [];
        this.rowSpan = 0; //initialized by parent.calculateThreadUtilization()
        if (source.throwableClass) {
            this.stackTrace = ['Caught ' + source.throwableClass + ": " + source.throwableMessage]
            for (const stackTraceElement of source.stackTrace) {
                this.stackTrace.push('at ' + stackTraceElement)
            }
        }
    }

    toggleHighlight() {
        const self = this;
        this.highlight = !this.highlight;
        this.cells.forEach(function (cell) {
            cell.selected(self.highlight);
        });
        this.rows.forEach(function (row) {
            row.selected(self.highlight);
        });
    }

    addCell(cell) {
        this.cells.push(cell);
    }

    addRow(row) {
        this.rows.push(row);
    }


    hasStackTrace() {
        return this.stackTrace !== undefined && this.stackTrace.length > 0;
    }

    isNotApplicableRule(rule) {
        return (rule.entityType.toLowerCase() === "method" || rule.entityType.toLowerCase() === "scenario") && this.countdown;
    }

    getMatchElementsFor(matcherEntityType) {
        const self = this;
        const elementByEntityType = {
            "class": self.classKey,
            "feature": self.classKey,
            "method": self.methodKey,
            "scenario": self.methodKey,
            "thread": self.threadName,
            "result": self.result,
            "matcher": self.matcherKeys,
            "stage": self.countdown ? "c" : "m"
        }
        return elementByEntityType[matcherEntityType.toLowerCase()] || "";
    }

    updateVisibility = async function () {
        const rootModel = this.rootModel;
        const visible = this.end.getTime() >= rootModel.filter.startTime().getTime()
                && this.start.getTime() <= rootModel.filter.endTime().getTime()
                && rootModel.filterByAllRules(this);
        this.rows.forEach(function (row) {
            if (row.visible() !== visible) {
                row.visible(visible);
                row.parent.updateVisibility(visible);
            }
        });
    }

    endsInSameMilliSecond() {
        return this.start.getTime() === this.end.getTime();
    }

    getActionTypeString() {
        if (this.countdown) {
            return " preparation of ";
        } else {
            return " execution of ";
        }
    }

    getStartAction() {
        if (this.isAborted()) {
            return "Evaluated";
        } else {
            return "Started";
        }
    }

    getEndAction() {
        if (this.isAborted()) {
            return "Aborted";
        } else if (this.isSuppressed()) {
            return "Suppressed failure during";
        } else if (this.isSuccess()) {
            return "Completed";
        } else {
            return "Failed to complete";
        }
    }

    startString() {
        let message;
        if (this.endsInSameMilliSecond()) {
            message = this.getStartAction() + ' and ' + this.getEndAction().toLowerCase()
                    + this.getActionTypeString() + '"' + this.displayName + '"';
            if (this.hasStackTrace()) {
                message += '<br /><br />' + this.stackTrace[0] + '<br /><div class="stack-trace">' + this.stackTrace.slice(1).join('<br />') + '</div>';
            }
        } else {
            message = this.getStartAction() + this.getActionTypeString() + '"' + this.displayName + '"';
        }
        return message;
    }

    endString() {
        let message = this.getEndAction() + this.getActionTypeString() + '"' + this.displayName + '"';
        if (this.hasStackTrace()) {
            message += '<br /><br />' + this.stackTrace[0] + '<br /><div class="stack-trace">' + this.stackTrace.slice(1).join('<br />') + '</div>';
        }
        return message;
    }

    isSuccess() {
        return this.result === "SUCCESS";
    }

    isFailure() {
        return this.result === "FAILURE";
    }

    isAborted() {
        return this.result === "ABORT";
    }

    isSuppressed() {
        return this.result === "SUPPRESSED";
    }

    colorClass() {
        return (this.countdown ? "countdown-" : "mission-") + this.result.toLowerCase();
    }

    textColorClass() {
        return "text-" + this.result.toLowerCase();
    }
}

module.exports.TestRunModel = TestRunModel;

