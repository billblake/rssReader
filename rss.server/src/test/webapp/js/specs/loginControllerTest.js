describe("RssReader", function() {

    beforeEach(module('viewApp'));

    describe("LoginController", function() {

        var $controller;
        readerConstants = {appContextPath : "reader"};
        var $httpBackend, location;
        ga = function(){};

        beforeEach(inject(function(_$rootScope_, _$controller_, $location, $http, $injector){
            // The injector unwraps the underscores (_) from around the parameter names when matching
            $controller = _$controller_;
            $rootScope = _$rootScope_;
            scope = $rootScope.$new();
            location = $location;

            $httpBackend = $injector.get('$httpBackend');

            $controller('LoginController',
                  {'$rootScope' : $rootScope, '$scope': scope, '$http': $http, "$location" : $location});
        }));

        it("log in successfully", function() {
            expect(scope.errorMessage).toBe("");

            var data = {userName: "billblake@yahoo.ie",
                    password : "password"};
            var user = {userName: "billblake@yahoo.ie"};
            $httpBackend.expectPOST('reader/login', data).respond(201, user);

            scope.email = "billblake@yahoo.ie";
            scope.password = "password";

            scope.login();
            $httpBackend.flush();
            expect(scope.errorMessage).toBe("");
            expect(location.path()).toBe("/list");
            expect(scope.user).toEqual(user);
            expect(scope.loggedIn).toBe(true);
        });

        it("log in fails with invalid password", function() {
            expect(scope.errorMessage).toBe("");
            var dataInvalidPassword = {userName: "billblake@yahoo.ie",
                    password : "invalidPassword"};
            $httpBackend.expectPOST('reader/login', dataInvalidPassword).respond(401, 'Invalid Username/Password');

            scope.email = "billblake@yahoo.ie";
            scope.password = "invalidPassword";

            scope.login();
            $httpBackend.flush();
            expect(scope.errorMessage).toBe("Invalid Username/Password");
            expect(scope.loggedIn).toBeUndefined();
        });
    });
});