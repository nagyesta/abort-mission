const { expect, describe, it } = require('@jest/globals');
const {FlightEvaluationReportModel} = require('../src/app');
const {parachute} = require('./data/parachute-test-data');
const {fuelTank} = require('./data/fueltank-test-data');
const {fuelTankCucumber} = require('./data/fueltank-cucumber-test-data');
const help = require('./test-helper');

for (const testData of [parachute, fuelTank, fuelTankCucumber]) {
    describe("FlightEvaluationReportModel initData() using " + testData.name, function () {
        it("should parse and convert valid JSON input", function () {
            //given
            const input = testData.input;
            const underTest = new FlightEvaluationReportModel();
            //when
            underTest.initData(input);
            //then
            help.expectImportSuccessful(underTest, testData);
        });
    });
}

describe("FlightEvaluationReportModel testStart() and testEnd()", function () {
    it("should return placeholder if no runs are present", function () {
        //given
        const underTest = new FlightEvaluationReportModel();
        //when
        const actualStart = underTest.testStart();
        const actualEnd = underTest.testEnd();
        //then
        expect(actualStart).toEqual("-");
        expect(actualEnd).toEqual("-");
    });
});
