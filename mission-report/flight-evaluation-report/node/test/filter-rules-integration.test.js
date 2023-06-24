const {expect, describe, it} = require('@jest/globals');
const {FlightEvaluationReportModel} = require('../src/app');
const {parachute} = require('./data/parachute-test-data');
const {fuelTank} = require('./data/fueltank-test-data');
const {fuelTankCucumber} = require('./data/fueltank-cucumber-test-data');
const help = require('./test-helper');
const {KeyValueEntityModel} = require("../src/key-value-entity-model");

for (const testData of [parachute, fuelTank, fuelTankCucumber]) {
    describe("Filter with rules using " + testData.name, function () {

        it("should filter based on className include rule.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.classNames[0];
            //when
            underTest.toggleInclude()();
            //then
            help.expectFilteredRules(root, r => r === underTest, () => false);
            help.expectIncludeRulesMatch(root, testData.filters.classNames);
            help.expectFilteredRunsAreHidden(root, r => r.run.classKey === underTest.key);
            expect(root.filter.isFiltered()).toBe(true);
        });

        it("should filter based on className exclude rule.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.classNames[0];
            //when
            underTest.toggleExclude()();
            //then
            help.expectFilteredRules(root, () => false, r => r === underTest);
            help.expectFilteredRunsAreHidden(root, r => r.run.classKey !== underTest.key);
            expect(root.filter.isFiltered()).toBe(true);
        });

        it("should filter based on methodName include rule.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.methodNames[0];
            //when
            underTest.toggleInclude()();
            //then
            help.expectFilteredRules(root, r => r === underTest, () => false);
            help.expectIncludeRulesMatch(root, testData.filters.methodNames);
            help.expectFilteredRunsAreHidden(root, r => r.run.methodKey === underTest.key);
            expect(root.filter.isFiltered()).toBe(true);
        });

        it("should filter based on methodName exclude rule.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.methodNames[0];
            //when
            underTest.toggleExclude()();
            //then
            help.expectFilteredRules(root, () => false, r => r === underTest);
            help.expectFilteredRunsAreHidden(root, r => r.run.methodKey !== underTest.key || r.run.countdown);
            expect(root.filter.isFiltered()).toBe(true);
        });

        it("should filter based on threadName include rule.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.threadNames[0];
            //when
            underTest.toggleInclude()();
            //then
            help.expectFilteredRules(root, r => r === underTest, () => false);
            help.expectIncludeRulesMatch(root, testData.filters.threadNames);
            help.expectFilteredRunsAreHidden(root, r => r.run.threadName === underTest.key);
            expect(root.filter.isFiltered()).toBe(true);
        });

        it("should filter based on threadName exclude rule.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.threadNames[0];
            //when
            underTest.toggleExclude()();
            //then
            help.expectFilteredRules(root, () => false, r => r === underTest);
            help.expectFilteredRunsAreHidden(root, r => r.run.threadName !== underTest.key);
            expect(root.filter.isFiltered()).toBe(true);
        });

        it("should filter based on result include rule.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.results[0];
            //when
            underTest.toggleInclude()();
            //then
            help.expectFilteredRules(root, r => r === underTest, () => false);
            help.expectIncludeRulesMatch(root, testData.filters.resultNames);
            help.expectFilteredRunsAreHidden(root, r => r.run.result === "SUCCESS");
            expect(root.filter.isFiltered()).toBe(true);
        });

        it("should filter based on result exclude rule.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.results[0];
            //when
            underTest.toggleExclude()();
            //then
            help.expectFilteredRules(root, () => false, r => r === underTest);
            help.expectFilteredRunsAreHidden(root, r => r.run.result !== "SUCCESS");
            expect(root.filter.isFiltered()).toBe(true);
        });

        it("should filter based on countdown include rule.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.missionStage[0];
            //when
            underTest.toggleInclude()();
            //then
            help.expectFilteredRules(root, r => r === underTest, () => false);
            help.expectIncludeRulesMatch(root, testData.filters.stageNames);
            help.expectFilteredRunsAreHidden(root, r => r.run.countdown);
            expect(root.filter.isFiltered()).toBe(true);
        });

        it("should filter based on countdown exclude rule.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.missionStage[0];
            //when
            underTest.toggleExclude()();
            //then
            help.expectFilteredRules(root, () => false, r => r === underTest);
            help.expectFilteredRunsAreHidden(root, r => !r.run.countdown);
            expect(root.filter.isFiltered()).toBe(true);
        });

        it("should filter based on matcher include rule.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.matchers[0];
            //when
            underTest.toggleInclude()();
            //then
            help.expectFilteredRules(root, r => r === underTest, () => false);
            help.expectIncludeRulesMatch(root, testData.filters.matchers);
            help.expectFilteredRunsAreHidden(root, r => r.run.matcherKeys.includes(underTest.key));
            expect(root.filter.isFiltered()).toBe(true);
        });

        it("should filter based on matcher exclude rule.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.matchers[0];
            //when
            underTest.toggleExclude()();
            //then
            help.expectFilteredRules(root, () => false, r => r === underTest);
            help.expectFilteredRunsAreHidden(root, r => !r.run.matcherKeys.includes(underTest.key));
            expect(root.filter.isFiltered()).toBe(true);
        });
    });

    describe("Filter rule selection using " + testData.name, function () {
        it("should deselect include when selecting exclude for the same rule.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.matchers[0];
            underTest.toggleExclude()();
            //when
            underTest.toggleInclude()();
            //then
            help.expectFilteredRules(root, r => r === underTest, () => false);
            help.expectFilteredRunsAreHidden(root, r => r.run.matcherKeys.includes(underTest.key));
        });

        it("should deselect exclude when selecting include for the same rule.", function () {
            //given
            const root = help.createFlightEvaluationReportWithData(testData);
            const underTest = root.matchers[0];
            underTest.toggleInclude()();
            //when
            underTest.toggleExclude()();
            //then
            help.expectFilteredRules(root, () => false, r => r === underTest);
            help.expectFilteredRunsAreHidden(root, r => !r.run.matcherKeys.includes(underTest.key));
            expect(root.filter.isFiltered()).toBe(true);
        });

        if (testData !== fuelTankCucumber) {
            it("should select all matching rules using class name filter and include toggle.", function () {
                //given
                const root = help.createFlightEvaluationReportWithData(testData);
                const startFilter = "com.github";
                root.filter.input(startFilter);
                const underTest = new KeyValueEntityModel(root, "CLASS", startFilter, startFilter, false, false, "CLASS");
                //when
                underTest.toggleInclude()();
                help.expectFilteredRules(root, r => r.entityType === "CLASS", () => false);
                root.classNames[1].toggleExclude()();
                //then
                help.expectFilteredRules(root, r => r === root.classNames[0], r => r === root.classNames[1]);
                help.expectFilteredRunsAreHidden(root, r => r.run.classKey === root.classNames[0].key);
                expect(root.filter.isFiltered()).toBe(true);
            });

            it("should deselect all matching rules using class name filter and exclude toggle.", function () {
                //given
                const root = help.createFlightEvaluationReportWithData(testData);
                const startFilter = "com.github";
                root.filter.input(startFilter);
                const underTest = new KeyValueEntityModel(root, "CLASS", startFilter, startFilter, false, false, "CLASS");
                root.classNames[0].toggleInclude()();
                root.classNames[1].toggleInclude()();
                root.results[0].toggleExclude()();
                help.expectFilteredRules(root, r => r.entityType === "CLASS", r => r === root.results[0]);
                //when
                underTest.toggleExclude()();
                //then
                help.expectFilteredRules(root, () => false, r => r.entityType === "CLASS" || r === root.results[0]);
                help.expectFilteredRunsAreHidden(root, r => false);
                expect(root.filter.isFiltered()).toBe(true);
            });
        }
    });
}
