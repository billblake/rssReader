app.service('userService', function ($http, $resource, $cookies, $rootScope) {

	this.createUser = function (user, successCallback, failureCallback) {
        var userObject = $resource(readerConstants.appContextPath + '/user');

        var newUser = new userObject({
        	firstName : user.firstName,
        	lastName : user.lastName,
        	userName : user.email,
        	password : user.password
        });
        return newUser.$save(successCallback, failureCallback);
    };

    this.getFullName = function() {
        var fullName = $cookies.user;
        if (typeof fullName === "undefined") {
            fullName = $rootScope.user.firstName + " " + $rootScope.user.lastName;
        }
        return fullName.replace(/"/g, '');
    };

    this.getUserame = function() {
        var username = $cookies.username;
        if (typeof username === "undefined") {
            username = $rootScope.user.username;
        }
        return username.replace(/"/g, '');
    };

});
