// noinspection TypeScriptUMDGlobal

const ko = require('knockout');
const {KeyValueEntityModel} = require('../src/key-value-entity-model');
const {TestRunModel} = require('../src/test-run-model');
const {LogViewTimelineModel} = require('../src/log-view-timeline-model');
const {TestRunFilter} = require('../src/filter');

class DetailViewStatModel {
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
        this.min = Math.min(other.min, this.min);
        this.max = Math.max(other.max, this.max);
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

    formatDuration(duration) {
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
                const fractionalDigits = hasMinutes ? 0 : 1;
                result += (duration / 1000).toFixed(fractionalDigits) + "<small>s</small>";
            } else {
                result += duration.toFixed(0) + "<small>ms</small>";
            }
        }
        return result;
    }

    average() {
        return this.formatDuration(this.runs === 0 ? null : this.sum / this.runs);
    }

    minimum() {
        return this.formatDuration(this.min, 0);
    }

    maximum() {
        return this.formatDuration(this.max, 0);
    }

    totalSum() {
        return this.formatDuration(this.sum, 0);
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

class DetailViewMethodModel {
    constructor(run, methodName) {
        this.rootModel = run.rootModel;
        this.parent = run.rootModel.detailView;
        this.methodName = methodName;
        this.methodKey = run.methodKey;
        this.countdown = run.countdown;
        this.collapsed = ko.observable(this.parent.isCollapsed(run.classKey, run.methodKey));
        this.filtered = new DetailViewStatModel(run.rootModel, r => r.rows[0].visible() === false);
        this.total = new DetailViewStatModel(run.rootModel, () => false);
        this.addRun(run);
    }

    addRun(run) {
        this.filtered.addRun(run);
        this.total.addRun(run);
    }

    missionStageEntity() {
        const self = this;
        return self.rootModel.missionStage.find(item => {
            return item.key === (self.countdown ? 'c' : 'm');
        });
    }

    methodEntity() {
        if (this.countdown) {
            return null;
        }
        const self = this;
        return self.rootModel.methodNames.find(item => {
            return item.key === self.methodKey;
        });
    }

    collapseExpandIcon() {
        return this.collapsed()
                ? '<!-- Chevron right icon under MIT license from https://feathericons.com/ --><svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-chevron-right"><polyline points="9 18 15 12 9 6"></polyline></svg>'
                : '<!-- Chevron down icon under MIT license from https://feathericons.com/ --><svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-chevron-down"><polyline points="6 9 12 15 18 9"></polyline></svg>';
    }

    toggleMethod() {
        const classKey = this.parent.classKey();
        this.parent.toggleCollapsed(classKey, this.methodKey);
        this.collapsed(this.parent.isCollapsed(classKey, this.methodKey));
    }

    selectedData() {
        return this.parent.showFiltered() ? this.filtered : this.total;
    }

    css() {
        return 'result-' + this.worstResult();
    }

    worstResult() {
        return this.selectedData().worstResult();
    }
}

class DetailViewModel {

    constructor(rootModel) {
        this.rootModel = rootModel;
        this.classKey = ko.observable("");
        this.className = ko.observable("");
        this.visible = ko.observable(false);
        this.expandedMethods = [];
        this.showFiltered = ko.observable(true);
    }

    toggleFiltering() {
        this.showFiltered(!this.showFiltered());
    }

    filteringText() {
        return this.showFiltered() ? "Don't filter overview" : "Show filtered overview";
    }

    isCollapsed(classKey, methodKey) {
        if (this.classKey() !== classKey) {
            throw new Error("Class keys does not match: " + this.classKey() + " != " + classKey);
        }
        return this.expandedMethods.indexOf(methodKey) === -1;
    }

    toggleCollapsed(classKey, methodKey) {
        const isCollapsed = this.isCollapsed(classKey, methodKey);
        if (isCollapsed) {
            this.expandedMethods.push(methodKey);
        } else {
            const index = this.expandedMethods.indexOf(methodKey);
            this.expandedMethods.splice(index, 1);
        }
    }

    classNameShort = ko.pureComputed(function () {
        const classEntity = this.classEntity();
        return classEntity === undefined ? this.className() : classEntity.valuePrefixShort() + classEntity.valueMain()
    }, this);

    classEntity() {
        const self = this;
        return self.rootModel.classNames.find(item => {
            return item.key === self.classKey();
        });
    }

    methods = ko.pureComputed(function () {
        const relevantRuns = this.rootModel.runs.filter(item => {
            return item.classKey === this.classKey();
        });
        const result = {}
        for (const run of relevantRuns) {
            const methodName = run.methodName ? run.methodName : 'Countdown';
            if (result[methodName]) {
                result[methodName].addRun(run);
            } else {
                result[methodName] = new DetailViewMethodModel(run, methodName);
            }
        }
        const resultArray = [];
        for (const key in result) {
            resultArray.push(result[key]);
        }
        return resultArray;
    }, this);

    summary = ko.pureComputed(function () {
        const result = new DetailViewStatModel(this.rootModel, () => false);
        for (const method of this.methods()) {
            const selectedData = method.selectedData();
            result.merge(selectedData);
        }
        return result;
    }, this);

    css() {
        return 'result-' + this.worstResult();
    }

    worstResult = ko.pureComputed(function () {
        return this.summary().worstResult();
    }, this);

    hide() {
        this.visible(false);
        this.expandedMethods.length = 0;
    }

    setRun(run) {
        const self = this;
        if (run === null) {
            self.classKey("");
            self.className("");
            self.hide();
            return;
        }
        self.classKey(run.classKey);
        self.className(run.className);
        self.visible(true);
    }
}

module.exports.DetailViewModel = DetailViewModel;
