const {expect, describe, it} = require('@jest/globals');
const {FlightEvaluationReportModel} = require('../src/app');
const {fuelTank} = require('./data/fueltank-test-data');
const {fuelTankCucumber} = require('./data/fueltank-cucumber-test-data');
const help = require('./test-helper');
const {DetailViewModel} = require("../src/detail-view-model");

describe("DetailViewModel.setRun(run) using fuelTank class data", function () {
    const testData = fuelTank;

    it("should not select anything when called with null run", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        const underTest = root.detailView;
        //when
        underTest.setRun(null);
        //then
        expect(underTest.classKey()).toEqual("");
        expect(underTest.className()).toEqual("");
        expect(underTest.classNameShort()).toEqual("");
        expect(underTest.methods().length).toEqual(0);
    });

    it("should select a class and find related method data when called with non-null run", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        const underTest = root.detailView;
        const runDetail = testData.selectedRunDetail;
        const run = root.runs[runDetail.index];
        //when
        underTest.setRun(run);
        //then
        help.expectDetailsMatch(underTest, runDetail);
    });

    it("should filter method data when called with active filtering", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        root.results[1].toggleExclude()();
        root.results[3].toggleExclude()();
        const underTest = root.detailView;
        const runDetail = testData.selectedRunDetailNoFailureNoSuppress;
        const run = root.runs[runDetail.index];
        //when
        underTest.setRun(run);
        //then
        help.expectDetailsMatch(underTest, runDetail);
    });

    it("should ignore filtering when called with active filtering and toggled filtering", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        root.results[1].toggleExclude()();
        root.results[3].toggleExclude()();
        const underTest = root.detailView;
        const runDetail = testData.selectedRunDetail;
        const run = root.runs[runDetail.index];
        //when
        underTest.setRun(run);
        underTest.toggleFiltering();
        //then
        help.expectDetailsMatch(underTest, runDetail);
    });
});

describe("DetailViewModel using fuelTank class data", function () {
    const testData = fuelTank;

    it("should return appropriate message for filtering toggle when filtering is active.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        const underTest = root.detailView;
        const runDetail = testData.selectedRunDetail;
        const run = root.runs[runDetail.index];
        underTest.setRun(run);
        //when
        const actual = underTest.filteringText();
        //then
        expect(actual).toEqual('Don\'t filter overview');
    });

    it("should return appropriate message for filtering toggle when filtering is not active.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        const underTest = root.detailView;
        const runDetail = testData.selectedRunDetail;
        const run = root.runs[runDetail.index];
        underTest.setRun(run);
        //when
        underTest.toggleFiltering();
        const actual = underTest.filteringText();
        //then
        expect(actual).toEqual('Show filtered overview');
    });
});

describe("DetailViewModel.isCollapsed(classKey, methodKey) using fuelTank class data", function () {
    const testData = fuelTank;

    it("should return true when countdown method is collapsed.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        const underTest = root.detailView;
        const runDetail = testData.selectedRunDetail;
        const run = root.runs[runDetail.index];
        underTest.setRun(run);
        const classKey = runDetail.classKey;
        const methodKey = runDetail.methods[0].methodKey;
        //when
        const actual = underTest.isCollapsed(classKey, methodKey);
        //then
        expect(actual).toBe(true);
    });

    it("should return true when mission method is collapsed.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        const underTest = root.detailView;
        const runDetail = testData.selectedRunDetail;
        const run = root.runs[runDetail.index];
        underTest.setRun(run);
        const classKey = runDetail.classKey;
        const methodKey = runDetail.methods[1].methodKey;
        //when
        const actual = underTest.isCollapsed(classKey, methodKey);
        //then
        expect(actual).toBe(true);
    });

    it("should return false when countdown method is expanded.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        const underTest = root.detailView;
        const runDetail = testData.selectedRunDetail;
        const run = root.runs[runDetail.index];
        underTest.setRun(run);
        const classKey = runDetail.classKey;
        const methodKey = runDetail.methods[0].methodKey;
        underTest.methods()[0].toggleMethod();
        //when
        const actual = underTest.isCollapsed(classKey, methodKey);
        //then
        expect(actual).toBe(false);
    });

    it("should return false when mission method is expanded.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        const underTest = root.detailView;
        const runDetail = testData.selectedRunDetail;
        const run = root.runs[runDetail.index];
        underTest.setRun(run);
        const classKey = runDetail.classKey;
        const methodKey = runDetail.methods[1].methodKey;
        underTest.methods()[1].toggleMethod();
        //when
        const actual = underTest.isCollapsed(classKey, methodKey);
        //then
        expect(actual).toBe(false);
    });

    it("should throw error when called with invalid classKey.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        const underTest = root.detailView;
        const runDetail = testData.selectedRunDetail;
        const run = root.runs[runDetail.index];
        underTest.setRun(run);
        const classKey = null;
        const methodKey = runDetail.methods[1].methodKey;
        //when
        expect(() => {
            underTest.isCollapsed(classKey, methodKey)
        }).toThrow('Class keys does not match: 1a51235d05c3 != null');
        //then + error
    });

    it("should return true when countdown method is expanded then collapsed again.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        const underTest = root.detailView;
        const runDetail = testData.selectedRunDetail;
        const run = root.runs[runDetail.index];
        underTest.setRun(run);
        const classKey = runDetail.classKey;
        const methodKey = runDetail.methods[0].methodKey;
        underTest.methods()[0].toggleMethod();
        underTest.methods()[0].toggleMethod();
        //when
        const actual = underTest.isCollapsed(classKey, methodKey);
        //then
        expect(actual).toBe(true);
    });

    it("should return true when mission method is expanded then collapsed again.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        const underTest = root.detailView;
        const runDetail = testData.selectedRunDetail;
        const run = root.runs[runDetail.index];
        underTest.setRun(run);
        const classKey = runDetail.classKey;
        const methodKey = runDetail.methods[1].methodKey;
        underTest.methods()[1].toggleMethod();
        underTest.methods()[1].toggleMethod();
        //when
        const actual = underTest.isCollapsed(classKey, methodKey);
        //then
        expect(actual).toBe(true);
    });
});


describe("DetailViewModel.methods.collapseExpandIcon() using fuelTank class data", function () {
    const testData = fuelTank;

    it("should return chevron right when method is collapsed.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        const detailView = root.detailView;
        const runDetail = testData.selectedRunDetail;
        const run = root.runs[runDetail.index];
        detailView.setRun(run);
        const underTest = detailView.methods()[0];
        //when
        const actual =  underTest.collapseExpandIcon()
        //then
        expect(actual).toContain("chevron-right");
    });

    it("should return chevron down when method is expanded.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        const detailView = root.detailView;
        const runDetail = testData.selectedRunDetail;
        const run = root.runs[runDetail.index];
        detailView.setRun(run);
        const underTest = detailView.methods()[0];
        underTest.toggleMethod();
        //when
        const actual = underTest.collapseExpandIcon()
        //then
        expect(actual).toContain("chevron-down");
    });
});
