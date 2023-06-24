const { expect, describe, it } = require('@jest/globals');
const {FlightEvaluationReportModel} = require('../src/app');
const {parachute} = require('./data/parachute-test-data');
const {fuelTank} = require('./data/fueltank-test-data');
const {fuelTankCucumber} = require('./data/fueltank-cucumber-test-data');
const help = require('./test-helper');

for (const testData of [parachute, fuelTank, fuelTankCucumber]) {
    for (const scenario of testData.timeFilterScenarios) {
        describe("Filter based on times using " + testData.name, function () {
            it("should filter matching " + scenario.name + ".", function () {
                //given
                const root = help.createFlightEvaluationReportWithData(testData);
                const underTest = root.filter;
                //when
                underTest.setStartTime(new Date(scenario.start))();
                underTest.setEndTime(new Date(scenario.end))();
                //then
                help.expectFilteredRules(root, () => false, () => false);
                help.expectFilteredRunsAreHidden(root, r => r.run.start <= scenario.end && r.run.end >= scenario.start);
                help.expectRunsOutOfRangeAreMarked(root, scenario.start, scenario.end);
                help.expectLastStartOfTimeStampIsDecorated(root);
                help.expectLastEndOfTimeStampIsDecorated(root);
                help.expectNumberOfVisibleRowsAndRunsIs(root, scenario.expectedVisibleRows, scenario.expectedVisibleRuns);
                expect(root.filter.isFiltered()).toBe(true);
            });
        });
    }
}
