/// <reference path="../Scripts/angular-1.1.4.js" />

/*#######################################################################

  Dan Wahlin
  http://twitter.com/DanWahlin
  http://weblogs.asp.net/dwahlin
  http://pluralsight.com/training/Authors/Details/dan-wahlin

  Normally like to break AngularJS apps into the following folder structure
  at a minimum:

  /app
      /controllers
      /directives
      /services
      /partials
      /views

  #######################################################################*/

var app = angular.module('viewApp', ['ngResource', 'ngRoute', 'ngCookies']);

//This configures the routes and associates each route with a view and a controller
app.config(function ($routeProvider) {
    $routeProvider
        .when('/list',
            {
                controller: 'ListController',
                templateUrl: 'app/partials/list.jsp'
            })
        .when('/login',
            {
                controller: 'LoginController',
                templateUrl: 'app/partials/login.html'
            })
        .when('/signUp',
            {
                controller: 'SignUpController',
                templateUrl: 'app/partials/signUp.html'
            })
        .when('/manage',
            {
                controller: 'FeedManagerController',
                templateUrl: 'app/partials/manage.html'
            })
        .when('/logout',
            {
                templateUrl: 'app/partials/logout.html'
            })
        .otherwise({ redirectTo: '/list' });
});





app.controller('FeedManagerController', function($scope, feedService) {

    $scope.showTab = function($event) {
        $event.preventDefault();
        $($event.target).tab('show');
    };

    $scope.feedCategories = feedService.getCategories();

    $scope.editFeed = function(feed, category) {
        $scope.currentFeed = feed;
        $scope.currentCategory = category;
    };

    $scope.deleteFeed = function(feed) {

    };


    $scope.editCategory = function(category) {
        $scope.currentCategory = $.extend({}, category);
    };

    $scope.deleteCategory = function(category) {

    };


    $scope.addFeed = function() {
        $scope.currentFeed = {};
    };

    $scope.addCategory = function() {
        $scope.currentCategory = {};
    };

    $scope.saveFeed = function(feed) {
        console.log(feed);

    };

    $scope.saveCategory = function(category) {
        console.log(category);
        feedService.saveCategory(category);
    };

});
app.controller('LoginController', function($scope, $http, $location, $rootScope) {

    $scope.errorMessage = "";

    $scope.login = function() {
        var data = {
            userName : $scope.email,
            password : $scope.password
        };
        var responsePromise = $http.post(readerConstants.appContextPath + "/login", data);

        responsePromise.success(function(user, status, headers, config) {
            $rootScope.loggedIn = true;
            $rootScope.user = user;
            $location.path('/list');
        });
        responsePromise.error(function(errorMessageResponse, status, headers, config) {
            $scope.errorMessage = errorMessageResponse;
        });
    };
});
app.controller('ListController', function($scope, feedService, $cookies, $cookieStore, $location, $rootScope, $http) {

    $(window).scroll(function(){
        if  ($(window).scrollTop() == $(document).height() - $(window).height()){
            $scope.page++;
            $scope.loading = true;
            feedService.getFeeds(null, null, loadMoreFeedsSuccessful, fail, $scope.page);
        }
    });


    var loggedInValue = $cookies.loggedIn;
    if (loggedInValue !== "logged-in" && !$rootScope.loggedIn) {
        $location.path('/login');
        return;
    }

    $scope.page = 1;
    $scope.loading = true;
    $scope.loadingMessage = "Loading Feeds";
    $scope.feedCategories = feedService.getCategories();
    $scope.feeds = feedService.getFeeds(null, null, loadFeedsSuccessful, fail);
    $scope.name = getFullName();

    $scope.displayFeedsForCategory = function(categoryId) {
        $scope.loading = true;
        $scope.feeds = feedService.getFeeds(categoryId, null, loadFeedsSuccessful, fail);
    };

    $scope.displayFeedsForAllCategory = function() {
        $scope.loading = true;
        $scope.feeds = {};
        $scope.feeds = feedService.getFeeds(null, null, loadFeedsSuccessful, fail);
    };

    $scope.displayFeedsForFeed = function(feedId) {
        $scope.loading = true;
        var categoryId = undefined;
        $scope.feeds = feedService.getFeeds(categoryId, feedId, loadFeedsSuccessful, fail);
    };

    $scope.refresh = function() {
        feedService.refreshFeeds(showRefreshedFeeds);
    };

    $scope.articleClass = function(index) {
        return "article-" + index;
    };

    $scope.toggleArticle = function(index) {
        $(".article-" + index + ":first").toggle();
    };

    $scope.logout = function() {
        var responsePromise = $http.get(readerConstants.appContextPath + "/logout");

        responsePromise.success(function(user, status, headers, config) {
            $location.path('/logout');
            $cookies.loggedIn = "logged-out";
            $rootScope.loggedIn = false;
            $cookieStore.remove("user");
        });
    };


    function getFullName() {
        var fullName = $cookies.user;
        if (typeof fullName === "undefined") {
            fullName = $rootScope.user.firstName + " " + $rootScope.user.lastName;
        }
        return fullName.replace(/"/g, '');
    }


    function showRefreshedFeeds() {
        $scope.feeds = feedService.getFeeds();
    }


    function loadFeedsSuccessful(data) {
        $scope.loading = false;
    };

    function loadMoreFeedsSuccessful(newlyFetchedFeeds) {
        $scope.feeds = $scope.feeds.concat(newlyFetchedFeeds);
        $scope.loading = false;
    }

    function fail() {
    };
});
app.controller('SignUpController', function ($scope, userService) {
    $scope.invalidForm = true;

    $scope.validateForm = function(user, signUpForm) {
        var passwordsMatch = validatePasswordsMatch(user);
        $scope.invalidForm = !(signUpForm.$valid && passwordsMatch);
    };


    $scope.validateEmail = function(emailField) {
        displayInvalidEmailMessage($scope, emailField);
    };

    $scope.validatePassword = function(password) {
        validatePassword($scope, password);
    };

    $scope.validateConfirmPassword = function(user) {
        var passwordsMatch = validatePasswordsMatch(user);
        $scope.showConfirmPasswordError = !passwordsMatch;
        $scope.confirmPasswordErrorClass = getErrorClass(!passwordsMatch);
    };


    $scope.signUp = function(user) {
        var createdUserPromise = userService.createUser(user, userCreatedSuccessfully, userCreationFailure);
        
    };


    function userCreatedSuccessfully(response) {
        $scope.userCreated = true;
        $scope.userCreatedError = false;
        $scope.errorMessage = "";
        $scope.user = {};
    }


    function userCreationFailure(response) {
        $scope.userCreated = false;
        $scope.userCreatedError = true;
        $scope.errorMessage = response.data;
    }
});


function validatePasswordsMatch(user) {
    return user.password === user.confirmPassword;
}


function validatePassword($scope, password) {
    var validLength = typeof password !== "undefined" && password.length >= 6;
    var requiredCharacters = containsRequiredCharacters(password);
    var validPassword = validLength && requiredCharacters;
    displayInvalidPasswordMessage($scope, validPassword);
    $scope.showPasswordError = !validPassword;
}


function containsRequiredCharacters(password) {
    var numberPattern = new RegExp("[0-9]");
    var requiredNumber = numberPattern.test(password);
    var upperCasePattern = new RegExp("[A-Z]");
    var requiredUpperCase = upperCasePattern.test(password);
    return requiredNumber && requiredUpperCase;
}


function displayInvalidPasswordMessage($scope, validPassword) {
    $scope.passwordErrorClass = getErrorClass(!validPassword);
}


function displayInvalidEmailMessage($scope, emailField) {
    $scope.showEmailError = emailField.$invalid && emailField.$dirty;
    $scope.emailErrorClass = getErrorClass($scope.showEmailError);
}


function getErrorClass(isError) {
    if (isError) {
        return "has-error";
    } else {
        return "";
    }
}

app.service('feedService', function ($http, $resource) {


    this.getCategories = function () {
        var category = createCategoryResource();
        return category.query();
    };


    this.saveCategory = function (_category) {
        if (typeof _category === "undefined") {
            return;
        }

        var Category = createCategoryResource();

//  Example of retrieving the resource first then updating it.
//
//            Category.get({categoryId : _category.categoryId}, function(returnedCategory) {
//                returnedCategory.name = _category.name;
//                returnedCategory.$save();
//            });

        var category = new Category({categoryId : _category.categoryId, name : _category.name});
        category.$save();
    };


    function createCategoryResource() {
        return $resource(readerConstants.appContextPath + '/category/:categoryId',
                {categoryId : "@id"}
        );
    }


    this.getFeeds = function (_categoryId, _feedId, suc, fail, _page) {
        var feedResource = createFeedResource(_categoryId, _feedId, _page);
        return feedResource.query(suc, fail);
    };


    this.refreshFeeds = function (callback) {
        var feedResource = createFeedResource();
        return feedResource.refresh(callback);
    };


    function createFeedResource(_categoryId, _feedId, _page) {
        if (_feedId === null || typeof _feedId === "undefined") {
            _feedId = "@id";
        }
        if (_categoryId  === null || typeof _categoryId === "undefined") {
            _categoryId = "@id";
        }
        if (_page  === null || typeof _page === "undefined") {
            _page = "1";
        }
        var feedResource = $resource(readerConstants.appContextPath + '/feeds/category/:categoryId/feed/:feedId',
            {
                feedId : _feedId,
                categoryId : _categoryId,
                page : _page
            },
            {
                refresh : {method:'GET', isArray: true, params:{refresh:true}}
            }
        );
        return feedResource;
    };
});


app.service('userService', function ($http, $resource) {

	this.createUser = function (user, successCallback, failureCallback) {
        var userObject = $resource(readerConstants.appContextPath + '/users');

        var newUser = new userObject({
        	firstName : user.firstName,
        	lastName : user.lastName,
        	userName : user.email,
        	password : user.password
        });
        return newUser.$save(successCallback, failureCallback);
    };

});
//This directive adds custom animations to views as they enter or leave a screen
//Note that AngularJS 1.1.4 now has an ng-animate directive but this one can be used when you 
//want complete control or when you can't use that version of AngularJS yet
app.directive('animatedView', ['$route', '$anchorScroll', '$compile', '$controller', function ($route, $anchorScroll, $compile, $controller) {
    return {
        restrict: 'ECA',
        terminal: true,
        link: function (scope, element, attr) {
            var lastScope,
                onloadExp = attr.onload || '',
                defaults = { duration: 500, viewEnterAnimation: 'slideLeft', viewExitAnimation: 'fadeOut', slideAmount: 50, disabled: false },
                locals,
                template,
                options = scope.$eval(attr.animations);

            angular.extend(defaults, options);

            scope.$on('$routeChangeSuccess', update);
            update();


            function destroyLastScope() {
                if (lastScope) {
                    lastScope.$destroy();
                    lastScope = null;
                }
            }

            function clearContent() {
                element.html('');
                destroyLastScope();
            }

            function update() {
                locals = $route.current && $route.current.locals;
                template = locals && locals.$template;

                if (template) {
                    if (!defaults.disabled) {
                        if (element.children().length > 0) { //Have content in view
                            animate(defaults.viewExitAnimation);
                        }
                        else { //No content in view so treat it as an enter animation
                            animateEnterView(defaults.viewEnterAnimation);
                        }
                    }
                    else {
                        bindElement();
                    }

                } else {
                    clearContent();
                }
            }

            function animateEnterView(animation) {
                $(element).css('display', 'block');
                bindElement();
                animate(animation);
            }

            function animate(animationType) {
                switch (animationType) {
                    case 'fadeOut':
                        $(element.children()).animate({
                            //opacity: 0.0, 
                        }, defaults.duration, function () {
                            animateEnterView('slideLeft');
                        });
                        break;
                    case 'slideLeft':
                        $(element.children()).animate({
                            left: '-=' + defaults.slideAmount,
                            opacity: 1.0
                        }, defaults.duration);
                        break;
                    case 'slideRight':
                        $(element.children()).animate({
                            left: '+=' + defaults.slideAmount,
                            opacity: 1.0
                        }, defaults.duration);
                        break;
                }
            }

            function bindElement() {
                element.html(template);
                destroyLastScope();

                var link = $compile(element.contents()),
                    current = $route.current,
                    controller;

                lastScope = current.scope = scope.$new();
                if (current.controller) {
                    locals.$scope = lastScope;
                    controller = $controller(current.controller, locals);
                    element.children().data('$ngControllerController', controller);
                }

                link(lastScope);
                lastScope.$emit('$viewContentLoaded');
                lastScope.$eval(onloadExp);

                // $anchorScroll might listen on event...
                $anchorScroll();
            }
        }
    };
}]);

app.filter('reverse', function() {
    return function(input, uppercase) {
      var out = "";
      for (var i = 0; i < input.length; i++) {
        out = input.charAt(i) + out;
      }
      // conditional based on optional argument
      if (uppercase) {
        out = out.toUpperCase();
      }
      return out;
    };
  });



app.filter('formatDate', function() {
    return function(input) {
      var out = "",
      //inputDate = moment(input, "ddd, D MMM yyyy HH:mm:SS ZZ"),
      inputDate = moment.parseZone(input),
      now = moment();

      if (inputDate.dayOfYear() == now.dayOfYear() && inputDate.year() == now.year()) {
        out = inputDate.format("HH:mm a");
      } else {
        out = inputDate.format("MMM DD");
      }
      
      return out;
    };
  });

