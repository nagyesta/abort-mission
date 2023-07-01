const {expect, describe, it} = require('@jest/globals');
const {formatDuration} = require('../src/stat-model');

describe("formatDuration()", function () {
    it("should return milliseconds when called with less than a second.", function () {
        //given
        const input = 987;
        //when
        const actual = formatDuration(input);
        //then
        expect(actual).toEqual("987<small>ms</small>");
    });
    it("should return decimals with seconds when called with seconds.", function () {
        //given
        const input = 1487.6;
        //when
        const actual = formatDuration(input);
        //then
        expect(actual).toEqual("1.5<small>s</small>");
    });
    it("should return no decimals of seconds when called with minutes.", function () {
        //given
        const input = 61487.6;
        //when
        const actual = formatDuration(input);
        //then
        expect(actual).toEqual("1<small>m</small>1<small>s</small>");
    });
    it("should return decimals of seconds when called with minutes and explicit decimal parameter.", function () {
        //given
        const input = 61487.6;
        //when
        const actual = formatDuration(input, 1);
        //then
        expect(actual).toEqual("1<small>m</small>1.5<small>s</small>");
    });
});
