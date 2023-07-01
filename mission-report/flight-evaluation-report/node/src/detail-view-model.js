// noinspection TypeScriptUMDGlobal

const ko = require('knockout');
const {StatModel} = require('../src/stat-model');

class DetailViewMethodModel {
    constructor(run, methodName) {
        this.rootModel = run.rootModel;
        this.parent = run.rootModel.detailView;
        this.methodName = methodName;
        this.methodKey = run.methodKey;
        this.countdown = run.countdown;
        this.collapsed = ko.observable(this.parent.isCollapsed(run.classKey, run.methodKey));
        this.filtered = new StatModel(run.rootModel, r => r.rows[0].visible() === false);
        this.total = new StatModel(run.rootModel, () => false);
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
        const result = new StatModel(this.rootModel, () => false);
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
