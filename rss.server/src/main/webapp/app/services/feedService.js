app.service('feedService', function ($http, $resource) {


    this.getCategories = function (categoryId) {
        if (typeof _categoryId === "undefined") {
            _categoryId = "@id";
        }
        var categories = $resource(readerConstants.appContextPath + '/categories/:categoryId',
            {categoryId:'@id'}
        );
        return categories.query();
    };

    this.getFeeds = function (_categoryId, _feedId) {
        var feedResource = createFeedResource(_categoryId, _feedId);
        return feedResource.query();
    };


    this.refreshFeeds = function (callback) {
        var feedResource = createFeedResource();
        return feedResource.refresh(callback);
    };


    function createFeedResource(_categoryId, _feedId) {
        if (typeof _feedId === "undefined") {
            _feedId = "@id";
        }
        if (typeof _categoryId === "undefined") {
            _categoryId = "@id";
        }
        var feedResource = $resource(readerConstants.appContextPath + '/category/:categoryId/feeds/:feedId',
            {
                feedId : _feedId,
                categoryId : _categoryId
            },
            {
                refresh : {method:'GET', isArray: true, params:{refresh:true}}
            }
        );
        return feedResource;
    };
});

