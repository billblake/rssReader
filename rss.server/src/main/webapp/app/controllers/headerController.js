app.controller('loggedInHeaderController', function($scope, userService) {

    $scope.name = userService.getFullName();

});