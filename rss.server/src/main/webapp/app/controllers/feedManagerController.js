app.controller('FeedManagerController', function($scope, $cookies, $rootScope, $location, feedService, userService) {

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
        if (feed.categoryId === "new") {
            var category = {name : feed.newCategoryName};
            category.userName = $scope.username
            feedService.saveCategory(category, function(createdCategory, putResponseHeaders) {
                feed.categoryId = createdCategory.categoryId;
                feedService.saveFeed(feed);
            });
        } else {
            feedService.saveFeed(feed);
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
        feedService.getCategories().$then(function(response){
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

});