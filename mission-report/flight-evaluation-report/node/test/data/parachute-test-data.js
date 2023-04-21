const matcherNames = ['2e26c95575dc', '499bc3b18ca3'];
const classNames = ['5057855e4ee9', '50311598f734'];
const methodNames = ['43223e8b610f'];
const threadNames = ['Test worker'];
const timeFilterScenarios = [
    {start: 1682278146555, end: 1682278146555, expectedVisibleRows: 1, expectedVisibleRuns: 1, name: "single millisecond"},
    {start: 1682278146555, end: 1682278146559, expectedVisibleRows: 3, expectedVisibleRuns: 3, name: "range with exact timestamps"},
    {start: 1682278146580, end: 1682278146593, expectedVisibleRows: 16, expectedVisibleRuns: 8, name: "range with arbitrary timestamps"}
];
const detailTextScenarios = [
    {
        name: "countdown success (same millisecond)",
        index: 0,
        expectedStart: 'Started and completed preparation of "ParachuteTestContextPerClass"',
        expectedEnd: null
    },
    {
        name: "mission success",
        index: 1,
        expectedStart: 'Started execution of "testParachuteShouldOpenWhenCalled(int) [1] 0"',
        expectedEnd: 'Completed execution of "testParachuteShouldOpenWhenCalled(int) [1] 0"'
    },
    {
        name: "mission abort",
        index: 5,
        expectedStart: 'Evaluated and aborted execution of "testParachuteShouldOpenWhenCalled(int) [5] 4"',
        expectedEnd: null
    },
    {
        name: "mission failure",
        index: 4,
        expectedStart: 'Started execution of "testParachuteShouldOpenWhenCalled(int) [4] 3"',
        expectedEnd: 'Failed to complete execution of "testParachuteShouldOpenWhenCalled(int) [4] 3"<br /><br />Caught org.opentest4j.AssertionFailedError: Parachutes should open. ==> expected: <true> but was: <false><br /><div class="stack-trace">at < filtered ><br />at com.github.nagyesta.abortmission.booster.jupiter.ParachuteTestContextPerClass.testParachuteShouldOpenWhenCalled(ParachuteTestContextPerClass.java:44)<br />at < filtered ><br />at com.github.nagyesta.abortmission.booster.jupiter.ParachuteDropTest.testAssumptionPerClass(ParachuteDropTest.java:70)<br />at < filtered ></div>'
    }
];
const times = [
    1682278146535,
    1682278146539,
    1682278146541,
    1682278146543,
    1682278146544,
    1682278146546,
    1682278146548,
    1682278146553,
    1682278146555,
    1682278146557,
    1682278146559,
    1682278146560,
    1682278146561,
    1682278146562,
    1682278146564,
    1682278146577,
    1682278146579,
    1682278146581,
    1682278146582,
    1682278146583,
    1682278146584,
    1682278146585,
    1682278146587,
    1682278146588,
    1682278146589,
    1682278146590,
    1682278146592,
    1682278146593,
    1682278146594,
    1682278146595,
    1682278146596,
    1682278146597,
    1682278146599,
    1682278146600,
    1682278146601,
    1682278146602,
    1682278146603,
    1682278146604,
    1682278146605,
    1682278146606,
    1682278146608
];
const filters = {
    "matchers": [
        {
            prefix: null,
            main: "CLASS_MATCHING('.+\\\\.ParachuteTestContextPerClass')",
            css: "filter-option-matcher"
        },
        {
            prefix: null,
            main: "CLASS_MATCHING('.+\\\\.ParachuteTestContext')",
            css: "filter-option-matcher"
        }
    ],
    "classNames": [
        {
            prefix: "co.gi.na.ab.bo.ju.",
            main: "ParachuteTestContext",
            css: "filter-option-class"
        },
        {
            prefix: "co.gi.na.ab.bo.ju.",
            main: "ParachuteTestContextPerClass",
            css: "filter-option-class"
        }
    ],
    "methodNames": [
        {
            prefix: null,
            main: "testParachuteShouldOpenWhenCalled",
            css: "filter-option-method"
        }
    ],
    "threadNames": [
        {
            prefix: null,
            main: "Test worker",
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
        "499bc3b18ca3": "CLASS_MATCHING('.+\\\\.ParachuteTestContext')",
        "2e26c95575dc": "CLASS_MATCHING('.+\\\\.ParachuteTestContextPerClass')"
    },
    "classNames": {
        "50311598f734": "com.github.nagyesta.abortmission.booster.jupiter.ParachuteTestContextPerClass",
        "5057855e4ee9": "com.github.nagyesta.abortmission.booster.jupiter.ParachuteTestContext"
    },
    "methodNames": {
        "43223e8b610f": "testParachuteShouldOpenWhenCalled"
    },
    "runs": [
        {
            "classKey": "50311598f734",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "2e26c95575dc"
            ],
            "launchId": "188ebcaf-bb29-4eae-a5f7-e05e980c56f8",
            "result": "SUCCESS",
            "start": 1682278146535,
            "end": 1682278146535,
            "displayName": "ParachuteTestContextPerClass",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "50311598f734",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "2e26c95575dc"
            ],
            "launchId": "4621e886-3d31-4a95-bc04-0be896070c68",
            "result": "SUCCESS",
            "start": 1682278146539,
            "end": 1682278146541,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [1] 0",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "50311598f734",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "2e26c95575dc"
            ],
            "launchId": "20271944-04f7-450e-b4b2-bbc8f58a26f1",
            "result": "SUCCESS",
            "start": 1682278146543,
            "end": 1682278146544,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [2] 1",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "50311598f734",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "2e26c95575dc"
            ],
            "launchId": "8b906300-dd84-4fa2-834b-6dbe47ca8204",
            "result": "SUCCESS",
            "start": 1682278146546,
            "end": 1682278146546,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [3] 2",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "50311598f734",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "2e26c95575dc"
            ],
            "launchId": "bddf79b8-1593-4e81-996c-df66905bd92f",
            "result": "FAILURE",
            "start": 1682278146548,
            "end": 1682278146553,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [4] 3",
            "threadName": "Test worker",
            "throwableClass": "org.opentest4j.AssertionFailedError",
            "throwableMessage": "Parachutes should open. ==> expected: <true> but was: <false>",
            "stackTrace": [
                "< filtered >",
                "com.github.nagyesta.abortmission.booster.jupiter.ParachuteTestContextPerClass.testParachuteShouldOpenWhenCalled(ParachuteTestContextPerClass.java:44)",
                "< filtered >",
                "com.github.nagyesta.abortmission.booster.jupiter.ParachuteDropTest.testAssumptionPerClass(ParachuteDropTest.java:70)",
                "< filtered >"
            ]
        },
        {
            "classKey": "50311598f734",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "2e26c95575dc"
            ],
            "launchId": "ba095cad-52ea-463a-b721-ded7631b4c71",
            "result": "ABORT",
            "start": 1682278146555,
            "end": 1682278146555,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [5] 4",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "50311598f734",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "2e26c95575dc"
            ],
            "launchId": "24a0f3ba-fb8e-4327-b2af-b16c69cd9a9a",
            "result": "ABORT",
            "start": 1682278146557,
            "end": 1682278146557,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [6] 5",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "50311598f734",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "2e26c95575dc"
            ],
            "launchId": "8a15947c-2b42-40d7-b83d-805975f3c2d9",
            "result": "ABORT",
            "start": 1682278146559,
            "end": 1682278146559,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [7] 6",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "50311598f734",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "2e26c95575dc"
            ],
            "launchId": "7cb2deae-b8f2-4cc9-8ada-29be4e7cbc0d",
            "result": "ABORT",
            "start": 1682278146560,
            "end": 1682278146561,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [8] 7",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "50311598f734",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "2e26c95575dc"
            ],
            "launchId": "73f1dac9-092f-4601-812d-787a7fac5a27",
            "result": "ABORT",
            "start": 1682278146562,
            "end": 1682278146562,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [9] 8",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "50311598f734",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "2e26c95575dc"
            ],
            "launchId": "785c1125-8cad-4c1d-827d-91d6163984f9",
            "result": "ABORT",
            "start": 1682278146564,
            "end": 1682278146564,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [10] 9",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "40a9698a-c72e-483b-8101-e71b6c1c4051",
            "result": "SUCCESS",
            "start": 1682278146577,
            "end": 1682278146579,
            "displayName": "ParachuteTestContext",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "0622bc36-c16c-40fb-aa85-dde0dff5f8e7",
            "result": "SUCCESS",
            "start": 1682278146579,
            "end": 1682278146581,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [1] 0",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "75140f27-44bf-44f0-814a-b4a2013d8a5c",
            "result": "SUCCESS",
            "start": 1682278146582,
            "end": 1682278146583,
            "displayName": "ParachuteTestContext",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "17a34307-e415-4a79-a4b3-8987f8c9318a",
            "result": "SUCCESS",
            "start": 1682278146583,
            "end": 1682278146584,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [2] 1",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "add9ce7c-50ee-41b3-a213-8515bde8b407",
            "result": "SUCCESS",
            "start": 1682278146585,
            "end": 1682278146587,
            "displayName": "ParachuteTestContext",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "e73057b1-cd9f-47bb-9eb3-55419136ea3c",
            "result": "SUCCESS",
            "start": 1682278146587,
            "end": 1682278146588,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [3] 2",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "1352b6ee-6fac-46c3-8c20-e59406a6719e",
            "result": "SUCCESS",
            "start": 1682278146589,
            "end": 1682278146590,
            "displayName": "ParachuteTestContext",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "eb0cb0fb-710b-4497-919e-f05901624621",
            "result": "FAILURE",
            "start": 1682278146590,
            "end": 1682278146592,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [4] 3",
            "threadName": "Test worker",
            "throwableClass": "org.opentest4j.AssertionFailedError",
            "throwableMessage": "Parachutes should open. ==> expected: <true> but was: <false>",
            "stackTrace": [
                "< filtered >",
                "com.github.nagyesta.abortmission.booster.jupiter.ParachuteTestContext.testParachuteShouldOpenWhenCalled(ParachuteTestContext.java:41)",
                "< filtered >",
                "com.github.nagyesta.abortmission.booster.jupiter.ParachuteDropTest.testAssumption(ParachuteDropTest.java:38)",
                "< filtered >"
            ]
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "5455d246-369a-4934-8229-9882f252b962",
            "result": "SUCCESS",
            "start": 1682278146593,
            "end": 1682278146594,
            "displayName": "ParachuteTestContext",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "6a9bd60c-307c-4f31-80bc-0a0dbd893699",
            "result": "ABORT",
            "start": 1682278146595,
            "end": 1682278146595,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [5] 4",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "c2920f91-12f2-4142-bf10-090b7718a357",
            "result": "SUCCESS",
            "start": 1682278146596,
            "end": 1682278146597,
            "displayName": "ParachuteTestContext",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "7122a886-ef4d-4c6a-b9f3-4b28bfbd8799",
            "result": "ABORT",
            "start": 1682278146597,
            "end": 1682278146597,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [6] 5",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "4f02700a-7031-4354-b9c2-0b673840a24f",
            "result": "SUCCESS",
            "start": 1682278146599,
            "end": 1682278146600,
            "displayName": "ParachuteTestContext",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "506063ff-e4a8-407b-a21a-9516be4f0803",
            "result": "ABORT",
            "start": 1682278146600,
            "end": 1682278146600,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [7] 6",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "4660d303-d2cf-4e07-bd91-90a093488611",
            "result": "SUCCESS",
            "start": 1682278146601,
            "end": 1682278146602,
            "displayName": "ParachuteTestContext",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "ae994f10-f77a-4913-b5a5-a60ad5299085",
            "result": "ABORT",
            "start": 1682278146603,
            "end": 1682278146603,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [8] 7",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "c8140175-4ae3-481c-92c9-c2516eeb8782",
            "result": "SUCCESS",
            "start": 1682278146604,
            "end": 1682278146605,
            "displayName": "ParachuteTestContext",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "edd1bb40-0262-4991-8253-7c8d67794586",
            "result": "ABORT",
            "start": 1682278146605,
            "end": 1682278146605,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [9] 8",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "ba83ae73-0331-4c38-b728-11471956fffa",
            "result": "SUCCESS",
            "start": 1682278146606,
            "end": 1682278146608,
            "displayName": "ParachuteTestContext",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "5057855e4ee9",
            "methodKey": "43223e8b610f",
            "countdown": false,
            "matcherKeys": [
                "499bc3b18ca3"
            ],
            "launchId": "8eb926aa-8c0e-4a6b-be69-e9996b126cdc",
            "result": "ABORT",
            "start": 1682278146608,
            "end": 1682278146608,
            "displayName": "testParachuteShouldOpenWhenCalled(int) [10] 9",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        }
    ]
}

module.exports.parachute = {
    name: "ParachuteTest",
    classNames: classNames,
    methodNames: methodNames,
    matcherNames: matcherNames,
    threadNames: threadNames,
    timestamps: times,
    input: input,
    filters: filters,
    timeFilterScenarios: timeFilterScenarios,
    detailTextScenarios: detailTextScenarios
}
