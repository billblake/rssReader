app.controller('ListController', function ($scope, feedService) {

  	$scope.feedCategories = feedService.getCategories();
  	$scope.feeds = feedService.getFeeds();
  	$scope.name = "Bill Blake";
  	$scope.rightArrow = readerConstants.appContextPath + "/Content/images/selector-right-arrow.png";
  	$scope.downArrow = readerConstants.appContextPath + "/Content/images/selector-down-arrow.png";

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


  	$scope.articleClass = function (index) {
        return "article-" + index;
    };

    $scope.toggleArticle = function (index) {
        $(".article-" + index + ":first").toggle();
    };

    function showRefreshedFeeds() {
        $scope.feeds = feedService.getFeeds();
    }
});