const {expect, describe, it} = require('@jest/globals');
const {fuelTank} = require('./data/fueltank-test-data');
const help = require('./test-helper');

describe("SummaryViewOutcomeModel.resultEntity() using fuelTank class data", function () {
    const testData = fuelTank;

    it("should return matching result entity.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(testData);
        const underTest = root.summaryView.results[0];
        //when
        const actual = underTest.resultEntity();
        //then
        expect(actual.key).toEqual("SUCCESS");
        expect(actual.value).toEqual("Success");
    });
});
