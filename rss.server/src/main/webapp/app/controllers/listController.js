app.controller('ListController', function($scope, feedService, $cookies, $cookieStore, $location, $rootScope, $http, userService) {

    var loggedInValue = $cookies.loggedIn;
    if (loggedInValue !== "logged-in" && !$rootScope.loggedIn) {
        $location.path('/login');
        return;
    }

    $scope.page = 0;
    $scope.loading = true;
    $scope.loadingMessage = "Loading Feeds";
    $scope.feedCategories = feedService.getCategories();
    $scope.feeds = [];
    $scope.name = userService.getFullName();
    $scope.title = "All Feeds";

    $scope.loadMore = function() {
        $scope.page++;
        $scope.loading = true;
        feedService.getFeeds($scope.categoryId, $scope.feedId, loadMoreFeedsSuccessful, fail, $scope.page);
    };

    $scope.displayFeedsForCategory = function(category) {
        $scope.page = 0;
        $scope.loading = true;
        $scope.categoryId = category.categoryId;
        $scope.feedId = undefined;
        $scope.feeds = feedService.getFeeds(category.categoryId, $scope.feedId, loadFeedsSuccessful, fail);
        $scope.title = category.name;
    };

    $scope.displayFeedsForAllCategory = function() {
        $scope.page = 0;
        $scope.loading = true;
        $scope.feeds = {};
        $scope.categoryId = undefined;
        $scope.feedId = undefined;
        $scope.feeds = feedService.getFeeds(null, null, loadFeedsSuccessful, fail);
        $scope.title = "All Feeds";
    };

    $scope.displayFeedsForFeed = function(feed) {
        $scope.page = 0;
        $scope.loading = true;
        $scope.categoryId = undefined;
        $scope.feedId = feed.feedId;
        $scope.feeds = feedService.getFeeds($scope.categoryId, feed.feedId, loadFeedsSuccessful, fail);
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


    $scope.markAsRead = function(feedItem) {
        if (!feedItem.read) {
	   feedService.markAsRead(feedItem, function(updatedFeedItem) {
	       feedItem.read = true;
	       updateCategoryCounts(updatedFeedItem.feedId, true, false);
	   });
        }
    };


    $scope.deleteFeedItem = function(feedItem) {
        feedService.deleteFeedItem(feedItem, function(updatedFeedItem) {
            for (var i = 0; i < $scope.feeds.length; i++) {
                if ($scope.feeds[i].feedItemId === updatedFeedItem.feedItemId) {
                    $scope.feeds.splice(i, 1);
                }
            }
            updateCategoryCounts(updatedFeedItem.feedId, !updatedFeedItem.read, true);
        });
    };


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

    function fail() {
    };
});
