module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),

    concat: {
      app: {
        src: ["app/app.js",
              "app/controllers/feedManagerController.js",
              "app/controllers/loginController.js",
              "app/controllers/listController.js",
              "app/controllers/signUpController.js",
              "app/services/feedService.js",
              "app/services/userService.js",
              "app/services/spinnerService.js",
              "app/directives/animatedView.js",
              "app/filters/filters.js"],
        dest: 'scripts/main.js',
      },
    },

    sass: {
      dist: {
        files: {
          'Content/main.css': 'Content/sass/main.scss'
        }
      }
    },

    watch: {
      scripts: {
        files: ['app/**/*.js'],
        tasks: ['concat']
      },
      styles: {
        files: ['./**/*.scss'],
        tasks: ['sass']
      }
    },

    // uglify: {
    //   options: {
    //     banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n'
    //   },
    //   build: {
    //     src: 'src/<%= pkg.name %>.js',
    //     dest: 'build/<%= pkg.name %>.min.js'
    //   }
    // }
  });

  // Load the plugin that provides the "uglify" task.
  // grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-sass');
  grunt.loadNpmTasks('grunt-contrib-watch');

  // Default task(s).
  // grunt.registerTask('default', ['uglify', 'concat', 'sass', 'watch']);
  grunt.registerTask('default', ['watch']);

};