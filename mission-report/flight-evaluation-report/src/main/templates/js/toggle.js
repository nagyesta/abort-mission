function collapseClass(className) {
    let toShow = document.getElementsByClassName('abort-class-' + className + '-collapse');
    for (let toShowElement of toShow) {
        toShowElement.classList.remove("hidden-class");
    }
    let toHide = document.getElementsByClassName('abort-class-' + className + '-expand');
    for (let toHideElement of toHide) {
        toHideElement.classList.add("hidden-class");
    }
}

function expandClass(className) {
    let toShow = document.getElementsByClassName('abort-class-' + className + '-expand');
    for (let toShowElement of toShow) {
        toShowElement.classList.remove("hidden-class");
    }
    let toHide = document.getElementsByClassName('abort-class-' + className + '-collapse');
    for (let toHideElement of toHide) {
        toHideElement.classList.add("hidden-class");
    }
}

function toggleMethod(className, methodName) {
    let cssClass = 'abort-class-' + className + '-mission-' + methodName + '-details';
    let toToggle = document.getElementsByClassName(cssClass);
    let isHidden = false;
    for (let toToggleElement of toToggle) {
        toToggleElement.classList.toggle('hidden');
        isHidden = toToggleElement.classList.contains('hidden');
    }
    let indicatorClass = 'abort-class-' + className + '-mission-' + methodName + '-indicator';
    let toFlip = document.getElementsByClassName(indicatorClass);
    for (let toFlipElement of toFlip) {
        if (isHidden) {
            toFlipElement.innerHTML = '&#9658;';
        } else {
            toFlipElement.innerHTML = '&#9660;';
        }
    }
}
