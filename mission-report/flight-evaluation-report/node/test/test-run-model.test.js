const {expect, describe, it} = require('@jest/globals');
const {FlightEvaluationReportModel} = require('../src/app');
const {parachute} = require('./data/parachute-test-data');
const {fuelTank} = require('./data/fueltank-test-data');
const {fuelTankCucumber} = require('./data/fueltank-cucumber-test-data');
const help = require('./test-helper');

for (const testData of [parachute, fuelTank, fuelTankCucumber]) {
    for (const scenario of testData.detailTextScenarios) {
        describe("TestRunModel.startString() using " + testData.name, function () {
            it("should return expected text in case of " + scenario.name + ".", function () {
                //given
                const root = help.createFlightEvaluationReportWithData(testData);
                const rawRun = testData.input.runs[scenario.index];
                const underTest = help.findRowByRawRun(root, rawRun).run;
                //when
                const actual = underTest.startString();
                //then
                expect(actual).toEqual(scenario.expectedStart);
            });
        });
        if (scenario.expectedEnd !== null) {
            describe("TestRunModel.endString() using " + testData.name, function () {
                it("should return expected text in case of " + scenario.name + ".", function () {
                    //given
                    const root = help.createFlightEvaluationReportWithData(testData);
                    const rawRun = testData.input.runs[scenario.index];
                    const underTest = help.findRowByRawRun(root, rawRun).run;
                    //when
                    const actual = underTest.endString();
                    //then
                    expect(actual).toEqual(scenario.expectedEnd);
                });
            });
        }
    }
    describe("TestRunModel.toggleHighlight() using " + testData.name, function () {
        it("should highlight when previously it wasn't.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const rawRun = testData.input.runs[6];
            const underTest = help.findRowByRawRun(root, rawRun);
            //when
            underTest.run.toggleHighlight();
            //then
            expect(underTest.run.highlight).toEqual(true);
            help.expectElementsHighlightedFor(root, underTest.run.launchId);
        });
        it("should remove highlight when previously it was.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const rawRun = testData.input.runs[6];
            const underTest = help.findRowByRawRun(root, rawRun);
            underTest.toggleSelected();
            //when
            underTest.run.toggleHighlight();
            //then
            expect(underTest.run.highlight).toEqual(false);
            help.expectElementsHighlightedFor(root, "none");
        });
    });
}
