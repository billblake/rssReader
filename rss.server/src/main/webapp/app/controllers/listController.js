app.controller('ListController', function($scope, feedService, $cookies, $cookieStore, $location, $rootScope, $http, userService) {

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
    $scope.name = userService.getFullName();

    $scope.loadMore = function() {
        $scope.page++;
        $scope.loading = true;
        feedService.getFeeds($scope.categoryId, $scope.feedId, loadMoreFeedsSuccessful, fail, $scope.page);
    };

    $scope.displayFeedsForCategory = function(categoryId) {
        $scope.loading = true;
        $scope.categoryId = categoryId;
        $scope.feedId = undefined;
        $scope.feeds = feedService.getFeeds(categoryId, $scope.feedId, loadFeedsSuccessful, fail);
    };

    $scope.displayFeedsForAllCategory = function() {
        $scope.loading = true;
        $scope.feeds = {};
        $scope.categoryId = undefined;
        $scope.feedId = undefined;
        $scope.feeds = feedService.getFeeds(null, null, loadFeedsSuccessful, fail);
    };

    $scope.displayFeedsForFeed = function(feedId) {
        $scope.loading = true;
        $scope.categoryId = undefined;
        $scope.feedId = feedId;
        $scope.feeds = feedService.getFeeds($scope.categoryId, feedId, loadFeedsSuccessful, fail);
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