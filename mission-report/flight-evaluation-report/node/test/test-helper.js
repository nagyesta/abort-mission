const {expect, beforeAll} = require('@jest/globals');
const {FlightEvaluationReportModel} = require("../src/app");

beforeAll(() => {
    //turn off console output
    console.log = jest.fn();
    console.debug = jest.fn();
    console.info = jest.fn();
});

function expectCollectionsMatch(actualCollection, expectedCollection, valueFunction) {
    expect(actualCollection.length).toEqual(expectedCollection.length);
    for (let i = 0; i < expectedCollection.length; i++) {
        const expected = expectedCollection[i];
        const actual = actualCollection[i];
        expect(actual.key).toEqual(expected);
        expect(actual.value).toEqual(valueFunction(expected));
    }
}

function expectTimeStampsMatch(actualCollection, expectedCollection, rowCountFunction) {
    expect(actualCollection.length).toEqual(expectedCollection.length);
    for (let i = 0; i < expectedCollection.length; i++) {
        const expected = expectedCollection[i];
        const actual = actualCollection[i];
        expect(actual.timeStamp).toEqual(expected);
        expect(actual.rows.length).toEqual(rowCountFunction(expected));
    }
}

function runCounterFunction(input) {
    return function (expected) {
        return input.runs.filter(run => run.start === expected || run.end === expected).length;
    }
}

function expectStartAndEndDateMatches(actualModel, inputData) {
    expect(actualModel.testStart())
            .toEqual(new Date(inputData.timestamps[0])
                    .toISOString().replaceAll("T", " ").replaceAll("Z", ""));
    expect(actualModel.testEnd())
            .toEqual(new Date(inputData.timestamps[inputData.timestamps.length - 1])
                    .toISOString().replaceAll("T", " ").replaceAll("Z", ""));
    expect(actualModel.getTotalRunTime())
            .toEqual(inputData.totalRunTime)
}


function expectImportSuccessful(actualModel, testData) {
    const input = testData.input;
    expectCollectionsMatch(actualModel.classNames, testData.classNames, expected => input.classNames[expected])
    expectCollectionsMatch(actualModel.methodNames, testData.methodNames, expected => input.methodNames[expected]);
    expectCollectionsMatch(actualModel.matchers, testData.matcherNames, expected => input.matchers[expected]);
    expectCollectionsMatch(actualModel.threadNames, testData.threadNames, expected => expected);
    expectTimeStampsMatch(actualModel.logViewTimeline.logEntryMultiMap, testData.timestamps, runCounterFunction(input));
    expectStartAndEndDateMatches(actualModel, testData);
}

function expectVisibilityIsSet(visibilityPredicate, row, noneVisible) {
    if (visibilityPredicate(row)) {
        expect(row.visible()).toBe(true);
        expect(row.rowCss()).not.toContain("filtered")
        noneVisible = false;
    } else {
        expect(row.visible()).toBe(false);
        expect(row.rowCss()).toContain("filtered")
    }
    return noneVisible;
}

function expectFilterSelection(rule, expectedInclude, expectedExclude) {
    expect(rule.include()).toBe(expectedInclude);
    expect(rule.exclude()).toBe(expectedExclude);
}

function expectFilteredRules(actualModel, includeIf, excludeIf) {
    for (const rule of actualModel.allRules) {
        expectFilterSelection(rule, includeIf(rule), excludeIf(rule));
    }
}

function expectIncludeRulesMatch(actualModel, expectedRules) {
    for (let i = 0; i < actualModel.filter.allIncludes().length; i++){
        const rule = actualModel.filter.allIncludes()[i];
        expect(rule.valuePrefixShort()).toEqual(expectedRules[i].prefix);
        expect(rule.valueMain()).toEqual(expectedRules[i].main);
        expect(rule.cssEntityTypeName()).toEqual(expectedRules[i].css);
    }
}

function createFlightEvaluationReportWithData(testData) {
    const input = testData.input;
    const underTest = new FlightEvaluationReportModel();
    underTest.initData(input);
    return underTest;
}

function expectFilteredRunsAreHidden(actualModel, visibilityPredicate) {
    actualModel.logViewTimeline.logEntryMultiMap.forEach(value => {
        let noneVisible = true;
        value.rows.forEach(row => {
            noneVisible = expectVisibilityIsSet(visibilityPredicate, row, noneVisible);
        });
        expect(value.noneVisible()).toEqual(noneVisible);
    });
}

function expectRunsOutOfRangeAreMarked(actualModel, start, end) {
    actualModel.logViewTimeline.logEntryMultiMap.forEach(value => {
        if (!value.noneVisible()) {
            for (let i = 0; i < value.rows.length; i++) {
                const row = value.rows[i];
                const hasVisibleTimeStamp = i === 0 && row.visible();
                if (hasVisibleTimeStamp && (value.timeStamp < start || value.timeStamp > end)) {
                    expect(row.rowCss()).toContain("filter-out-of-range");
                } else if (row.visible()) {
                    const endingRowAfterFilterEnd = !row.isStart && (row.run.start <= end && row.run.end > end);
                    const startingRowBeforeFilterStart = row.isStart && (row.run.start < start && row.run.end >= start);
                    if (endingRowAfterFilterEnd || startingRowBeforeFilterStart) {
                        expect(row.rowCss()).toContain("filter-out-of-range");
                    }
                }
            }
        }
    });
}

function expectLastStartOfTimeStampIsDecorated(actualModel) {
    actualModel.logViewTimeline.logEntryMultiMap.forEach(value => {
        value.rows.forEach(row => {
            if (row.lastStarting) {
                expect(row.categoryBoundaryCss()).toContain("border-bottom");
            }
        });
    });
}

function expectLastEndOfTimeStampIsDecorated(actualModel) {
    actualModel.logViewTimeline.logEntryMultiMap.forEach(value => {
        value.rows.forEach(row => {
            if (row.lastEnding) {
                expect(row.categoryBoundaryCss()).toContain("soft-border-bottom");
            }
        });
    });
}

function expectNumberOfVisibleRowsAndRunsIs(actualModel, expectedRows, expectedRuns) {
    let actualRows = 0;
    let actualRuns = 0;
    actualModel.logViewTimeline.logEntryMultiMap.forEach(timeStamp => {
        timeStamp.rows.forEach(row => {
            if (row.visible()) {
                actualRows++;
                if (row.isStart) {
                    actualRuns++;
                }
            }
        });
    });
    expect(actualRows).toEqual(expectedRows);
    expect(actualRuns).toEqual(expectedRuns);
}

function findRowByRawRun(actualModel, rawRun) {
    return actualModel.logViewTimeline.logEntryMultiMap
            .find(t => t.timeStamp === rawRun.start)
            .rows
            .find(r => r.isStart && r.run.launchId === rawRun.launchId);
}

function expectElementsHighlightedFor(actualModel, expectedHighlightedLaunchId) {
    actualModel.logViewTimeline.logEntryMultiMap.forEach(value => {
        value.rows.forEach(row => {
            expect(row.selected()).toEqual(row.run.launchId === expectedHighlightedLaunchId);
            row.threads.forEach(thread => {
                if (thread.run != null) {
                    expect(thread.selected()).toEqual(thread.run.launchId === expectedHighlightedLaunchId);
                }
            });
        });
    });
}

function expectMethodDetailsMatch(actualMethods, expectedMethods) {
    expect(actualMethods.length).toEqual(expectedMethods.length);
    for (let i = 0; i < expectedMethods.length; i++) {
        expect(actualMethods[i].methodKey).toEqual(expectedMethods[i].methodKey);
        expect(actualMethods[i].methodName).toEqual(expectedMethods[i].methodName);
        if (actualMethods[i].countdown) {
            expect(actualMethods[i].methodEntity()).toBeNull();
        } else {
            expect(actualMethods[i].methodEntity().key).toEqual(expectedMethods[i].methodKey);
        }
        expect(actualMethods[i].countdown).toEqual(expectedMethods[i].countdown);
        expect(actualMethods[i].missionStageEntity().key).toEqual(expectedMethods[i].stageKey);
        const selectedData = actualMethods[i].selectedData();
        expect(selectedData.runs).toEqual(expectedMethods[i].runs);
        expect(selectedData.start).toEqual(expectedMethods[i].start);
        if (selectedData.runs > 0) {
            const timeStamp = /2023-[0-2][0-9]-[0-3][0-9] [0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9]{3}/;
            expect(selectedData.startTimeAsString()).toMatch(timeStamp);
            expect(selectedData.endTimeAsString()).toMatch(timeStamp);
        } else {
            expect(selectedData.startTimeAsString()).toEqual("N/A");
            expect(selectedData.endTimeAsString()).toEqual("N/A");
        }
        expect(selectedData.end).toEqual(expectedMethods[i].end);
        expect(selectedData.minimum()).toEqual(expectedMethods[i].min);
        expect(selectedData.maximum()).toEqual(expectedMethods[i].max);
        expect(selectedData.average()).toEqual(expectedMethods[i].avg);
        expect(selectedData.totalSum()).toEqual(expectedMethods[i].sum);
        expect(selectedData.success).toEqual(expectedMethods[i].success);
        expect(selectedData.failure).toEqual(expectedMethods[i].failure);
        expect(selectedData.aborted).toEqual(expectedMethods[i].aborted);
        expect(selectedData.suppressed).toEqual(expectedMethods[i].suppressed);
        expect(actualMethods[i].worstResult()).toEqual(expectedMethods[i].worstResult);
        expect(selectedData.visible()).toEqual(expectedMethods[i].visible);
        expect(selectedData.canFilterStartTime()).toEqual(expectedMethods[i].canFilterStartTime);
        expect(selectedData.canFilterEndTime()).toEqual(expectedMethods[i].canFilterEndTime);
    }
}

function expectDetailsMatch(actual, expected) {
    expect(actual.classKey()).toEqual(expected.classKey);
    expect(actual.className()).toEqual(expected.className);
    expect(actual.classNameShort()).toEqual(expected.classNameShort);
    expect(actual.worstResult()).toEqual(expected.worstResult);
    const actualMethods = actual.methods();
    const expectedMethods = expected.methods;
    expectMethodDetailsMatch(actualMethods, expectedMethods);
}

module.exports = {
    expectCollectionsMatch: expectCollectionsMatch,
    expectTimeStampsMatch: expectTimeStampsMatch,
    expectStartAndEndDateMatches: expectStartAndEndDateMatches,
    runCounterFunction: runCounterFunction,
    expectImportSuccessful: expectImportSuccessful,
    expectVisibilityIsSet: expectVisibilityIsSet,
    expectFilterSelection: expectFilterSelection,
    expectFilteredRules: expectFilteredRules,
    expectIncludeRulesMatch: expectIncludeRulesMatch,
    expectFilteredRunsAreHidden: expectFilteredRunsAreHidden,
    expectRunsOutOfRangeAreMarked: expectRunsOutOfRangeAreMarked,
    expectLastStartOfTimeStampIsDecorated: expectLastStartOfTimeStampIsDecorated,
    expectLastEndOfTimeStampIsDecorated: expectLastEndOfTimeStampIsDecorated,
    expectNumberOfVisibleRowsAndRunsIs: expectNumberOfVisibleRowsAndRunsIs,
    createFlightEvaluationReportWithData: createFlightEvaluationReportWithData,
    findRowByRawRun: findRowByRawRun,
    expectElementsHighlightedFor: expectElementsHighlightedFor,
    expectMethodDetailsMatch: expectMethodDetailsMatch,
    expectDetailsMatch: expectDetailsMatch
}
