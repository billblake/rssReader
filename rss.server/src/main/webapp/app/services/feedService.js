app.service('feedService', function ($http, $resource) {


    this.getCategories = function () {
        var category = createCategoryResource();
        return category.query();
    };


    this.saveCategory = function (_category) {
        if (typeof _category === "undefined") {
            return;
        }

        var Category = createCategoryResource();

//  Example of retrieving the resource first then updating it.
//
//            Category.get({categoryId : _category.categoryId}, function(returnedCategory) {
//                returnedCategory.name = _category.name;
//                returnedCategory.$save();
//            });

        var category = new Category({categoryId : _category.categoryId, name : _category.name});
        category.$save();
    };


    function createCategoryResource() {
        return $resource(readerConstants.appContextPath + '/category/:categoryId',
                {categoryId : "@id"}
        );
    }


    this.getFeeds = function (_categoryId, _feedId, suc, fail) {
        var feedResource = createFeedResource(_categoryId, _feedId);
        return feedResource.query(suc, fail);
    };


    this.refreshFeeds = function (callback) {
        var feedResource = createFeedResource();
        return feedResource.refresh(callback);
    };


    function createFeedResource(_categoryId, _feedId) {
        if (_feedId === null || typeof _feedId === "undefined") {
            _feedId = "@id";
        }
        if (_categoryId  === null || typeof _categoryId === "undefined") {
            _categoryId = "@id";
        }
        var feedResource = $resource(readerConstants.appContextPath + '/feeds/category/:categoryId/feed/:feedId',
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

