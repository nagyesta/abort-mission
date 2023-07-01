// noinspection TypeScriptUMDGlobal

const ko = require('knockout');
const {StatModel} = require('../src/stat-model');

const capitalize = function (string) {
    return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
}

class SummaryViewOutcomeModel {
    constructor(rootModel, key) {
        this.rootModel = rootModel;
        this.name = capitalize(key);
        this.key = key.toUpperCase();
        this.total = new StatModel(rootModel, () => false);
    }

    addRun(run) {
        this.total.addRun(run);
    }

    resultEntity() {
        const self = this;
        return self.rootModel.results.find(item => {
            return item.key === self.key;
        });
    }

}

class SummaryViewModel {

    constructor(rootModel) {
        this.rootModel = rootModel;
        this.results = [
            new SummaryViewOutcomeModel(this.rootModel, 'success'),
            new SummaryViewOutcomeModel(this.rootModel, 'failure'),
            new SummaryViewOutcomeModel(this.rootModel, 'abort'),
            new SummaryViewOutcomeModel(this.rootModel, 'suppressed')
        ];
        this.summary = new StatModel(this.rootModel, () => false);
    }

    addRun(run) {
        const result = this.results.filter(item => {
            return item.key === run.result.toUpperCase();
        });
        result[0].addRun(run);
        this.summary.addRun(run);
    }

}

module.exports.SummaryViewModel = SummaryViewModel;
