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

var app = angular.module('viewApp', ['ngResource', 'ngRoute', 'ngCookies', 'infinite-scroll', 'ngSanitize']);

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





app.controller('FeedManagerController', function($scope, $cookies, $rootScope, $location, feedService, categoryService, userService) {

    var loggedInValue = $cookies.loggedIn;
    if (loggedInValue !== "logged-in" && !$rootScope.loggedIn) {
        $location.path('/login');
        return;
    }

    $scope.name = userService.getFullName();
    $scope.username = userService.getUserame();
    $scope.invalidForm = true;

    $scope.showTab = function($event) {
        $event.preventDefault();
        $($event.target).tab('show');
    };

    getFlatListOfFeeds();

    $scope.editFeed = function(feed, category) {
        $scope.currentFeed = angular.copy(feed);
        $scope.currentCategory = category;
    };

    $scope.deleteFeed = function(feed) {
        feedService.deleteFeed(feed, getFlatListOfFeeds);
    };


    $scope.editCategory = function(category) {
        $scope.currentCategory = $.extend({}, category);
    };

    $scope.deleteCategory = function(category) {
        categoryService.deleteCategory(category);
    };


    $scope.addFeed = function() {
        $scope.currentFeed = {};
    };


    $scope.validateCategoryForm = function(addCategoryForm) {
        $scope.invalidCategoryForm = !(addCategoryForm.$valid);
    };


    $scope.saveCategory = function(category) {
        categoryService.saveCategory(category, categorySaved);
    }

    $scope.addCategory = function() {
        $scope.currentCategory = {};
    };


    $scope.saveFeed = function(feed) {
        if (feed.categoryId === "new") {
            var category = {name : feed.newCategoryName};
            category.userName = $scope.username
            categoryService.saveCategory(category, function(createdCategory, putResponseHeaders) {
                feed.categoryId = createdCategory.categoryId;
                feedService.saveFeed(feed, feedSaved);
            });
        } else {
            feedService.saveFeed(feed, feedSaved);
        }
    };


    $scope.validateForm = function(feed, addFeedForm) {
        $scope.invalidForm = !(addFeedForm.$valid && isCategoryValid(feed));
    };


    function isCategoryValid(feed) {
        if (feed.categoryId === "new") {
            return typeof feed.newCategoryName !== "undefined" && feed.newCategoryName !== "";
        }
        return true;
    }


    function getFlatListOfFeeds() {
        categoryService.getCategories().$then(function(response){
            var feedCategories = response.data;
            $scope.feedCategories = feedCategories;
            var feed = {}, feeds = [], cat;
            feedCategories.forEach(function(category) {
              cat = category;
              $scope.username = category.username;
              category.feeds.forEach(function(feedItem) {
                  feed.categoryId = cat.categoryId;
                  feed.category = cat.name;
                  feed.feedId = feedItem.feedId;
                  feed.name = feedItem.name;
                  feed.url = feedItem.url;
                  feeds.push(feed);
                  feed = {};
              });
          });
          $scope.feeds = feeds;
        });
    }


    function feedSaved(response) {
        getFlatListOfFeeds();
        $('#feedModal').modal('hide');
    }


    function categorySaved(response) {
        getFlatListOfFeeds();
        $('#categoryModal').modal('hide');
    }
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
app.controller('ListController', function($scope, feedService, feedItemService, categoryService, $cookies, $cookieStore, $location, $rootScope, $http, userService) {

    var loggedInValue = $cookies.loggedIn;
    if (loggedInValue !== "logged-in" && !$rootScope.loggedIn) {
        $location.path('/login');
        return;
    }

    $scope.page = 0;
    $scope.loading = true;
    $scope.loadingMessage = "Loading Feeds";
    $scope.feedCategories = categoryService.getCategories();
    $scope.feeds = [];
    $scope.name = userService.getFullName();
    $scope.title = "All Feeds";

    $scope.loadMore = function() {
        $scope.page++;
        $scope.loading = true;
        feedService.getFeeds($scope.categoryId, $scope.feedId, loadMoreFeedsSuccessful, fail, $scope.page, $scope.displaySaved);
    };

    $scope.displayFeedsForCategory = function(category) {
        initList(category.categoryId, undefined);
        $scope.feeds = feedService.getFeeds(category.categoryId, $scope.feedId, loadFeedsSuccessful, fail, $scope.page);
        $scope.title = category.name;
    };

    $scope.displayFeedsForAllCategory = function() {
        initList(undefined, undefined);
        $scope.feeds = feedService.getFeeds(null, null, loadFeedsSuccessful, fail, $scope.page);
        $scope.title = "All Feeds";
    };

    $scope.displaySavedFeeds = function() {
        initList(undefined, undefined);
        $scope.displaySaved = true;
        $scope.feeds = feedService.getFeeds(null, null, loadFeedsSuccessful, fail, $scope.page, $scope.displaySaved);
        $scope.title = "Saved Feeds";
    };

    $scope.displayFeedsForFeed = function(feed) {
        initList(undefined, feed.feedId);
        $scope.feeds = feedService.getFeeds($scope.categoryId, feed.feedId, loadFeedsSuccessful, fail, $scope.page);
        $scope.title = feed.name;
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


    $scope.toggleSideBar = function() {
        $scope.sideBarClass = ($scope.sideBarClass !== "display") ? "display" : "";
    };


    $scope.readOrUnread = function(feed) {
        return (feed.read) ? "read" : "unread";
    };

    $scope.isSaved = function(feed) {
        return (feed.saved) ? "icon-floppy" : "icon-floppy-1";
    };


    $scope.markAsRead = function(feedItem) {
        if (!feedItem.read) {
            feedItemService.markAsRead(feedItem, function(updatedFeedItem) {
                feedItem.read = true;
                updateCategoryCounts(updatedFeedItem.feedId, true, false);
            });
        }
    };


    $scope.markAllAsRead = function() {
        if ($scope.feedId) {
            feedItemService.markFeedFeedItemsAsRead($scope.feedId, updateCountsAfterMarkFeedFeedItemsAsRead);
        } else if ($scope.categoryId) {
            feedItemService.markCategoryFeedItemsAsRead($scope.categoryId, updateCountsAfterMarkCategoryFeedItemsAsRead);
        } else {
            feedItemService.markAllAsRead(updateCountsAfterMarkAllFeedItemsAsRead);
        }
    };


    $scope.deleteFeedItem = function(feedItem) {
        feedItemService.deleteFeedItem(feedItem, function(updatedFeedItem) {
            for (var i = 0; i < $scope.feeds.length; i++) {
                if ($scope.feeds[i].feedItemId === updatedFeedItem.feedItemId) {
                    $scope.feeds.splice(i, 1);
                }
            }
            updateCategoryCounts(updatedFeedItem.feedId, !updatedFeedItem.read, true);
        });
    };


    $scope.deleteAllFeedItem = function() {
        if ($scope.feedId) {
            feedItemService.deleteAllFeedItemsInFeed($scope.feedId, deleteFeedItemsCallback);
        } else if ($scope.categoryId) {
            feedItemService.deleteAllFeedItemsInCategory($scope.categoryId, deleteFeedItemsCallback);
        } else {
            feedItemService.deleteAllFeedItems(deleteFeedItemsCallback);
        }
    };


    $scope.saveFeedItem = function(feedItem) {
        if (!feedItem.saved) {
            feedItemService.saveFeedItem(feedItem, function(feedResponse) {
                feedItem.saved = feedResponse.saved;
            });
        }
    };

    function deleteFeedItemsCallback() {
        $scope.feedCategories = categoryService.getCategories();
        for (var i = 0; i < $scope.feeds.length; i++) {
            if (!$scope.feeds[i].saved) {
                $scope.feeds.splice(i, 1);
            }
        }
    }

    function updateCategoryCounts(updatedFeedId, updateUnReadCount, updateTotalCount) {
        for (var i = 0; i < $scope.feedCategories.length; i++) {
            for (var j = 0; j < $scope.feedCategories[i].feeds.length; j++) {
                if ($scope.feedCategories[i].feeds[j].feedId === updatedFeedId) {
                    if (updateUnReadCount) {
                        $scope.feedCategories[i].feeds[j].unReadCount -= 1;
                        $scope.feedCategories[i].unReadCount -= 1;
                    }
                    if (updateTotalCount) {
                        $scope.feedCategories[i].feeds[j].totalCount -= 1;
                        $scope.feedCategories[i].totalCount -= 1;
                    }
                }
            }
        }
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

    function fail(response) {
        if (response.status === 401) {
            $location.path('/login');
        }
    };

    function updateCountsAfterMarkFeedFeedItemsAsRead(response) {
        markAllFeedsAsRed();
        for (var i = 0; i < $scope.feedCategories.length; i++) {
            for (var j = 0; j < $scope.feedCategories[i].feeds.length; j++) {
                if ($scope.feedCategories[i].feeds[j].feedId === response.feedId) {
                    var oldNumberOfUnread = $scope.feedCategories[i].feeds[j].unReadCount;
                    $scope.feedCategories[i].feeds[j].unReadCount = 0;
                    $scope.feedCategories[i].unReadCount -= oldNumberOfUnread;
                    return;
                }
            }
        }
    }

    function updateCountsAfterMarkCategoryFeedItemsAsRead(response) {
        markAllFeedsAsRed();
        for (var i = 0; i < $scope.feedCategories.length; i++) {
            if ($scope.feedCategories[i].categoryId === response.categoryId) {
                $scope.feedCategories[i].unReadCount = 0;
                for (var j = 0; j < $scope.feedCategories[i].feeds.length; j++) {
                    $scope.feedCategories[i].feeds[j].unReadCount = 0;
                }
                return;
            }
        }
    }

    function updateCountsAfterMarkAllFeedItemsAsRead(response) {
        markAllFeedsAsRed();
        for (var i = 0; i < $scope.feedCategories.length; i++) {
            $scope.feedCategories[i].unReadCount = 0;
            for (var j = 0; j < $scope.feedCategories[i].feeds.length; j++) {
                $scope.feedCategories[i].feeds[j].unReadCount = 0;
            }
        }
    }

    function markAllFeedsAsRed() {
        for (var i = 0; i < $scope.feeds.length; i++) {
            $scope.feeds[i].read = true;
        }
    }

    function initList(categoryId, feedId) {
        $scope.page = 1;
        $scope.loading = true;
        $scope.feeds = {};
        $scope.categoryId = categoryId;
        $scope.feedId = feedId;
        $scope.displaySaved = false;
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

    this.getFeeds = function (_categoryId, _feedId, suc, fail, _page, _saved) {
        var feedResource = createFeedResource(_categoryId, _feedId, _page, _saved);
        return feedResource.query(suc, fail);
    };


    this.saveFeed = function(_feed, callback) {
        if (typeof _feed === "undefined") {
            return;
        }

        var Feed = createFeedResource(_feed.categoryId);

        var feed = new Feed({
            categoryId : _feed.categoryId,
            name : _feed.name,
            url : _feed.url,
            feedId : _feed.feedId,
            userName : _feed.userName
        });
        feed.$save(callback);
    };


    this.deleteFeed = function(_feed, callback) {
        if (typeof _feed === "undefined") {
            return;
        }

        var Feed = createFeedResource(_feed.categoryId, _feed.feedId);

        var feed = new Feed({
            categoryId : _feed.categoryId,
            name : _feed.name,
            url : _feed.url,
            feedId : _feed.feedId,
            userName : _feed.userName
        });
        feed.$delete(callback);
    };


    this.refreshFeeds = function (callback) {
        var feedResource = createFeedResource();
        return feedResource.refresh(callback);
    };


    function createFeedResource(_categoryId, _feedId, _page, _saved) {
        if (_feedId === null || typeof _feedId === "undefined") {
            _feedId = "@id";
        }
        if (_categoryId  === null || typeof _categoryId === "undefined") {
            _categoryId = "@id";
        }
        if (_page  === null || typeof _page === "undefined") {
            _page = "@page";
        }
        if (_saved  === null || typeof _saved === "undefined") {
            _saved = "@saved";
        }
        var feedResource = $resource(readerConstants.appContextPath + '/feeds/category/:categoryId/feed/:feedId',
            {
                feedId : _feedId,
                categoryId : _categoryId,
                page : _page,
                saved : _saved
            },
            {
                refresh : {method:'GET', isArray: true, params:{refresh:true}}
            }
        );
        return feedResource;
    };

});


app.service('feedItemService', function ($http, $resource) {

    this.markAsRead = function(_feedItem, callback) {
        var FeedItem = createFeedItemResource(_feedItem.catId, _feedItem.feedId, _feedItem.feedItemId);
        var feedItem = new FeedItem({
            catId : _feedItem.catId,
            feedId : _feedItem.feedId,
            feedItemId : _feedItem.feedItemId
        });
        feedItem.$markAsRead(callback);
    };

    this.deleteFeedItem = function(_feedItem, callback) {
        var FeedItem = createFeedItemResource(_feedItem.catId, _feedItem.feedId, _feedItem.feedItemId);
        var feedItem = new FeedItem({
            catId : _feedItem.catId,
            feedId : _feedItem.feedId,
            feedItemId : _feedItem.feedItemId
        });
        feedItem.$delete(callback);
    };


    this.markFeedFeedItemsAsRead = function(feedId, callback) {
        var FeedItem = createFeedItemResource(undefined, feedId, undefined);
        var feedItem = new FeedItem({
            feedId : feedId
        });
        feedItem.$markAsRead(callback);
    };


    this.markCategoryFeedItemsAsRead = function(categoryId, callback) {
        var FeedItem = createFeedItemResource(categoryId, undefined, undefined);
        var feedItem = new FeedItem({
            categoryId : categoryId
        });
        feedItem.$markAsRead(callback);
    };


    this.markAllAsRead = function(callback) {
        var FeedItem = createFeedItemResource(undefined, undefined, undefined);
        var feedItem = new FeedItem({});
        feedItem.$markAsRead(callback);
    };


    this.deleteAllFeedItemsInFeed = function(feedId, callback) {
        var FeedItem = createFeedItemResource(undefined, feedId, undefined);
        var feedItem = new FeedItem({
            feedId : feedId
        });
        feedItem.$deleteFeeds(callback);
    };


    this.deleteAllFeedItemsInCategory = function(categoryId, callback) {
        var FeedItem = createFeedItemResource(categoryId, undefined, undefined);
        var feedItem = new FeedItem({
            categoryId : categoryId
        });
        feedItem.$deleteFeeds(callback);
    };


    this.deleteAllFeedItems = function(callback) {
        var FeedItem = createFeedItemResource(undefined, undefined, undefined);
        var feedItem = new FeedItem({});
        feedItem.$deleteFeeds(callback);
    };


    this.saveFeedItem = function(_feedItem, callback) {
        var FeedItem = createFeedItemResource(_feedItem.catId, _feedItem.feedId, _feedItem.feedItemId);
        var feedItem = new FeedItem({
            catId : _feedItem.catId,
            feedId : _feedItem.feedId,
            feedItemId : _feedItem.feedItemId
        });
        feedItem.$saveFeedItem(callback);
    };


    function createFeedItemResource(_categoryId, _feedId, _feedItemId) {
        if (_feedItemId === null || typeof _feedItemId === "undefined") {
            _feedItemId = "@id";
        }
        if (_feedId === null || typeof _feedId === "undefined") {
            _feedId = "@id";
        }
        if (_categoryId  === null || typeof _categoryId === "undefined") {
            _categoryId = "@id";
        }
        var feedItemResource = $resource(readerConstants.appContextPath + '/feeds/category/:categoryId/feed/:feedId/feedItem/:feedItemId',
            {
                feedId : _feedId,
                categoryId : _categoryId,
                feedItemId : _feedItemId
            },
            {
                markAsRead : {method:'PUT', params:{markAsRead:true}},
                saveFeedItem : {method:'PUT', params:{save:true}},
                deleteFeeds : {method:'DELETE', params:{deleteAll:true}}
            }
        );
        return feedItemResource;
    }
});
app.service('categoryService', function ($http, $resource) {

    this.getCategories = function (callback) {
        var category = createCategoryResource();
        return category.query(callback);
    };


    this.saveCategory = function (_category, callback) {
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
        category.$save(callback);
    };


    this.deleteCategory = function(_category) {
        if (typeof _category === "undefined") {
            return;
        }
        var Category = createCategoryResource(_category.categoryId);
        var category = new Category({
            categoryId : _category.categoryId,
            name : _category.name,
            username : _category.username
        });

        if (typeof callback === "function") {
            category.$delete(callback);
        } else {
            category.$delete();
        }
    };


    function createCategoryResource(_categoryId) {
        if (_categoryId  === null || typeof _categoryId === "undefined") {
            _categoryId = "@id";
        }
        return categoryResource = $resource(readerConstants.appContextPath + '/category/:categoryId', {categoryId : _categoryId});
    }
});
app.service('userService', function ($http, $resource, $cookies, $rootScope) {

	this.createUser = function (user, successCallback, failureCallback) {
        var userObject = $resource(readerConstants.appContextPath + '/user');

        var newUser = new userObject({
        	firstName : user.firstName,
        	lastName : user.lastName,
        	userName : user.email,
        	password : user.password
        });
        return newUser.$save(successCallback, failureCallback);
    };

    this.getFullName = function() {
        var fullName = $cookies.user;
        if (typeof fullName === "undefined") {
            fullName = $rootScope.user.firstName + " " + $rootScope.user.lastName;
        }
        return fullName.replace(/"/g, '');
    };

    this.getUserame = function() {
        var username = $cookies.username;
        if (typeof username === "undefined") {
            username = $rootScope.user.username;
        }
        return username.replace(/"/g, '');
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

