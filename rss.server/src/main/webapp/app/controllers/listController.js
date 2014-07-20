app.controller('ListController', function($scope, feedService, $cookies, $cookieStore, $location, $rootScope, $http) {

    var loggedInValue = $cookies.loggedIn;
    if (loggedInValue !== "logged-in" && !$rootScope.loggedIn) {
        $location.path('/login');
        return;
    }

    $scope.feedCategories = feedService.getCategories();
    $scope.feeds = feedService.getFeeds();
    $scope.name = getFullName();

    $scope.displayFeedsForCategory = function(categoryId) {
        $scope.feeds = feedService.getFeeds(categoryId);
    };

    $scope.displayFeedsForAllCategory = function() {
        $scope.feeds = feedService.getFeeds();
    };

    $scope.displayFeedsForFeed = function(feedId) {
        var categoryId = undefined;
        $scope.feeds = feedService.getFeeds(categoryId, feedId);
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
});