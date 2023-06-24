const {expect, describe, it} = require('@jest/globals');
const {FlightEvaluationReportModel} = require('../src/app');
const help = require('./test-helper');
const {LogViewTimeStampModel} = require("../src/log-view-time-stamp-model");

// noinspection PointlessArithmeticExpressionJS
const expectedThreadTotalWidth = [
    null,
    20,
    40,
    60,
    60,
    120,
    120,
    120,
    120,
    240,
    240,
    240,
    240,
    240,
    240,
    240,
    240,
    320,
    320,
    320,
    320,
    320,
    320,
    320,
    320,
    320,
    320,
    320,
    320,
    320,
    320,
    320,
    320,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360,
    360
];

for (let t = 1; t <= 64; t++) {
    describe("LogViewTimelineModel.totalThreadWidth()", function () {
        it("should return expected width in case of " + t + " threads.", function () {
            //given
            const underTest = new FlightEvaluationReportModel();
            for (let i = 0; i < t; i++) {
                underTest._temporary.addThread({key: "Thread " + i, value: "Thread " + i});
            }
            //when
            const actual = underTest.logViewTimeline.totalThreadWidth();
            //then
            expect(actual).toEqual(expectedThreadTotalWidth[t]);
        });
    });
    describe("LogViewTimeStampModel.threadWidth()", function () {
        it("should return expected width in case of " + t + " threads.", function () {
            //given
            const root = new FlightEvaluationReportModel();
            for (let i = 0; i < t; i++) {
                root._temporary.addThread({key: "Thread " + i, value: "Thread " + i});
            }
            const underTest = new LogViewTimeStampModel(root.logViewTimeline, new Date());
            //when
            const actual = underTest.threadWidth();
            //then
            const expected = expectedThreadTotalWidth[t] / t;
            expect(actual).toEqual(expected + 'px');
        });
    });
    describe("LogViewTimelineModel.threadColumnCategoryCss()", function () {
        it("should return expected class name in case of " + t + " threads.", function () {
            //given
            const root = new FlightEvaluationReportModel();
            for (let i = 0; i < t; i++) {
                root._temporary.addThread({key: "Thread " + i, value: "Thread " + i});
            }
            const underTest = root.logViewTimeline;
            //when
            const actual = underTest.threadColumnCategoryCss();
            //then
            const expected = 'threads-t' + (Math.pow(2, Math.ceil(Math.log2(t))));
            expect(actual).toEqual(expected);
        });
    });
}
