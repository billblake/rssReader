app.controller('ListController', function ($scope, feedService) {

  	$scope.feedCategories = feedService.getCategories();
  	$scope.feeds = feedService.getFeeds();
  	$scope.name = "Bill Blake"

  	$scope.displayFeedsForCategory = function(categoryId){
  		  $scope.feeds = feedService.getFeeds(categoryId);
  	};

  	$scope.displayFeedsForAllCategory = function(){
  		  $scope.feeds = feedService.getFeeds();
  	};

  	$scope.displayFeedsForFeed = function(feedId) {
  		  var categoryId = undefined;
  		  $scope.feeds = feedService.getFeeds(categoryId, feedId);
  	};

  	$scope.refresh = function(){
  		  feedService.refreshFeeds(showRefreshedFeeds);	
  	};

    function showRefreshedFeeds() {
        $scope.feeds = feedService.getFeeds();
    }
});