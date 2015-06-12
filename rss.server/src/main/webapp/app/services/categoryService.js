app.service('categoryService', function ($http, $resource) {

    this.getCategories = function (callback) {
        var category = createCategoryResource();
        return category.query(callback);
    };


    this.saveCategory = function (_category, callback) {
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
        category.$save(callback);
    };


    this.deleteCategory = function(_category) {
        if (typeof _category === "undefined") {
            return;
        }
        var Category = createCategoryResource(_category.categoryId);
        var category = new Category({
            categoryId : _category.categoryId,
            name : _category.name,
            username : _category.username
        });

        if (typeof callback === "function") {
            category.$delete(callback);
        } else {
            category.$delete();
        }
    };


    function createCategoryResource(_categoryId) {
        if (_categoryId  === null || typeof _categoryId === "undefined") {
            _categoryId = "@id";
        }
        return categoryResource = $resource(readerConstants.appContextPath + '/category/:categoryId', {categoryId : _categoryId});
    }
});