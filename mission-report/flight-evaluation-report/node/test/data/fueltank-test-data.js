const matcherNames = ['2c87129446ff', '23e7f11fd139'];
const classNames = ['1a51235d05c3', '12d8e6927583'];
const methodNames = ['2ffc91fc2ecf', '1ffc91fc2ecf'];
const threadNames = ['Test worker', 'Test worker 2'];
const timeFilterScenarios = [
    {start: 1682278146496, end: 1682278146496, expectedVisibleRows: 4, expectedVisibleRuns: 2, name: "single millisecond"},
    {start: 1682278146496, end: 1682278146502, expectedVisibleRows: 8, expectedVisibleRuns: 4, name: "range with exact timestamps"},
    {start: 1682278146422, end: 1682278146501, expectedVisibleRows: 14, expectedVisibleRuns: 8, name: "range with arbitrary timestamps"}
];
const selectedRunDetail = {
    index: 0,
    classKey: "1a51235d05c3",
    className: "com.github.nagyesta.abortmission.booster.jupiter.FuelTankTestContextPerClass",
    classNameShort: "co.gi.na.ab.bo.ju.FuelTankTestContextPerClass",
    worstResult: "failure",
    methods: [
        {
            methodKey: null,
            countdown: true,
            stageKey: 'c',
            methodName: "Countdown",
            runs: 1,
            start: 1682278146281,
            end: 1682278146335,
            min: "54<small>ms</small>",
            max: "54<small>ms</small>",
            avg: "54<small>ms</small>",
            sum: "54<small>ms</small>",
            success: 1,
            failure: 0,
            aborted: 0,
            suppressed: 0,
            worstResult: "success",
            visible: true,
            canFilterStartTime: false,
            canFilterEndTime: true
        },
        {
            methodKey: "2ffc91fc2ecf",
            countdown: false,
            stageKey: 'm',
            methodName: "testFuelTankShouldFillWhenCalled_2",
            runs: 1,
            start: 1682278146350,
            end: 1682278146423,
            min: "73<small>ms</small>",
            max: "73<small>ms</small>",
            avg: "73<small>ms</small>",
            sum: "73<small>ms</small>",
            success: 0,
            failure: 0,
            aborted: 0,
            suppressed: 1,
            worstResult: "suppressed",
            visible: true,
            canFilterStartTime: true,
            canFilterEndTime: true
        },
        {
            methodKey: "1ffc91fc2ecf",
            countdown: false,
            stageKey: 'm',
            methodName: "testFuelTankShouldFillWhenCalled",
            runs: 4,
            start: 1682278146429,
            end: 1682278146440,
            min: "0<small>ms</small>",
            max: "3<small>ms</small>",
            avg: "1<small>ms</small>",
            sum: "5<small>ms</small>",
            success: 1,
            failure: 1,
            aborted: 2,
            suppressed: 0,
            worstResult: "failure",
            visible: true,
            canFilterStartTime: true,
            canFilterEndTime: true
        }
    ],
};
const selectedRunDetailNoFailureNoSuppress = {
    index: 0,
    classKey: "1a51235d05c3",
    className: "com.github.nagyesta.abortmission.booster.jupiter.FuelTankTestContextPerClass",
    classNameShort: "co.gi.na.ab.bo.ju.FuelTankTestContextPerClass",
    worstResult: "aborted",
    methods: [
        {
            methodKey: null,
            countdown: true,
            stageKey: 'c',
            methodName: "Countdown",
            runs: 1,
            start: 1682278146281,
            end: 1682278146335,
            min: "54<small>ms</small>",
            max: "54<small>ms</small>",
            avg: "54<small>ms</small>",
            sum: "54<small>ms</small>",
            success: 1,
            failure: 0,
            aborted: 0,
            suppressed: 0,
            worstResult: "success",
            visible: true,
            canFilterStartTime: false,
            canFilterEndTime: true
        },
        {
            methodKey: "2ffc91fc2ecf",
            countdown: false,
            stageKey: 'm',
            methodName: "testFuelTankShouldFillWhenCalled_2",
            runs: 0,
            start: null,
            end: null,
            min: "N/A",
            max: "N/A",
            avg: "N/A",
            sum: "0<small>ms</small>",
            success: 0,
            failure: 0,
            aborted: 0,
            suppressed: 0,
            worstResult: "empty",
            visible: false,
            canFilterStartTime: false,
            canFilterEndTime: false
        },
        {
            methodKey: "1ffc91fc2ecf",
            countdown: false,
            stageKey: 'm',
            methodName: "testFuelTankShouldFillWhenCalled",
            runs: 3,
            start: 1682278146429,
            end: 1682278146440,
            min: "0<small>ms</small>",
            max: "3<small>ms</small>",
            avg: "1<small>ms</small>",
            sum: "3<small>ms</small>",
            success: 1,
            failure: 0,
            aborted: 2,
            suppressed: 0,
            worstResult: "aborted",
            visible: true,
            canFilterStartTime: true,
            canFilterEndTime: true
        }
    ],
};
const detailTextScenarios = [
    {
        name: "countdown success",
        index: 0,
        expectedStart: 'Started preparation of "FuelTankTestContextPerClass"',
        expectedEnd: 'Completed preparation of "FuelTankTestContextPerClass"'
    },
    {
        name: "mission suppression",
        index: 1,
        expectedStart: 'Started execution of "testFuelTankShouldFillWhenCalled(int) [1] -1"',
        expectedEnd: 'Suppressed failure during execution of "testFuelTankShouldFillWhenCalled(int) [1] -1"<br /><br />Caught java.lang.UnsupportedOperationException: Cannot load negative amount of propellant.<br /><div class="stack-trace">at com.github.nagyesta.abortmission.testkit.vanilla.FuelTank.load(FuelTank.java:10)<br />at com.github.nagyesta.abortmission.booster.jupiter.FuelTankTestContextPerClass.testFuelTankShouldFillWhenCalled(FuelTankTestContextPerClass.java:44)<br />at < filtered ><br />at com.github.nagyesta.abortmission.booster.jupiter.FuelTankTest.testAssumptionPerClass(FuelTankTest.java:71)<br />at < filtered ></div>'
    },
    {
        name: "mission success",
        index: 2,
        expectedStart: 'Started execution of "testFuelTankShouldFillWhenCalled(int) [2] 42"',
        expectedEnd: 'Completed execution of "testFuelTankShouldFillWhenCalled(int) [2] 42"'
    },
    {
        name: "mission failure",
        index: 3,
        expectedStart: 'Started execution of "testFuelTankShouldFillWhenCalled(int) [3] 5001"',
        expectedEnd: 'Failed to complete execution of "testFuelTankShouldFillWhenCalled(int) [3] 5001"<br /><br />Caught java.lang.IllegalStateException: Fuel tank exploded.<br /><div class="stack-trace">at com.github.nagyesta.abortmission.testkit.vanilla.FuelTank.load(FuelTank.java:14)<br />at com.github.nagyesta.abortmission.booster.jupiter.FuelTankTestContextPerClass.testFuelTankShouldFillWhenCalled(FuelTankTestContextPerClass.java:44)<br />at < filtered ><br />at com.github.nagyesta.abortmission.booster.jupiter.FuelTankTest.testAssumptionPerClass(FuelTankTest.java:71)<br />at < filtered ></div>'
    },
    {
        name: "mission failure (same millisecond)",
        index: 11,
        expectedStart: 'Started and failed to complete execution of "testFuelTankShouldFillWhenCalled(int) [3] 5001"<br /><br />Caught java.lang.IllegalStateException: Fuel tank exploded.<br /><div class="stack-trace">at com.github.nagyesta.abortmission.testkit.vanilla.FuelTank.load(FuelTank.java:14)<br />at com.github.nagyesta.abortmission.booster.jupiter.FuelTankTestContext.testFuelTankShouldFillWhenCalled(FuelTankTestContext.java:42)<br />at < filtered ><br />at com.github.nagyesta.abortmission.booster.jupiter.FuelTankTest.testAssumption(FuelTankTest.java:39)<br />at < filtered ></div>',
        expectedEnd: null
    },
    {
        name: "mission aborted",
        index: 4,
        expectedStart: 'Evaluated and aborted execution of "testFuelTankShouldFillWhenCalled(int) [4] 420000"',
        expectedEnd: null
    }
];
const times = [
    1682278146281,
    1682278146335,
    1682278146350,
    1682278146423,
    1682278146429,
    1682278146432,
    1682278146434,
    1682278146436,
    1682278146438,
    1682278146440,
    1682278146494,
    1682278146496,
    1682278146499,
    1682278146500,
    1682278146502,
    1682278146503,
    1682278146505,
    1682278146506,
    1682278146509,
    1682278146510,
    1682278146511,
    1682278146513,
    1682278146514,
    1682278146515
];
const filters = {
    "matchers": [
        {
            prefix: null,
            main: "CLASS_MATCHING('.+\\\\.FuelTankTestContext')",
            css: "filter-option-matcher"
        },
        {
            prefix: null,
            main: "CLASS_MATCHING('.+\\\\.FuelTankTestContextPerClass')",
            css: "filter-option-matcher"
        }
    ],
    "classNames": [
        {
            prefix: "co.gi.na.ab.bo.ju.",
            main: "FuelTankTestContextPerClass",
            css: "filter-option-class"
        },
        {
            prefix: "co.gi.na.ab.bo.ju.",
            main: "FuelTankTestContext",
            css: "filter-option-class"
        }
    ],
    "methodNames": [
        {
            prefix: null,
            main: "testFuelTankShouldFillWhenCalled_2",
            css: "filter-option-method"
        },
        {
            prefix: null,
            main: "testFuelTankShouldFillWhenCalled",
            css: "filter-option-method"
        }
    ],
    "threadNames": [
        {
            prefix: null,
            main: "Test worker",
            css: "filter-option-thread"
        },
        {
            prefix: null,
            main: "Test worker 2",
            css: "filter-option-thread"
        }
    ],
    "stageNames": [
        {
            prefix: null,
            main: "Countdown",
            css: "filter-option-stage"
        }
    ],
    "resultNames": [
        {
            prefix: null,
            main: "Success",
            css: "filter-option-result"
        }
    ]
}
const input = {
    "matchers": {
        "23e7f11fd139": "CLASS_MATCHING('.+\\\\.FuelTankTestContextPerClass')",
        "2c87129446ff": "CLASS_MATCHING('.+\\\\.FuelTankTestContext')"
    },
    "classNames": {
        "12d8e6927583": "com.github.nagyesta.abortmission.booster.jupiter.FuelTankTestContext",
        "1a51235d05c3": "com.github.nagyesta.abortmission.booster.jupiter.FuelTankTestContextPerClass"
    },
    "methodNames": {
        "1ffc91fc2ecf": "testFuelTankShouldFillWhenCalled",
        "2ffc91fc2ecf": "testFuelTankShouldFillWhenCalled_2"
    },
    "runs": [
        {
            "classKey": "1a51235d05c3",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "23e7f11fd139"
            ],
            "launchId": "bafe4cad-9e75-4133-94e9-fb1fd9b743bd",
            "result": "SUCCESS",
            "start": 1682278146281,
            "end": 1682278146335,
            "displayName": "FuelTankTestContextPerClass",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "1a51235d05c3",
            "methodKey": "2ffc91fc2ecf",
            "countdown": false,
            "matcherKeys": [
                "23e7f11fd139"
            ],
            "launchId": "1d27c602-94f4-462f-bf95-b03703ed35d7",
            "result": "SUPPRESSED",
            "start": 1682278146350,
            "end": 1682278146423,
            "displayName": "testFuelTankShouldFillWhenCalled(int) [1] -1",
            "threadName": "Test worker",
            "throwableClass": "java.lang.UnsupportedOperationException",
            "throwableMessage": "Cannot load negative amount of propellant.",
            "stackTrace": [
                "com.github.nagyesta.abortmission.testkit.vanilla.FuelTank.load(FuelTank.java:10)",
                "com.github.nagyesta.abortmission.booster.jupiter.FuelTankTestContextPerClass.testFuelTankShouldFillWhenCalled(FuelTankTestContextPerClass.java:44)",
                "< filtered >",
                "com.github.nagyesta.abortmission.booster.jupiter.FuelTankTest.testAssumptionPerClass(FuelTankTest.java:71)",
                "< filtered >"
            ]
        },
        {
            "classKey": "1a51235d05c3",
            "methodKey": "1ffc91fc2ecf",
            "countdown": false,
            "matcherKeys": [
                "23e7f11fd139"
            ],
            "launchId": "48780d25-718b-4fc8-8632-539e71791ffb",
            "result": "SUCCESS",
            "start": 1682278146429,
            "end": 1682278146432,
            "displayName": "testFuelTankShouldFillWhenCalled(int) [2] 42",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "1a51235d05c3",
            "methodKey": "1ffc91fc2ecf",
            "countdown": false,
            "matcherKeys": [
                "23e7f11fd139"
            ],
            "launchId": "a8a906e1-5fe6-4475-9e13-9f3f617bb7d9",
            "result": "FAILURE",
            "start": 1682278146434,
            "end": 1682278146436,
            "displayName": "testFuelTankShouldFillWhenCalled(int) [3] 5001",
            "threadName": "Test worker",
            "throwableClass": "java.lang.IllegalStateException",
            "throwableMessage": "Fuel tank exploded.",
            "stackTrace": [
                "com.github.nagyesta.abortmission.testkit.vanilla.FuelTank.load(FuelTank.java:14)",
                "com.github.nagyesta.abortmission.booster.jupiter.FuelTankTestContextPerClass.testFuelTankShouldFillWhenCalled(FuelTankTestContextPerClass.java:44)",
                "< filtered >",
                "com.github.nagyesta.abortmission.booster.jupiter.FuelTankTest.testAssumptionPerClass(FuelTankTest.java:71)",
                "< filtered >"
            ]
        },
        {
            "classKey": "1a51235d05c3",
            "methodKey": "1ffc91fc2ecf",
            "countdown": false,
            "matcherKeys": [
                "23e7f11fd139"
            ],
            "launchId": "de3fdf53-21f3-4ce3-a107-e33a6db13de9",
            "result": "ABORT",
            "start": 1682278146438,
            "end": 1682278146438,
            "displayName": "testFuelTankShouldFillWhenCalled(int) [4] 420000",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "1a51235d05c3",
            "methodKey": "1ffc91fc2ecf",
            "countdown": false,
            "matcherKeys": [
                "23e7f11fd139"
            ],
            "launchId": "ecd0dd9e-6049-445b-852f-c472c9fee079",
            "result": "ABORT",
            "start": 1682278146440,
            "end": 1682278146440,
            "displayName": "testFuelTankShouldFillWhenCalled(int) [5] 500000",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "12d8e6927583",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "2c87129446ff"
            ],
            "launchId": "1b1a4bd9-b6dd-4608-9962-2d231800ede9",
            "result": "SUCCESS",
            "start": 1682278146494,
            "end": 1682278146496,
            "displayName": "FuelTankTestContext",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "12d8e6927583",
            "methodKey": "1ffc91fc2ecf",
            "countdown": false,
            "matcherKeys": [
                "2c87129446ff"
            ],
            "launchId": "b650f4ae-7270-45d3-b041-0667e5de0370",
            "result": "SUPPRESSED",
            "start": 1682278146496,
            "end": 1682278146499,
            "displayName": "testFuelTankShouldFillWhenCalled(int) [1] -1",
            "threadName": "Test worker",
            "throwableClass": "java.lang.UnsupportedOperationException",
            "throwableMessage": "Cannot load negative amount of propellant.",
            "stackTrace": [
                "com.github.nagyesta.abortmission.testkit.vanilla.FuelTank.load(FuelTank.java:10)",
                "com.github.nagyesta.abortmission.booster.jupiter.FuelTankTestContext.testFuelTankShouldFillWhenCalled(FuelTankTestContext.java:42)",
                "< filtered >",
                "com.github.nagyesta.abortmission.booster.jupiter.FuelTankTest.testAssumption(FuelTankTest.java:39)",
                "< filtered >"
            ]
        },
        {
            "classKey": "12d8e6927583",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "2c87129446ff"
            ],
            "launchId": "a8a79bd3-9c22-4e1b-b124-f8e642772654",
            "result": "SUCCESS",
            "start": 1682278146500,
            "end": 1682278146502,
            "displayName": "FuelTankTestContext",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "12d8e6927583",
            "methodKey": "1ffc91fc2ecf",
            "countdown": false,
            "matcherKeys": [
                "2c87129446ff"
            ],
            "launchId": "71195ec1-a4e6-43ab-9fa4-d8cb2593912f",
            "result": "SUCCESS",
            "start": 1682278146502,
            "end": 1682278146503,
            "displayName": "testFuelTankShouldFillWhenCalled(int) [2] 42",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "12d8e6927583",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "2c87129446ff"
            ],
            "launchId": "c1da681c-ef32-4b04-bd98-bd5cbb1fe9cd",
            "result": "SUCCESS",
            "start": 1682278146505,
            "end": 1682278146506,
            "displayName": "FuelTankTestContext",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "12d8e6927583",
            "methodKey": "1ffc91fc2ecf",
            "countdown": false,
            "matcherKeys": [
                "2c87129446ff"
            ],
            "launchId": "2fd4cdb2-c389-4e5a-b57b-538e08694919",
            "result": "FAILURE",
            "start": 1682278146509,
            "end": 1682278146509,
            "displayName": "testFuelTankShouldFillWhenCalled(int) [3] 5001",
            "threadName": "Test worker",
            "throwableClass": "java.lang.IllegalStateException",
            "throwableMessage": "Fuel tank exploded.",
            "stackTrace": [
                "com.github.nagyesta.abortmission.testkit.vanilla.FuelTank.load(FuelTank.java:14)",
                "com.github.nagyesta.abortmission.booster.jupiter.FuelTankTestContext.testFuelTankShouldFillWhenCalled(FuelTankTestContext.java:42)",
                "< filtered >",
                "com.github.nagyesta.abortmission.booster.jupiter.FuelTankTest.testAssumption(FuelTankTest.java:39)",
                "< filtered >"
            ]
        },
        {
            "classKey": "12d8e6927583",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "2c87129446ff"
            ],
            "launchId": "7c5706b9-cfe0-4f66-9743-0853d4b6e30c",
            "result": "SUCCESS",
            "start": 1682278146510,
            "end": 1682278146511,
            "displayName": "FuelTankTestContext",
            "threadName": "Test worker 2",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "12d8e6927583",
            "methodKey": "1ffc91fc2ecf",
            "countdown": false,
            "matcherKeys": [
                "2c87129446ff"
            ],
            "launchId": "5770160b-88b1-4c33-a3c2-5b2f3c435b41",
            "result": "ABORT",
            "start": 1682278146511,
            "end": 1682278146511,
            "displayName": "testFuelTankShouldFillWhenCalled(int) [4] 420000",
            "threadName": "Test worker 2",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "12d8e6927583",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "2c87129446ff"
            ],
            "launchId": "7c359b67-27e0-4240-a079-aa0f8243dc0c",
            "result": "SUCCESS",
            "start": 1682278146513,
            "end": 1682278146513,
            "displayName": "FuelTankTestContext",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "12d8e6927583",
            "methodKey": "1ffc91fc2ecf",
            "countdown": false,
            "matcherKeys": [
                "2c87129446ff"
            ],
            "launchId": "7125d915-c82a-4f89-8ced-af3d8cfd5c5c",
            "result": "ABORT",
            "start": 1682278146514,
            "end": 1682278146514,
            "displayName": "testFuelTankShouldFillWhenCalled(int) [6] 500001",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "12d8e6927583",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "2c87129446ff"
            ],
            "launchId": "7c359b67-27e0-4240-a079-aa0f8243dc0b",
            "result": "SUCCESS",
            "start": 1682278146514,
            "end": 1682278146514,
            "displayName": "FuelTankTestContext",
            "threadName": "Test worker 2",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "12d8e6927583",
            "methodKey": "1ffc91fc2ecf",
            "countdown": false,
            "matcherKeys": [
                "2c87129446ff"
            ],
            "launchId": "7125d915-c82a-4f89-8ced-af3d8cfd5c5a",
            "result": "ABORT",
            "start": 1682278146514,
            "end": 1682278146515,
            "displayName": "testFuelTankShouldFillWhenCalled(int) [5] 500000",
            "threadName": "Test worker 2",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        }
    ]
}
const totalRunTime = "234<small>ms</small>"

module.exports.fuelTank = {
    name: "FuelTankTest",
    classNames: classNames,
    methodNames: methodNames,
    matcherNames: matcherNames,
    threadNames: threadNames,
    timestamps: times,
    totalRunTime: totalRunTime,
    input: input,
    filters: filters,
    timeFilterScenarios: timeFilterScenarios,
    detailTextScenarios: detailTextScenarios,
    selectedRunDetail: selectedRunDetail,
    selectedRunDetailNoFailureNoSuppress: selectedRunDetailNoFailureNoSuppress
}
