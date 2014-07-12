app.controller('LoginController', function($scope, $http, $location, $rootScope) {

    $scope.errorMessage = "";
    
    $scope.login = function() {
        var data = {
            userName : $scope.email,
            password : $scope.password
        };
        var responsePromise = $http.post(readerConstants.appContextPath + "/login", data);

        responsePromise.success(function(data, status, headers, config) {
            $rootScope.loggedIn = true;
            $location.path('/list');
        });
        responsePromise.error(function(errorMessageResponse, status, headers, config) {
            $scope.errorMessage = errorMessageResponse;
        });
    };
});