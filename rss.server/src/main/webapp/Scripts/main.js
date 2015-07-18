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

var app = angular.module('viewApp', ['ngResource', 'ngRoute', 'ngCookies', 'infinite-scroll', 'ngSanitize',
                                     'angAccordion', 'angularytics', 'angular-jqcloud']);

//This configures the routes and associates each route with a view and a controller
app.config(function ($routeProvider, AngularyticsProvider) {
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

    AngularyticsProvider.setEventHandlers(['Console', 'GoogleUniversal']);

}).run(function(Angularytics) {
    Angularytics.init();
});





app.controller('FeedManagerController', function($scope, $cookies, $rootScope, $location, feedService, categoryService, userService, Angularytics) {

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

    loadFeedsAndCategories();

    $scope.editFeed = function(feed, category) {
        $scope.currentFeed = angular.copy(feed);
        $scope.currentCategory = category;
    };

    $scope.deleteFeed = function(feed) {
        feedService.deleteFeed(feed, removeFeedFromModel);
    };


    $scope.editCategory = function(category) {
        $scope.currentCategory = $.extend({}, category);
    };

    $scope.deleteCategory = function(category) {
        categoryService.deleteCategory(category, removeCategoryFromModel);
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
            Angularytics.trackEvent("Manage Feeds", "Add Category");
            var category = {name : feed.newCategoryName};
            category.userName = $scope.username;
            categoryService.saveCategory(category, function(createdCategory, putResponseHeaders) {
                feed.categoryId = createdCategory.categoryId;
                $scope.feedCategories.push(createdCategory);
                feedService.saveFeed(feed, function(savedFeed) {
                    if (typeof feed.feedId === "undefined") {
                        feedAdded(savedFeed);
                    } else {
                        feedSaved(savedFeed);
                    }
                });
            });
        } else {
            feedService.saveFeed(feed, function(savedFeed) {
                if (typeof feed.feedId === "undefined") {
                    feedAdded(savedFeed);
                } else {
                    feedSaved(savedFeed);
                }
            });
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


    function loadFeedsAndCategories() {
        $scope.feedCategories = categoryService.getCategories();
    }


    function feedSaved(updatedFeed) {
        $('#feedModal').modal('hide');
        for (var i = 0; i < $scope.feedCategories.length; i++) {
            for (var j = 0; j < $scope.feedCategories[i].feeds.length; j++) {
                if ($scope.feedCategories[i].feeds[j].feedId === updatedFeed.feedId) {
                    $scope.feedCategories[i].feeds[j].name = updatedFeed.name;
                    return;
                }
            }
        }
    }


    function feedAdded(addedFeed) {
        $('#feedModal').modal('hide');
        for (var i = 0; i < $scope.feedCategories.length; i++) {
            if ($scope.feedCategories[i].categoryId === addedFeed.categoryId) {
                if ( $scope.feedCategories[i].feeds === null) {
                    $scope.feedCategories[i].feeds = [];
                }
                $scope.feedCategories[i].feeds.push(addedFeed);
                return;
            }
        }
    }


    function categorySaved(updatedCategory) {
        $('#categoryModal').modal('hide');
        for (var i = 0; i < $scope.feedCategories.length; i++) {
            if ($scope.feedCategories[i].categoryId === updatedCategory.categoryId) {
                $scope.feedCategories[i].name = updatedCategory.name;
                break;
            }
        }
    }


    function removeFeedFromModel(removedFeed) {
        for (var i = 0; i < $scope.feedCategories.length; i++) {
            for (var j = 0; j < $scope.feedCategories[i].feeds.length; j++) {
                if ($scope.feedCategories[i].feeds[j].feedId === removedFeed.feedId) {
                    $scope.feedCategories[i].feeds.splice(j, 1);
                    return;
                }
            }
        }
    }


    function removeCategoryFromModel(removedCategory) {
        for (var i = 0; i < $scope.feedCategories.length; i++) {
            if ($scope.feedCategories[i].categoryId === removedCategory.categoryId) {
                $scope.feedCategories.splice(i, 1);
                return;
            }
        }
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
app.controller('ListController', function($scope, feedService, feedItemService, categoryService, $cookies, $cookieStore,
        $location, $rootScope, $http, userService, Angularytics) {

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
    $scope.title = "All Feeds";
    feedItemService.getTags(handleGetTagsResponse, fail);

    $scope.loadMore = function() {
        $scope.page++;
        $scope.loading = true;
        feedItemService.getFeedItems($scope.categoryId, $scope.feedId, loadMoreFeedsSuccessful, fail, $scope.page, $scope.displaySaved, $scope.tag);
    };

    $scope.displayFeedsForCategory = function(category) {
        initList(category.categoryId, undefined);
        $scope.feeds = feedItemService.getFeedItems(category.categoryId, $scope.feedId, loadFeedsSuccessful, fail, $scope.page);
        $scope.title = category.name;
    };

    $scope.displayFeedsForAllCategory = function() {
        initList(undefined, undefined);
        $scope.feeds = feedItemService.getFeedItems(null, null, loadFeedsSuccessful, fail, $scope.page);
        $scope.title = "All Feeds";
    };

    $scope.displaySavedFeeds = function() {
        initList(undefined, undefined);
        $scope.displaySaved = true;
        $scope.feeds = feedItemService.getFeedItems(null, null, loadFeedsSuccessful, fail, $scope.page, $scope.displaySaved);
        $scope.title = "Saved Feeds";
    };

    $scope.displayFeedsForFeed = function(feed) {
        initList(undefined, feed.feedId);
        $scope.feeds = feedItemService.getFeedItems($scope.categoryId, feed.feedId, loadFeedsSuccessful, fail, $scope.page);
        $scope.title = feed.name;
    };

    $scope.articleClass = function(index) {
        return "article-" + index;
    };

    $scope.toggleArticle = function(index) {
        $(".article-" + index + ":first").toggle();
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
            Angularytics.trackEvent("List Feeds", "Mark As Read", "FeedItem");
            feedItemService.markAsRead(feedItem, function(updatedFeedItem) {
                feedItem.read = true;
                updateCategoryCounts(updatedFeedItem.feedId, true, false);
            });
        }
    };


    $scope.markAllAsRead = function() {
        if ($scope.feedId) {
            Angularytics.trackEvent("List Feeds", "Mark As Read", "Feed");
            feedItemService.markFeedFeedItemsAsRead($scope.feedId, updateCountsAfterMarkFeedFeedItemsAsRead);
        } else if ($scope.categoryId) {
            Angularytics.trackEvent("List Feeds", "Mark As Read", "Category");
            feedItemService.markCategoryFeedItemsAsRead($scope.categoryId, updateCountsAfterMarkCategoryFeedItemsAsRead);
        } else {
            Angularytics.trackEvent("List Feeds", "Mark As Read", "All");
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
        Angularytics.trackEvent("List Feeds", "Delete", "FeedItem");
    };

    $scope.displayDeleteAllConfirmation = function() {
        $scope.modalTitle = "Delete All";
        $scope.modalMessage = "Do you want to delete all your feed items.";
        $scope.onClickAction = "deleteAllFeedItem";
        $scope.modalButtonLabel = "Delete";
    };

    $scope.confirm = function(action) {
        if (action === "deleteAllFeedItem") {
            deleteAllFeedItem();
        }
        $('#confirmationModal').modal('hide');
    };


    $scope.saveFeedItem = function(feedItem) {
        if (!feedItem.saved) {
            Angularytics.trackEvent("List Feeds", "Save FeedItem");
            feedItemService.saveFeedItem(feedItem, function(feedResponse) {
                feedItem.saved = feedResponse.saved;
            });
        }
    };


    $scope.showTagPopup = function(feedItem) {
        $scope.currentFeedItem = feedItem;
        $scope.tag = "";
    };


    $scope.addTag = function(feedItem, tag) {
        feedItemService.addTag(feedItem, tag, function(feedItemResponse) {
            $('#addTagModal').modal('hide');
            feedItem.tags = feedItemResponse.tags;
        });
    };


    $scope.deleteTag = function(feedItem, tag) {
        feedItemService.deleteTag(feedItem, tag, function(feedItemResponse) {
            feedItem.tags = feedItemResponse.tags;
        });
    };


    $scope.readMore = function() {
        Angularytics.trackEvent("List Feeds", "Read More");
    };


    $scope.getFeedsByTag = function(tag) {
        initList(undefined, undefined, tag);
        $scope.feeds = feedItemService.getFeedItems(null, null, loadFeedsSuccessful, fail, $scope.page, null, tag);
        $scope.title = tag;
    };


    function deleteAllFeedItem() {
        if ($scope.feedId) {
            Angularytics.trackEvent("List Feeds", "Delete", "Feed");
            feedItemService.deleteAllFeedItemsInFeed($scope.feedId, deleteFeedItemsCallback);
        } else if ($scope.categoryId) {
            Angularytics.trackEvent("List Feeds", "Delete", "Category");
            feedItemService.deleteAllFeedItemsInCategory($scope.categoryId, deleteFeedItemsCallback);
        } else {
            Angularytics.trackEvent("List Feeds", "Delete", "All");
            feedItemService.deleteAllFeedItems(deleteFeedItemsCallback);
        }
    };

    function deleteFeedItemsCallback() {
        $scope.feedCategories = categoryService.getCategories();
        var savedFeeds = [];
        for (var i = 0; i < $scope.feeds.length; i++) {
            if ($scope.feeds[i].saved) {
                savedFeeds.push($scope.feeds[i]);
            }
        }
        $scope.feeds = savedFeeds;
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

    function initList(categoryId, feedId, tag) {
        $scope.page = 1;
        $scope.loading = true;
        $scope.feeds = {};
        $scope.categoryId = categoryId;
        $scope.feedId = feedId;
        $scope.tag = tag;
        $scope.displaySaved = false;
    }

    function handleGetTagsResponse(data, status, headers, config) {
        var handlers = {
            click: function(e) {
                $scope.getFeedsByTag(e.target.innerHTML);
            }
        };

        for (var i = 0; i < data.length; i++) {
            data[i]["handlers"] = handlers;
        }

        $scope.tags = data;
    }
});
app.controller('loggedInHeaderController', function($scope, $cookies, $http, $location, $rootScope, $cookieStore, userService) {

    var loggedInValue = $cookies.loggedIn;
    $scope.name = userService.getFullName();


    $scope.logout = function() {
        var responsePromise = $http.get(readerConstants.appContextPath + "/logout");

        responsePromise.success(function(user, status, headers, config) {
            $location.path('/logout');
            $cookies.loggedIn = "logged-out";
            $rootScope.loggedIn = false;
            $cookieStore.remove("user");
        });
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



    function createFeedResource(_categoryId, _feedId) {
        if (_feedId === null || typeof _feedId === "undefined") {
            _feedId = "@id";
        }
        if (_categoryId  === null || typeof _categoryId === "undefined") {
            _categoryId = "@id";
        }
        var feedResource = $resource(readerConstants.appContextPath + '/feeds/category/:categoryId/feed/:feedId',
            {
                feedId : _feedId,
                categoryId : _categoryId
            }
        );
        return feedResource;
    };

});


app.service('feedItemService', function ($http, $resource) {

    this.getFeedItems = function (_categoryId, _feedId, suc, fail, _page, _saved, tag) {
        var feedResource = createFeedItemResource(_categoryId, _feedId, null, _page, _saved, tag);
        return feedResource.query(suc, fail);
    };



    this.getTags = function(successCallback, errorCallback) {
        $http.get('getFeedItemTags').success(successCallback).error(errorCallback);
    };


    this.markAsRead = function(feedItem, callback) {
        _feedItem = $.extend({}, feedItem);
        _feedItem.read = true;
        saveFeedItem(_feedItem, callback);
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


    this.saveFeedItem = function(feedItem, callback) {
        _feedItem = $.extend({}, feedItem);
        _feedItem.saved = true;
        saveFeedItem(_feedItem, callback);
    };


    this.addTag = function(_feedItem, tag, callback) {
        var feedItem = $.extend({}, _feedItem);
        if (!feedItem.tags) {
            feedItem.tags = [];
        }
        if (feedItem.tags.indexOf(tag) == -1) {
            feedItem.tags.push(tag);
        }
        saveFeedItem(feedItem, callback);
    };


    this.deleteTag = function(_feedItem, tag, callback) {
        var feedItem = $.extend({}, _feedItem);
        var index = feedItem.tags.indexOf(tag);
        if (index > -1) {
            feedItem.tags.splice(index, 1);
        }
        saveFeedItem(feedItem, callback);
    };


    function saveFeedItem(_feedItem, callback) {
        var FeedItem = createFeedItemResource(_feedItem.catId, _feedItem.feedId, _feedItem.feedItemId);
        var feedItem = new FeedItem();
        feedItem.catId = _feedItem.catId;
        feedItem.feedId = _feedItem.feedId;
        feedItem.feedItemId = _feedItem.feedItemId;
        feedItem.description = _feedItem.description;
        feedItem.title = _feedItem.title;
        feedItem.tags = _feedItem.tags;
        feedItem.username = _feedItem.username;
        feedItem.source = _feedItem.source;
        feedItem.link = _feedItem.link;
        feedItem.pubDate = _feedItem.pubDate;
        feedItem.formattedDate = _feedItem.formattedDate;
        feedItem.read = _feedItem.read;
        feedItem.saved = _feedItem.saved;
        feedItem.$save(callback);
    }


    function createFeedItemResource(_categoryId, _feedId, _feedItemId,  _page, _saved, _tag) {
        if (_feedItemId === null || typeof _feedItemId === "undefined") {
            _feedItemId = "@id";
        }
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
        if (_tag  === null || typeof _tag === "undefined") {
            _tag = "@tag";
        }
        var feedItemResource = $resource(readerConstants.appContextPath + '/feeds/category/:categoryId/feed/:feedId/feedItem/:feedItemId',
            {
                feedId : _feedId,
                categoryId : _categoryId,
                feedItemId : _feedItemId,
                page : _page,
                saved : _saved,
                tag : _tag
            },
            {
                markAsRead : {method:'PUT', params:{markAsRead:true}},
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


    this.deleteCategory = function(_category, callback) {
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

