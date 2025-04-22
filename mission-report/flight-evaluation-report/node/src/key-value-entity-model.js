// noinspection TypeScriptUMDGlobal
const ko = require('knockout');

// noinspection JSIgnoredPromiseFromCall
class KeyValueEntityModel {
    
    constructor(rootModel, entityType, key, value, include, exclude, shortcutMatchType) {
        this.rootModel = rootModel;
        this.entityType = entityType;
        this.key = key;
        this.value = value;
        this.include = ko.observable(include);
        this.exclude = ko.observable(exclude);
        this.matchType = shortcutMatchType || null;
    }
    
    cssEntityTypeName() {
        return "filter-option-" + this.entityType.toLowerCase();
    }

    valuePrefix() {
        const classRegex = /^([a-zA-Z_$][a-zA-Z0-9_$]*\.)*[a-zA-Z_$][a-zA-Z0-9_$]*(\.\d+)*$/;
        if (this.entityType === "CLASS" && classRegex.test(this.value)) {
            return this.value.slice(0, this.value.lastIndexOf(".") + 1);
        } else if (this.entityType === "FEATURE" && this.value.includes("/")) {
            return this.value.slice(0, this.value.lastIndexOf("/") + 1);
        } else {
            return null;
        }
    }

    valuePrefixShort() {
        const prefix = this.valuePrefix();
        if (prefix !== null && this.entityType === "CLASS") {
            //replace all package name tokens with their first 2 characters only
            return prefix.replace(/([a-zA-Z_$][a-zA-Z0-9_$]*\.)/g, function (match, p1) {
                return p1.slice(0, 2) + '.';
            });
        } else if (prefix !== null && this.entityType === "FEATURE") {
            //remove classpath: prefix and replace all package name tokens with their first 2 characters only
            return prefix.replace(/(^classpath:)/, "")
                    .replace(/([a-zA-Z_$][a-zA-Z0-9_$]*\/)/g, function (match, p1) {
                        return p1.slice(0, 2) + '/';
                    });
        } else {
            return null;
        }
    }

    valueMain() {
        const prefix = this.valuePrefix();
        if (prefix !== null && this.entityType === "FEATURE") {
            return this.value.slice(prefix.length).replace(/(.feature)$/, "");
        } else if (prefix !== null && this.entityType === "CLASS") {
            return this.value.slice(prefix.length);
        } else {
            return this.value;
        }
    }

    keyMatchesElement(elements) {
        if (typeof elements === 'string') {
            return this.key === elements;
        } else if (elements instanceof Array) {
            return elements.includes(this.key);
        } else {
            console.error("ERROR: Invalid argument for keyMatchesElement: " + elements);
        }
    }

    toggleInclude() {
        if (this.matchType) {
            return this.rootModel.filter.selectAllMatching(this.entityType, this.matchType, true)
        } else {
            const self = this;
            return function () {
                if (self.exclude()) {
                    self.exclude(false);
                }
                self.rootModel.filter.hideOptions();
                self.include(!self.include());
                self.rootModel.filter.refreshFilter();
            }
        }
    }

    toggleExclude() {
        if (this.matchType) {
            return this.rootModel.filter.selectAllMatching(this.entityType, this.matchType, false)
        } else {
            const self = this;
            return function () {
                if (self.include()) {
                    self.include(false);
                }
                self.rootModel.filter.hideOptions();
                self.exclude(!self.exclude());
                self.rootModel.filter.refreshFilter();
            }
        }
    }
}

module.exports.KeyValueEntityModel = KeyValueEntityModel;

