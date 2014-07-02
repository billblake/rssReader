//This handles retrieving data and is used by controllers. 3 options (server, factory, provider) with 
//each doing the same thing just structuring the functions/data differently.
app.service('sampleService', function () {

    this.getSampleName = function () {
        return "Joe";
    };

    
});