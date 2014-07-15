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
                templateUrl: 'app/partials/list.html'
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
        .otherwise({ redirectTo: '/list' });
});








app.controller('FeedManagerController', function ($scope) {

    
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
app.controller('ListController', function ($scope, feedService, $cookies, $location, $rootScope) {

	var loggedInValue = $cookies.loggedIn;
	if (!loggedInValue && !$rootScope.loggedIn) {
		$location.path('/login');
	}

  	$scope.feedCategories = feedService.getCategories();
  	$scope.feeds = feedService.getFeeds();
  	$scope.name = $rootScope.user.firstName + " " + $rootScope.user.lastName;
  	$scope.rightArrow = readerConstants.appContextPath + "/Content/images/selector-right-arrow.png";
  	$scope.downArrow = readerConstants.appContextPath + "/Content/images/selector-down-arrow.png";

  	$scope.displayFeedsForCategory = function(categoryId){
  		  $scope.feeds = feedService.getFeeds(categoryId);
  	};

  	$scope.displayFeedsForAllCategory = function(){
  		  $scope.feeds = feedService.getFeeds();
  	};

  	$scope.displayFeedsForFeed = function(feedId) {
  		  var categoryId = undefined;
  		  $scope.feeds = feedService.getFeeds(categoryId, feedId);
  	};

  	$scope.refresh = function(){
  		  feedService.refreshFeeds(showRefreshedFeeds);
  	};

    function showRefreshedFeeds() {
        $scope.feeds = feedService.getFeeds();
    }
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


    this.getCategories = function (categoryId) {
        if (typeof _categoryId === "undefined") {
            _categoryId = "@id";
        }
        var categories = $resource(readerConstants.appContextPath + '/categories/:categoryId',
            {categoryId:'@id'}
        );
        return categories.query();
    };

    this.getFeeds = function (_categoryId, _feedId) {
        var feedResource = createFeedResource(_categoryId, _feedId);
        return feedResource.query();
    };


    this.refreshFeeds = function (callback) {
        var feedResource = createFeedResource();
        return feedResource.refresh(callback);
    };


    function createFeedResource(_categoryId, _feedId) {
        if (typeof _feedId === "undefined") {
            _feedId = "@id";
        }
        if (typeof _categoryId === "undefined") {
            _categoryId = "@id";
        }
        var feedResource = $resource(readerConstants.appContextPath + '/category/:categoryId/feeds/:feedId',
            {
                feedId : _feedId,
                categoryId : _categoryId
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

