class UiToggle {

    constructor() {
        this.prefersTheme = window.matchMedia("(prefers-color-scheme: dark)").matches ? 'dark' : 'light';
        if (this.manualTheme() === 'dark') {
            this.setDarkMode();
        } else {
            this.setLightMode();
        }
    }

    manualTheme() {
        const manualValue = localStorage.getItem("abort-mission-ui-state");
        return manualValue === null ? this.prefersTheme : manualValue;
    };

    setDarkMode() {
        document.body.classList.remove("light");
        document.body.classList.add("dark");
        const toggle = document.getElementById("theme-toggle");
        toggle.innerHTML = '<!-- Sun icon under MIT license from https://feathericons.com/ --><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-sun"><circle cx="12" cy="12" r="5"></circle><line x1="12" y1="1" x2="12" y2="3"></line><line x1="12" y1="21" x2="12" y2="23"></line><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line><line x1="1" y1="12" x2="3" y2="12"></line><line x1="21" y1="12" x2="23" y2="12"></line><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line></svg>';
        toggle.attributes["title"].value = "To the Sun!";
    }

    setLightMode() {
        document.body.classList.remove("dark");
        document.body.classList.add("light");
        const toggle = document.getElementById("theme-toggle");
        toggle.innerHTML = '<!-- Moon icon under MIT license from https://feathericons.com/ --><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-moon"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path></svg>';
        toggle.attributes["title"].value = "To the Moon!";
    }

    toggleTheme() {
        if (document.body.classList.contains("light")) {
            this.setDarkMode();
            localStorage.setItem("abort-mission-ui-state", "dark");
        } else {
            this.setLightMode();
            localStorage.setItem("abort-mission-ui-state", "light");
        }
    }
}

if (typeof window !== 'undefined') {
    window.uiToggle = new UiToggle();
}
