app.controller('FeedManagerController', function($scope, feedService) {

    $scope.showTab = function($event) {
        $event.preventDefault();
        $($event.target).tab('show');
    };

    $scope.feedCategories = feedService.getCategories();

    $scope.editFeed = function(feed, category) {
        $scope.currentFeed = feed;
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
        console.log(feed);

    };

    $scope.saveCategory = function(category) {
        console.log(category);
        feedService.saveCategory(category);
    };

});