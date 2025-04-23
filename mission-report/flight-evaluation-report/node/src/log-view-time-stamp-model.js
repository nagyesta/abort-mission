// noinspection TypeScriptUMDGlobal
const ko = require('knockout');
const {LogViewTempCalculationHelper} = require("./log-view-calculation-helper");
const {LogViewTestRunModel} = require("./log-view-time-stamp-row-model");

class LogViewTimeStampModel {

    constructor(parent, timeStamp) {
        this.timelineModel = parent;
        this.rootModel = parent.rootModel;
        this.timeStamp = timeStamp.getTime();
        this.timeStampAsDate = timeStamp;
        this.ended = [];
        this.started = [];
        this._temporary = new LogViewTempCalculationHelper(this);
        this.rows = [];
        this.noneVisible = ko.observable(false);
        this.isBoundaryStart = ko.observable(false);
        this.isBoundaryEnd = ko.observable(false);
    }

    generateRows() {
        this.rows = this._temporary.generateRows();
        this._temporary = null;
    }

    updateVisibility = async function (optionalVisibility) {
        if (optionalVisibility === true) {
            if (this.noneVisible()) {
                this.noneVisible(false);
            }
        } else {
            for (const row of this.rows) {
                if (row.visible()) {
                    if (this.noneVisible()) {
                        this.noneVisible(false);
                    }
                    return;
                }
            }
            if (!this.noneVisible()) {
                this.noneVisible(true);
            }
        }
    }

    threadWidth() {
        const fullWidthPx = this.timelineModel.totalThreadWidth();
        const threadCount = this.rootModel.threadNames.length;
        return (fullWidthPx / threadCount) + 'px';
    }

    addEntry(testRunModel) {
        if (testRunModel.start.getTime() === this.timeStamp) {
            this.started.push(new LogViewTestRunModel(testRunModel));
        } else {
            this.ended.push(new LogViewTestRunModel(testRunModel));
        }
    }
}

module.exports.LogViewTimeStampModel = LogViewTimeStampModel;

