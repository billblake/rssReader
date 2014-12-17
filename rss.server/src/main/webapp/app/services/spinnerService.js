app.service('spinnerService', function () {

    this.spinner = {};

    this.showSpinner = function () {
        var target = document.getElementById('spinner');
        this.spinner = new Spinner({}).spin(target);
    };

    this.hideSpinner = function () {
        this.spinner.stop();
    };

});