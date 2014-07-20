app.controller('LoginController', function($scope, $http, $location, $rootScope) {

    $scope.errorMessage = "";

    $scope.login = function() {
        var data = {
            userName : $scope.email,
            password : $scope.password
        };
        var responsePromise = $http.post(readerConstants.appContextPath + "/login", data);

        responsePromise.success(function(user, status, headers, config) {
            $rootScope.loggedIn = true;
            $rootScope.user = user;
            $location.path('/list');
        });
        responsePromise.error(function(errorMessageResponse, status, headers, config) {
            $scope.errorMessage = errorMessageResponse;
        });
    };
});