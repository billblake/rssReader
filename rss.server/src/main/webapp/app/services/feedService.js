app.service('feedService', function ($http, $resource) {


    this.saveFeed = function(_feed, callback) {
        if (typeof _feed === "undefined") {
            return;
        }

        var Feed = createFeedResource(_feed.categoryId);

        var feed = new Feed({
            categoryId : _feed.categoryId,
            name : _feed.name,
            url : _feed.url,
            feedId : _feed.feedId,
            userName : _feed.userName
        });
        feed.$save(callback);
    };


    this.deleteFeed = function(_feed, callback) {
        if (typeof _feed === "undefined") {
            return;
        }

        var Feed = createFeedResource(_feed.categoryId, _feed.feedId);

        var feed = new Feed({
            categoryId : _feed.categoryId,
            name : _feed.name,
            url : _feed.url,
            feedId : _feed.feedId,
            userName : _feed.userName
        });
        feed.$delete(callback);
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
            }
        );
        return feedResource;
    };

});

