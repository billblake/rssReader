app.controller('ListController', function ($scope, feedService, $cookies, $location, $rootScope) {

	var loggedInValue = $cookies.loggedIn;
	if (!loggedInValue && !$rootScope.loggedIn) {
		$location.path('/login');
	}

  	$scope.feedCategories = feedService.getCategories();
  	$scope.feeds = feedService.getFeeds();
  	$scope.name = getFullName();
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

  	function getFullName() {
  	  var fullName = $cookies.user;
      if (typeof fullName === "undefinded") {
          fullName = $rootScope.user.firstName + " " + $rootScope.user.lastName;
      }
      return fullName.replace(/"/g, '');
  	}

    function showRefreshedFeeds() {
        $scope.feeds = feedService.getFeeds();
    }
});