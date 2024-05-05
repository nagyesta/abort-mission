const loadGruntTasks = require('load-grunt-tasks');
const path = require('path');

module.exports = function (grunt) {
    grunt.initConfig({
        license_finder: {
            all: {
                options: {
                    production: true,
                    depth: 5,
                    directory: './',
                    out: '../build/nodejs-dependency-licenses.csv',
                    csv: true
                }
            }
        },
        sass: {
            options: {
                sourceMap: true
            },
            dist: {
                files: {
                    '../build/html-view/css/raw/00-reset.css': 'css/00-reset.scss',
                    '../build/html-view/css/raw/01-variables.css': 'css/01-variables.scss',
                    '../build/html-view/css/raw/10-base.css': 'css/10-base.scss',
                    '../build/html-view/css/raw/20-colors.css': 'css/20-colors.scss',
                    '../build/html-view/css/raw/30-layout-common.css': 'css/30-layout-common.scss',
                    '../build/html-view/css/raw/35-layout-log-view.css': 'css/35-layout-log-view.scss',
                    '../build/html-view/css/raw/40-layout-overview.css': 'css/40-layout-overview.scss',
                    '../build/html-view/css/raw/45-layout-summary.css': 'css/45-layout-summary.scss',
                    '../build/html-view/css/raw/50-theme-toggle.css': 'css/50-theme-toggle.scss',
                    '../build/html-view/css/raw/60-print.css': 'css/60-print.scss',
                }
            }
        },
        concat: {
            css: {
                src: ['../build/html-view/css/raw/*.css'],
                dest: '../build/html-view/css/all.css'
            }
        },
        cssmin: {
            target: {
                files: [{
                    expand: true,
                    cwd: '../build/html-view/css',
                    src: ['all.css'],
                    dest: '../build/html-view/css',
                    ext: '.min.css'
                }]
            }
        },
        uglify: {
            options: {
                mangle: true
            },
            dist: {
                files: {
                    '../build/html-view/js/ui-toggle.min.js': ['src/ui-toggle.js'],
                    '../build/html-view/js/app-bundle.min.js': ['../build/html-view/js/app-bundle.js']
                }
            }
        },
        webpack: {
            options: {
                entry: './src/app.js',
                output: {
                    path: path.resolve(__dirname, '..', 'build', 'html-view', 'js'),
                    filename: 'app-bundle.js',
                },
            },
            build: {
                // Grunt-specific options
                stats: 'errors-only',
                failOnError: true,
            },
        },
        assets_inline: {
            options: {
                jsTags: { // optional
                    start: '<script type="text/javascript">', // default: <script>
                    end: '</script>'                          // default: </script>
                },
                cssTags: { // optional
                    start: '<style type="text/css">', // default: <style>
                    end: '</style>'                    // default: </style>
                }
            },
            all: {
                files: {
                    '../build/html-view/inlined.html': 'report.html',
                },
            },
        },
        htmlmin: {
            dist: {
                options: {
                    removeComments: false,
                    collapseWhitespace: true
                },
                files: {
                    '../build/resources/main/templates/html/launch-report.html': '../build/html-view/inlined.html'
                }
            }
        }
    });

    loadGruntTasks(grunt);

    grunt.registerTask('default', ['license_finder', 'sass', 'concat:css', 'cssmin', 'webpack', 'uglify', 'assets_inline', 'htmlmin']);
}
;
