const matcherNames = ['404a507e7945'];
const classNames = ['496283970aa0'];
const methodNames = ['4612d5688888'];
const threadNames = ['Test worker', 'Test worker 2'];
const timeFilterScenarios = [
    {start: 1685391340544, end: 1685391340544, expectedVisibleRows: 2, expectedVisibleRuns: 1, name: "single millisecond"},
    {start: 1685391340544, end: 1685391340625, expectedVisibleRows: 5, expectedVisibleRuns: 3, name: "range with exact timestamps"},
    {start: 1685391340650, end: 1685391340690, expectedVisibleRows: 7, expectedVisibleRuns: 5, name: "range with arbitrary timestamps"}
];

const detailTextScenarios = [
    {
        name: "countdown success",
        index: 0,
        expectedStart: 'Started preparation of "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:14)"',
        expectedEnd: 'Completed preparation of "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:14)"'
    },
    {
        name: "countdown success (same millisecond)",
        index: 2,
        expectedStart: 'Started and completed preparation of "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:15)"',
        expectedEnd: null
    },
    {
        name: "mission success",
        index: 3,
        expectedStart: 'Started execution of "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:15)"',
        expectedEnd: 'Completed execution of "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:15)"'
    },
    {
        name: "mission suppression",
        index: 1,
        expectedStart: 'Started execution of "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:14)"',
        expectedEnd: 'Suppressed failure during execution of "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:14)"<br /><br />Caught java.lang.UnsupportedOperationException: Cannot load negative amount of propellant.<br /><div class="stack-trace">at com.github.nagyesta.abortmission.testkit.vanilla.FuelTank.load(FuelTank.java:10)<br />at com.github.nagyesta.abortmission.booster.cucumber.fueltank.FuelTankStepDefs.amountIsLoadedIntoTheTank(FuelTankStepDefs.java:21)<br />at < filtered ></div>'
    },
    {
        name: "mission failure",
        index: 5,
        expectedStart: 'Started execution of "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:16)"',
        expectedEnd: 'Failed to complete execution of "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:16)"<br /><br />Caught java.lang.IllegalStateException: Fuel tank exploded.<br /><div class="stack-trace">at com.github.nagyesta.abortmission.testkit.vanilla.FuelTank.load(FuelTank.java:14)<br />at com.github.nagyesta.abortmission.booster.cucumber.fueltank.FuelTankStepDefs.amountIsLoadedIntoTheTank(FuelTankStepDefs.java:21)<br />at < filtered ></div>'
    },
    {
        name: "mission aborted",
        index: 6,
        expectedStart: 'Evaluated and aborted execution of "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:17)"',
        expectedEnd: null
    }
];
const times = [
    1685391340524,
    1685391340542,
    1685391340544,
    1685391340618,
    1685391340625,
    1685391340651,
    1685391340662,
    1685391340683,
    1685391340684,
    1685391340710,
    1685391340711
];
const filters = {
    "matchers": [
        {
            prefix: null,
            main: "SCENARIO_NAME_MATCHING('^Fuel.+')",
            css: "filter-option-matcher"
        }
    ],
    "classNames": [
        {
            prefix: "co/gi/na/ab/bo/cu/fu/",
            main: "FuelTank",
            css: "filter-option-feature"
        }
    ],
    "methodNames": [
        {
            prefix: null,
            main: "Fuel_1 Fuel tank should fill",
            css: "filter-option-scenario"
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
        "404a507e7945": "SCENARIO_NAME_MATCHING('^Fuel.+')"
    },
    "classNames": {
        "496283970aa0": "classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature"
    },
    "methodNames": {
        "4612d5688888": "Fuel_1 Fuel tank should fill"
    },
    "runs": [
        {
            "classKey": "496283970aa0",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": ["404a507e7945"],
            "launchId": "f1c2a18a-ec0c-4212-a7b8-23cdccd3c373",
            "result": "SUCCESS",
            "start": 1685391340524,
            "end": 1685391340542,
            "displayName": "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:14)",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "496283970aa0",
            "methodKey": "4612d5688888",
            "countdown": false,
            "matcherKeys": ["404a507e7945"],
            "launchId": "f0f72781-03f0-46cb-94b7-6beb062e74c3",
            "result": "SUPPRESSED",
            "start": 1685391340544,
            "end": 1685391340683,
            "displayName": "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:14)",
            "threadName": "Test worker 2",
            "throwableClass": "java.lang.UnsupportedOperationException",
            "throwableMessage": "Cannot load negative amount of propellant.",
            "stackTrace": ["com.github.nagyesta.abortmission.testkit.vanilla.FuelTank.load(FuelTank.java:10)", "com.github.nagyesta.abortmission.booster.cucumber.fueltank.FuelTankStepDefs.amountIsLoadedIntoTheTank(FuelTankStepDefs.java:21)", "< filtered >"]
        },
        {
            "classKey": "496283970aa0",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": ["404a507e7945"],
            "launchId": "446c51c3-fac7-4781-b101-22e56d997a1f",
            "result": "SUCCESS",
            "start": 1685391340618,
            "end": 1685391340618,
            "displayName": "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:15)",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "496283970aa0",
            "methodKey": "4612d5688888",
            "countdown": false,
            "matcherKeys": ["404a507e7945"],
            "launchId": "e0e0f9be-ac67-4555-81e5-128c385f0c90",
            "result": "SUCCESS",
            "start": 1685391340618,
            "end": 1685391340625,
            "displayName": "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:15)",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "496283970aa0",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": ["404a507e7945"],
            "launchId": "a10f3867-d848-4129-9378-74ab4366ed13",
            "result": "SUCCESS",
            "start": 1685391340651,
            "end": 1685391340651,
            "displayName": "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:16)",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "496283970aa0",
            "methodKey": "4612d5688888",
            "countdown": false,
            "matcherKeys": ["404a507e7945"],
            "launchId": "05a19282-5000-41bb-b426-4fb0b94eb0cc",
            "result": "FAILURE",
            "start": 1685391340651,
            "end": 1685391340662,
            "displayName": "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:16)",
            "threadName": "Test worker",
            "throwableClass": "java.lang.IllegalStateException",
            "throwableMessage": "Fuel tank exploded.",
            "stackTrace": ["com.github.nagyesta.abortmission.testkit.vanilla.FuelTank.load(FuelTank.java:14)", "com.github.nagyesta.abortmission.booster.cucumber.fueltank.FuelTankStepDefs.amountIsLoadedIntoTheTank(FuelTankStepDefs.java:21)", "< filtered >"]
        },
        {
            "classKey": "496283970aa0",
            "methodKey": "4612d5688888",
            "countdown": false,
            "matcherKeys": ["404a507e7945"],
            "launchId": "830e3975-b6d0-41cb-88ad-af382c9a24c1",
            "result": "ABORT",
            "start": 1685391340684,
            "end": 1685391340684,
            "displayName": "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:17)",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "496283970aa0",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": ["404a507e7945"],
            "launchId": "b64c66c7-d195-4906-a5dd-30d2242e57d4",
            "result": "SUCCESS",
            "start": 1685391340684,
            "end": 1685391340684,
            "displayName": "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:17)",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "496283970aa0",
            "methodKey": null,
            "countdown": true,
            "matcherKeys": ["404a507e7945"],
            "launchId": "94384473-6285-403e-81c9-716492493340",
            "result": "SUCCESS",
            "start": 1685391340710,
            "end": 1685391340710,
            "displayName": "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:18)",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        },
        {
            "classKey": "496283970aa0",
            "methodKey": "4612d5688888",
            "countdown": false,
            "matcherKeys": ["404a507e7945"],
            "launchId": "a8086914-0198-452e-954d-620fb3282079",
            "result": "ABORT",
            "start": 1685391340710,
            "end": 1685391340711,
            "displayName": "Fuel_1 Fuel tank should fill (classpath:com/github/nagyesta/abortmission/booster/cucumber/fueltank/FuelTank.feature:18)",
            "threadName": "Test worker",
            "throwableClass": null,
            "throwableMessage": null,
            "stackTrace": null
        }
    ]
}
const totalRunTime = "187<small>ms</small>"

module.exports.fuelTankCucumber = {
    name: "FuelTankTest (Cucumber)",
    classNames: classNames,
    methodNames: methodNames,
    matcherNames: matcherNames,
    threadNames: threadNames,
    timestamps: times,
    totalRunTime: totalRunTime,
    input: input,
    filters: filters,
    timeFilterScenarios: timeFilterScenarios,
    detailTextScenarios: detailTextScenarios
}
