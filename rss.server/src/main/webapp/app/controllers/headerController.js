app.controller('loggedInHeaderController', function($scope, $cookies, $http, $location, $rootScope, $cookieStore, userService) {

    var loggedInValue = $cookies.loggedIn;
    $scope.name = userService.getFullName();


    $scope.logout = function() {
        var responsePromise = $http.get(readerConstants.appContextPath + "/logout");

        responsePromise.success(function(user, status, headers, config) {
            $location.path('/logout');
            $cookies.loggedIn = "logged-out";
            $rootScope.loggedIn = false;
            $cookieStore.remove("user");
        });
    };

});