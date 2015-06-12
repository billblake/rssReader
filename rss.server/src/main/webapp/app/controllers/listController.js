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


    $scope.deleteFeedItem = function() {
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
});
