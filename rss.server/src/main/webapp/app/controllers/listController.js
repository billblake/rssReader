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
    $scope.feedItemTags = feedItemService.getTags();
    $scope.feeds = [];
    $scope.title = "All Feeds";

    $scope.loadMore = function() {
        $scope.page++;
        $scope.loading = true;
        feedItemService.getFeedItems($scope.categoryId, $scope.feedId, loadMoreFeedsSuccessful, fail, $scope.page, $scope.displaySaved);
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

    function initList(categoryId, feedId) {
        $scope.page = 1;
        $scope.loading = true;
        $scope.feeds = {};
        $scope.categoryId = categoryId;
        $scope.feedId = feedId;
        $scope.displaySaved = false;
    }
});