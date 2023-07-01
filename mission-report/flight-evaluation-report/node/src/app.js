// noinspection TypeScriptUMDGlobal

const ko = require('knockout');
const {KeyValueEntityModel} = require('../src/key-value-entity-model');
const {TestRunModel} = require('../src/test-run-model');
const {LogViewTimelineModel} = require('../src/log-view-timeline-model');
const {TestRunFilter} = require('../src/filter');
const {DetailViewModel} = require('../src/detail-view-model');
const {formatDuration} = require('../src/stat-model');
const {SummaryViewModel} = require('../src/summary-view-model');

class FlightEvaluationReportInitializer {

    constructor(rootModel) {
        this.rootModel = rootModel;
    }

    addIfMissing(target, all, toAdd) {
        for (let i = 0; i < target.length; i++) {
            if (target[i].key === toAdd.key) {
                return;
            }
        }
        all.push(toAdd);
        return target.push(toAdd);
    }

    addMatcher(matcher) {
        this.addIfMissing(this.rootModel.matchers, this.rootModel.allRules,
                new KeyValueEntityModel(this.rootModel, "MATCHER", matcher.key, matcher.value, false, false));
        this.rootModel.matchers.sort((a, b) => a.key.localeCompare(b.value));
    }

    addClass(classObject) {
        const entityType = classObject.isGherkin ? "FEATURE" : "CLASS";
        this.addIfMissing(this.rootModel.classNames, this.rootModel.allRules,
                new KeyValueEntityModel(this.rootModel, entityType, classObject.key, classObject.value, false, false));
        this.rootModel.classNames.sort((a, b) => a.key.localeCompare(b.value));
    }

    addMethod(method) {
        const entityType = method.isGherkin ? "SCENARIO" : "METHOD";
        this.addIfMissing(this.rootModel.methodNames, this.rootModel.allRules,
                new KeyValueEntityModel(this.rootModel, entityType, method.key, method.value, false, false));
        this.rootModel.methodNames.sort((a, b) => a.key.localeCompare(b.value));
    }

    addThread(thread) {
        this.addIfMissing(this.rootModel.threadNames, this.rootModel.allRules,
                new KeyValueEntityModel(this.rootModel, "THREAD", thread.key, thread.value, false, false));
        this.rootModel.threadNames.sort((a, b) => a.key.localeCompare(b.value));
    }

    addResult(result) {
        this.addIfMissing(this.rootModel.results, this.rootModel.allRules,
                new KeyValueEntityModel(this.rootModel, "RESULT", result.toUpperCase(), result, false, false));
    }

    addMissionStage(result) {
        this.addIfMissing(this.rootModel.missionStage, this.rootModel.allRules,
                new KeyValueEntityModel(this.rootModel, "STAGE", result === 'Countdown' ? "c" : "m", result, false, false));
    }

    addRun(run) {
        this.rootModel.runs.push(run);
        this.rootModel.summaryView.addRun(run);
    }

    initStaticData() {
        for (const result of ["Success", "Failure", "Abort", "Suppressed"]) {
            this.addResult(result);
        }
        for (const missionStage of ["Countdown", "Mission"]) {
            this.addMissionStage(missionStage);
        }
    }

    initMetaData(reportJson) {
        for (const matcher of Object.keys(reportJson.matchers)) {
            this.addMatcher({
                key: matcher,
                value: reportJson.matchers[matcher]
            });
        }
        for (const classObject of Object.keys(reportJson.classNames)) {
            this.addClass({
                key: classObject,
                value: reportJson.classNames[classObject],
                isGherkin: reportJson.classNames[classObject].endsWith(".feature")
            });
        }
        for (const method of Object.keys(reportJson.methodNames)) {
            this.addMethod({
                key: method,
                value: reportJson.methodNames[method],
                isGherkin: reportJson.methodNames[method].includes(" ")
            });
        }
    }

    initRuns(reportJson) {
        for (const run of reportJson.runs) {
            this.addThread({key: run.threadName, value: run.threadName});
            const testRunModel = new TestRunModel(this.rootModel, run);
            this.addRun(testRunModel);
            this.rootModel.logViewTimeline.addLogEntry(testRunModel);
        }
        this.rootModel.logViewTimeline.calculateThreadUtilization();
        this.rootModel.logViewTimeline.calculateRowSpans();
        this.rootModel.logViewTimeline.generateRows();
        this.rootModel.filter.startTime(this.rootModel.getFirstStartDate());
        this.rootModel.filter.endTime(this.rootModel.getLastEndDate());
    }

    initData(reportJson) {
        this.initStaticData();
        this.initMetaData(reportJson);
        this.initRuns(reportJson);
    }
}

class FlightEvaluationReportModel {

    constructor() {
        this.missionStage = [];
        this.matchers = [];
        this.classNames = [];
        this.methodNames = [];
        this.threadNames = [];
        this.results = [];
        this.allRules = [];
        this.runs = [];
        this.summaryView = new SummaryViewModel(this);
        this.logViewTimeline = new LogViewTimelineModel(this);
        this.detailView = new DetailViewModel(this);
        this.filter = new TestRunFilter(this);
        this._temporary = new FlightEvaluationReportInitializer(this);
    }

    filterByExclude(ruleList, run) {
        //there are no matching exclude rules
        return ruleList.findIndex(rule => {
            if (run.isNotApplicableRule(rule)) {
                return false;
            }
            const elements = run.getMatchElementsFor(rule.entityType);
            if (elements === null) {
                console.warn("No elements for ", rule.entityType, run);
            }
            return rule.exclude() && rule.keyMatchesElement(elements);
        }) === -1;
    }

    filterByInclude(ruleList, run) {
        //either there are no include rules or there is at least one include rule that allows the element
        return ruleList.findIndex(rule => rule.include()) === -1
                || ruleList.findIndex(rule => {
                    if (run.isNotApplicableRule(rule)) {
                        return false;
                    }
                    const elements = run.getMatchElementsFor(rule.entityType);
                    return rule.include() && rule.keyMatchesElement(elements);
                }) !== -1;
    }

    filterByRules(ruleList, run) {
        const include = this.filterByInclude(ruleList, run);
        const exclude = this.filterByExclude(ruleList, run);
        return include && exclude;
    }

    filterByAllRules(run) {
        return this.filterByRules(this.allRules, run);
    }

    initData(reportJson) {
        this._temporary.initData(reportJson);
        this._temporary = null;
    }

    initBindings() {
        const startTime = new Date().getTime();
        ko.applyBindings(this, document.getElementById("binding-root"));
        const endTime = new Date().getTime();
        console.debug("initBindings took " + (endTime - startTime) + "ms");
    }

    getFirstStartDate() {
        return this.runs.length > 0 ? this.runs[0].start : null;
    }

    getLastEndDate() {
        return this.runs.length > 0 ? this.runs[this.runs.length - 1].end : null;
    }

    getTotalRunTime() {
        const duration = this.runs.length > 0 ? this.getLastEndDate() - this.getFirstStartDate() : null;
        return formatDuration(duration, 2);
    }

    testStart = ko.pureComputed(function () {
        return this.convertToDateTimeString(this.getFirstStartDate());
    }, this);

    testEnd = ko.pureComputed(function () {
        return this.convertToDateTimeString(this.getLastEndDate());
    }, this);

    convertToDateTimeString(date) {
        if (date === null) {
            return '-';
        }
        return date.toISOString().replace('T', ' ').slice(0, 23)
    }

    toggleDetails(row) {
        this.detailView.setRun(row.run);
    }
}

module.exports.FlightEvaluationReportModel = FlightEvaluationReportModel;
if (typeof window !== 'undefined') {
    window.FlightEvaluationReportModel = FlightEvaluationReportModel;
}
