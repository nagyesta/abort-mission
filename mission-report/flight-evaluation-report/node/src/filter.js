// noinspection TypeScriptUMDGlobal,JSIgnoredPromiseFromCall,ES6MissingAwait

const ko = require('knockout');
const {KeyValueEntityModel} = require('../src/key-value-entity-model');

const entityTypeToCollection = {
    "FEATURE": "classNames",
    "CLASS": "classNames",
    "METHOD": "methodNames",
    "SCENARIO": "methodNames",
    "THREAD": "threadNames"
}

function findCollectionForType(entityType, model) {
    return model[entityTypeToCollection[entityType]];
}

class TestRunFilter {
    constructor(parent) {
        this.rootModel = parent;
        this.startTime = ko.observable(new Date());
        this.endTime = ko.observable(new Date());
        this.input = ko.observable("");
        this.hideList = ko.observable(true);
    }

    formatDateAsString(date) {
        return date.toISOString().slice(0, 23).replace('T', ' ');
    }

    startTimeAsString = ko.pureComputed(function () {
        return this.formatDateAsString(this.startTime());
    }, this);

    endTimeAsString = ko.pureComputed(function () {
        return this.formatDateAsString(this.endTime());
    }, this);

    invalidRange = ko.pureComputed(function () {
        return this.startTime() >= this.endTime();
    }, this);

    resetStartTime() {
        this.startTime(this.rootModel.getFirstStartDate());
        this.refreshFilter();
    }

    resetEndTime() {
        this.endTime(this.rootModel.getLastEndDate());
        this.refreshFilter();
    }

    setStartTime(date) {
        const self = this;
        return function () {
            self.startTime(date);
            self.hideOptions();
            self.refreshFilter();
        }
    }

    setEndTime(date) {
        const self = this;
        return function () {
            self.endTime(date);
            self.hideOptions();
            self.refreshFilter();
        }
    }

    setRange(date, rangeInSeconds) {
        const self = this;
        return function () {
            self.startTime(new Date(date.getTime() - 1000 * rangeInSeconds));
            self.endTime(new Date(date.getTime() + 1000 * rangeInSeconds));
            self.hideOptions();
            self.refreshFilter();
        }
    }

    selectAllMatching(entityType, matchType, include) {
        const self = this;
        return function () {
            let collection = findCollectionForType(entityType, self.rootModel);
            let filterFunction = function (item) {
                return item.value.toLowerCase().includes(self.input().toLowerCase());
            };
            if (matchType === "start") {
                filterFunction = function (item) {
                    return item.value.toLowerCase().startsWith(self.input().toLowerCase());
                };
            } else if (matchType === "end") {
                filterFunction = function (item) {
                    return item.value.toLowerCase().endsWith(self.input().toLowerCase());
                };
            }
            collection.forEach(function (item) {
                if (filterFunction(item)) {
                    item.include(include);
                    item.exclude(!include);
                }
            });
            self.hideOptions();
            self.refreshFilter();
        };
    }

    useFeatureTerminology() {
        return this.rootModel.classNames.length > 0 && this.rootModel.classNames[0].entityType === "FEATURE";
    }

    placeholder = ko.pureComputed(function () {
        if (this.useFeatureTerminology()) {
            return 'Type to filter by time stamps, run results, stage, feature names, scenario names, thread names or matchers...';
        } else {
            return 'Type to filter by time stamps, run results, stage, class names, method names, thread names or matchers...';
        }
    }, this);

    addMatchingItems(collection, array, predicate) {
        let result = array || [];
        collection.forEach(function (item) {
            if (predicate(item)) {
                result.push(item);
            }
        });
        return result;
    }

    matchingOptions = ko.pureComputed(function () {
        let result = [];
        if (this.input() && this.input().length > 2) {
            const self = this;
            this.addTimeShortcutIfMatching(result);
            this.shortcutLookups().forEach(function (lookup) {
                self.shortcutMatchTypes().forEach(function (match) {
                    if (lookup.collection.findIndex(match.matchFunction) !== -1) {
                        result.push(new KeyValueEntityModel(self.rootModel, lookup.entityType, self.input(), match.description + self.input(), false, false, match.matchType));
                    }
                });
            });
        }
        return this.addItemsMatchingFilterText(this.rootModel.allRules, result);
    }, this);

    addItemsMatchingFilterText(collection, array) {
        const input = this.input().toLowerCase();
        return this.addMatchingItems(collection, array, item => item.value.toLowerCase().includes(input));
    }

    addTimeShortcutIfMatching(result) {
        const timeRegex = /^\d{2}:\d{2}:\d{2}$/;
        const preciseTimeRegex = /^\d{2}:\d{2}:\d{2}.\d{3}$/;
        const dateTimeRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/;
        const preciseDateTimeRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d{3}$/;
        const exampleIsoString = this.formatDateAsString(this.rootModel.getFirstStartDate());
        const zone = 'Z';
        if (preciseDateTimeRegex.test(this.input())) {
            const value = new Date(this.input().replace(' ', 'T') + zone);
            const dateAsString = this.formatDateAsString(value);
            result.push(new KeyValueEntityModel(this.rootModel, "TIME", value, dateAsString, false, false));
        } else if (dateTimeRegex.test(this.input())) {
            const value = new Date(this.input().replace(' ', 'T') + '.000' + zone);
            const dateAsString = this.formatDateAsString(value);
            result.push(new KeyValueEntityModel(this.rootModel, "TIME", value, dateAsString, false, false));
        } else if (timeRegex.test(this.input())) {
            const value = new Date(exampleIsoString.slice(0, 11) + this.input() + '.000' + zone);
            const dateAsString = this.formatDateAsString(value);
            result.push(new KeyValueEntityModel(this.rootModel, "TIME", value, dateAsString, false, false));
        } else if (preciseTimeRegex.test(this.input())) {
            const value = new Date(exampleIsoString.slice(0, 11) + this.input() + zone);
            const dateAsString = this.formatDateAsString(value);
            result.push(new KeyValueEntityModel(this.rootModel, "TIME", value, dateAsString, false, false));
        }
    }

    shortcutMatchTypes() {
        const startsWith = item => item.value.toLowerCase().startsWith(this.input().toLowerCase());
        const contains = item => item.value.toLowerCase().includes(this.input().toLowerCase());
        const endsWith = item => item.value.toLowerCase().endsWith(this.input().toLowerCase());
        return [
            {matchFunction: startsWith, matchType: "start", description: "Starting with "},
            {matchFunction: contains, matchType: "any", description: "Containing "},
            {matchFunction: endsWith, matchType: "end", description: "Ending with "},
        ];
    }

    shortcutLookups() {
        const classType = this.useFeatureTerminology() ? "FEATURE" : "CLASS";
        const methodType = this.useFeatureTerminology() ? "SCENARIO" : "METHOD";
        return [
            {collection: this.rootModel.classNames, entityType: classType},
            {collection: this.rootModel.methodNames, entityType: methodType},
            {collection: this.rootModel.threadNames, entityType: "THREAD"},
        ];
    }

    hideOptions() {
        this.input("");
        return this.hideList(true);
    }

    refreshFilter = async function () {
        const startTime = new Date().getTime();
        this.rootModel.runs.forEach(function (run) {
            run.updateVisibility();
        });
        this.rootModel.logViewTimeline.markBoundaries(this.startTime(), this.endTime());
        const endTime = new Date().getTime();
        console.debug("Filtering took " + (endTime - startTime) + " ms");
    }

    showAllOptions(obj, event) {
        if (event === undefined || event.key === "Escape") {
            return this.hideList(true);
        } else {
            return this.hideList(false);
        }
    }

    toggleOptions() {
        this.hideList(!this.hideList());
    }

    allIncludes = ko.pureComputed(function () {
        return this.addMatchingItems(this.rootModel.allRules, null, item => item.include());
    }, this);

    allExcludes = ko.pureComputed(function () {
        return this.addMatchingItems(this.rootModel.allRules, null, item => item.exclude());
    }, this);

    startTimeIsNotDefault = ko.pureComputed(function () {
        return this.startTime().getTime() !== this.rootModel.getFirstStartDate().getTime();
    }, this);

    endTimeIsNotDefault = ko.pureComputed(function () {
        return this.endTime().getTime() !== this.rootModel.getLastEndDate().getTime();
    }, this);

    isFiltered = ko.pureComputed(function () {
        return this.allIncludes().length > 0
                || this.allExcludes().length > 0
                || this.startTimeIsNotDefault()
                || this.endTimeIsNotDefault();
    }, this);

    clearAllItemsOf(collection) {
        let changed = false;
        collection.forEach(function (item) {
            if (item.include()) {
                item.include(false);
                changed = true;
            }
            if (item.exclude()) {
                item.exclude(false);
                changed = true;
            }
        });
        return changed;
    }

    clear() {
        const startTime = new Date().getTime();
        let changed = this.clearAllItemsOf(this.rootModel.allRules);
        if (this.startTimeIsNotDefault()) {
            this.startTime(this.rootModel.getFirstStartDate());
            changed = true;
        }
        if (this.endTimeIsNotDefault()) {
            this.endTime(this.rootModel.getLastEndDate());
            changed = true;
        }
        if (changed) {
            this.rootModel.runs.forEach(function (run) {
                run.updateVisibility();
            });
        }
        const endTime = new Date().getTime();
        console.debug("Clear took " + (endTime - startTime) + " ms");
    }
}

module.exports.TestRunFilter = TestRunFilter;

