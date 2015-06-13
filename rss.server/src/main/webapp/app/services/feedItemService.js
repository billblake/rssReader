app.service('feedItemService', function ($http, $resource) {

    this.markAsRead = function(_feedItem, callback) {
        var FeedItem = createFeedItemResource(_feedItem.catId, _feedItem.feedId, _feedItem.feedItemId);
        var feedItem = new FeedItem({
            catId : _feedItem.catId,
            feedId : _feedItem.feedId,
            feedItemId : _feedItem.feedItemId
        });
        feedItem.$markAsRead(callback);
    };

    this.deleteFeedItem = function(_feedItem, callback) {
        var FeedItem = createFeedItemResource(_feedItem.catId, _feedItem.feedId, _feedItem.feedItemId);
        var feedItem = new FeedItem({
            catId : _feedItem.catId,
            feedId : _feedItem.feedId,
            feedItemId : _feedItem.feedItemId
        });
        feedItem.$delete(callback);
    };


    this.markFeedFeedItemsAsRead = function(feedId, callback) {
        var FeedItem = createFeedItemResource(undefined, feedId, undefined);
        var feedItem = new FeedItem({
            feedId : feedId
        });
        feedItem.$markAsRead(callback);
    };


    this.markCategoryFeedItemsAsRead = function(categoryId, callback) {
        var FeedItem = createFeedItemResource(categoryId, undefined, undefined);
        var feedItem = new FeedItem({
            categoryId : categoryId
        });
        feedItem.$markAsRead(callback);
    };


    this.markAllAsRead = function(callback) {
        var FeedItem = createFeedItemResource(undefined, undefined, undefined);
        var feedItem = new FeedItem({});
        feedItem.$markAsRead(callback);
    };


    this.saveFeedItem = function(_feedItem, callback) {
        var FeedItem = createFeedItemResource(_feedItem.catId, _feedItem.feedId, _feedItem.feedItemId);
        var feedItem = new FeedItem({
            catId : _feedItem.catId,
            feedId : _feedItem.feedId,
            feedItemId : _feedItem.feedItemId
        });
        feedItem.$saveFeedItem(callback);
    };


    function createFeedItemResource(_categoryId, _feedId, _feedItemId) {
        if (_feedItemId === null || typeof _feedItemId === "undefined") {
            _feedItemId = "@id";
        }
        if (_feedId === null || typeof _feedId === "undefined") {
            _feedId = "@id";
        }
        if (_categoryId  === null || typeof _categoryId === "undefined") {
            _categoryId = "@id";
        }
        var feedItemResource = $resource(readerConstants.appContextPath + '/feeds/category/:categoryId/feed/:feedId/feedItem/:feedItemId',
            {
                feedId : _feedId,
                categoryId : _categoryId,
                feedItemId : _feedItemId
            },
            {
                markAsRead : {method:'PUT', params:{markAsRead:true}},
                saveFeedItem : {method:'PUT', params:{save:true}}
            }
        );
        return feedItemResource;
    }
});