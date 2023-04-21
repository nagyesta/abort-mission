const {expect, describe, it} = require('@jest/globals');
const {FlightEvaluationReportModel} = require('../src/app');
const {parachute} = require('./data/parachute-test-data');
const {fuelTank} = require('./data/fueltank-test-data');
const {fuelTankCucumber} = require('./data/fueltank-cucumber-test-data');
const help = require('./test-helper');
const {KeyValueEntityModel} = require("../src/key-value-entity-model");

for (const testData of [parachute, fuelTank, fuelTankCucumber]) {
    describe("Filter.clear() using " + testData.name, function () {

        it("should remove all include filter rules.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            root.classNames[0].toggleInclude()();
            const underTest = root.filter;
            //when
            underTest.clear();
            //then
            help.expectFilteredRules(root, () => false, () => false);
            help.expectFilteredRunsAreHidden(root, () => true);
        });

        it("should remove all exclude filter rules.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            root.classNames[0].toggleExclude()();
            const underTest = root.filter;
            //when
            underTest.clear();
            //then
            help.expectFilteredRules(root, () => false, () => false);
            help.expectFilteredRunsAreHidden(root, () => true);
        });

        it("should reset time ranges.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.filter;
            underTest.setStartTime(new Date(testData.timeFilterScenarios[0].start))();
            underTest.setEndTime(new Date(testData.timeFilterScenarios[0].end))();
            expect(underTest.startTime().getTime()).not.toEqual(root.getFirstStartDate().getTime());
            expect(underTest.endTime().getTime()).not.toEqual(root.getLastEndDate().getTime());
            //when
            underTest.clear();
            //then
            help.expectFilteredRules(root, () => false, () => false);
            help.expectFilteredRunsAreHidden(root, () => true);
        });

    });

    describe("Filter.startTimeAsString() using " + testData.name, function () {
        it("should convert to string.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.filter;
            //when
            const actual = underTest.startTimeAsString();
            //then
            const expected = new Date(underTest.startTime())
                    .toISOString()
                    .replaceAll("T", " ")
                    .replaceAll("Z", "");
            expect(actual).toEqual(expected);
        });
    });

    describe("Filter.endTimeAsString() using " + testData.name, function () {
        it("should convert to string.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.filter;
            //when
            const actual = underTest.endTimeAsString();
            //then
            const expected = new Date(underTest.endTime())
                    .toISOString()
                    .replaceAll("T", " ")
                    .replaceAll("Z", "");
            expect(actual).toEqual(expected);
        });
    });

    describe("Filter.invalidRange() using " + testData.name, function () {
        it("should return true is start is after end.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.filter;
            const original = underTest.invalidRange();
            underTest.setStartTime(new Date(underTest.endTime().getTime() + 1))();
            //when
            const actual = underTest.invalidRange();
            //then
            expect(original).toEqual(false);
            expect(actual).toEqual(true);
        });
    });

    describe("Filter.resetStartTime() using " + testData.name, function () {
        it("should reset start time.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.filter;
            underTest.setStartTime(new Date(underTest.endTime().getTime() + 1))();
            //when
            underTest.resetStartTime();
            //then
            expect(underTest.startTime().getTime()).toEqual(root.getFirstStartDate().getTime());
        });
    });

    describe("Filter.resetEndTime() using " + testData.name, function () {
        it("should reset end time.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.filter;
            underTest.setEndTime(new Date(underTest.endTime().getTime() - 1))();
            //when
            underTest.resetEndTime();
            //then
            expect(underTest.endTime().getTime()).toEqual(root.getLastEndDate().getTime());
        });
    });

    describe("Filter.setRange() using " + testData.name, function () {
        it("should set start and end time as a range.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.filter;
            const time = root.getFirstStartDate().getTime();
            const start = time - 1000;
            const end = time + 1000;
            //when
            underTest.setRange(new Date(time), 1)();
            //then
            expect(underTest.startTime().getTime()).toEqual(start);
            expect(underTest.endTime().getTime()).toEqual(end);
        });
    });
}

describe("Filter.placeholder() using fuelTank", function () {
    it("should use class specific placeholder.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(fuelTank);
        const underTest = root.filter;
        //when
        const actual = underTest.placeholder();
        //then
        expect(actual).toContain("class");
        expect(actual).toContain("method");
        expect(actual).not.toContain("feature");
        expect(actual).not.toContain("scenario");
    });
});

describe("Filter.placeholder() using fuelTankCucumber", function () {
    it("should use class specific placeholder.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(fuelTankCucumber);
        const underTest = root.filter;
        //when
        const actual = underTest.placeholder();
        //then
        expect(actual).toContain("feature");
        expect(actual).toContain("scenario");
        expect(actual).not.toContain("class");
        expect(actual).not.toContain("method");
    });
});

describe("Filter.selectAllMatching(e, m, i) using parachute", function () {
    it("should select matching rules with included selection.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(parachute);
        const underTest = root.filter;
        underTest.input("ParachuteTestContext");
        //when
        underTest.selectAllMatching("CLASS", "end", true)();
        //then
        help.expectFilteredRules(root, r => r === root.classNames[0], () => false);
    });
    it("should select all matching rules with excluded selection.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(parachute);
        const underTest = root.filter;
        underTest.input("com.github.nagyesta.abortmission.booster.jupiter.ParachuteTestContextP");
        //when
        underTest.selectAllMatching("CLASS", "start", false)();
        //then
        help.expectFilteredRules(root, () => false, r => r === root.classNames[1]);
    });
});

describe("Filter.matchingOptions() using parachute", function () {
    it("should add time based filtering if matching full format.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(parachute);
        const underTest = root.filter;
        underTest.input("2023-04-23 19:29:06.535");
        //when
        const actual = underTest.matchingOptions();
        //then
        expect(actual.length).toEqual(1);
        expect(actual[0].entityType).toEqual("TIME");
        expect(actual[0].value).toEqual("2023-04-23 19:29:06.535");
    });

    it("should add time based filtering if matching date and time without milliseconds.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(parachute);
        const underTest = root.filter;
        underTest.input("2023-04-23 19:29:06");
        //when
        const actual = underTest.matchingOptions();
        //then
        expect(actual.length).toEqual(1);
        expect(actual[0].entityType).toEqual("TIME");
        expect(actual[0].value).toEqual("2023-04-23 19:29:06.000");
    });

    it("should add time based filtering if matching full time.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(parachute);
        const underTest = root.filter;
        underTest.input("19:29:06.535");
        //when
        const actual = underTest.matchingOptions();
        //then
        expect(actual.length).toEqual(1);
        expect(actual[0].entityType).toEqual("TIME");
        expect(actual[0].value).toEqual("2023-04-23 19:29:06.535");
    });

    it("should add time based filtering if matching time without milliseconds.", function () {
        //given
        const root = help.createFlightEvaluationReportWithData(parachute);
        const underTest = root.filter;
        underTest.input("19:29:06");
        //when
        const actual = underTest.matchingOptions();
        //then
        expect(actual.length).toEqual(1);
        expect(actual[0].entityType).toEqual("TIME");
        expect(actual[0].value).toEqual("2023-04-23 19:29:06.000");
    });

    it('should not add time based filtering if not matching.', function () {
        //given
        const root = help.createFlightEvaluationReportWithData(parachute);
        const underTest = root.filter;
        underTest.input("2023-04-23 19:29:0");
        //when
        const actual = underTest.matchingOptions();
        //then
        expect(actual.length).toEqual(0);
    });

    it('should add shortcuts when found matching class name.', function () {
        //given
        const root = help.createFlightEvaluationReportWithData(parachute);
        const underTest = root.filter;
        underTest.input("PerClass");
        //when
        const actual = underTest.matchingOptions();
        //then
        expect(actual.length).toEqual(4);
        expect(actual[0].entityType).toEqual("CLASS");
        expect(actual[0].value).toEqual("Containing PerClass");
        expect(actual[1].entityType).toEqual("CLASS");
        expect(actual[1].value).toEqual("Ending with PerClass");
    });

    it('should add shortcuts when found matching method name.', function () {
       //given
       const root = help.createFlightEvaluationReportWithData(parachute);
       const underTest = root.filter;
       underTest.input("testParachuteShould");
       //when
       const actual = underTest.matchingOptions();
       //then
       expect(actual.length).toEqual(3);
       expect(actual[0].entityType).toEqual("METHOD");
       expect(actual[0].value).toEqual("Starting with testParachuteShould");
       expect(actual[1].entityType).toEqual("METHOD");
       expect(actual[1].value).toEqual("Containing testParachuteShould");
    });

    it('should not add shortcuts when input is too short.', function () {
       //given
       const root = help.createFlightEvaluationReportWithData(parachute);
       const underTest = root.filter;
       underTest.input("ss");
       //when
       const actual = underTest.matchingOptions();
       //then
       expect(actual.length).toEqual(7);
       expect(actual[0].entityType).toEqual("RESULT");
    });
});
