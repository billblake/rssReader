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