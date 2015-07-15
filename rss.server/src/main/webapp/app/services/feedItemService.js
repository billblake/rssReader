app.service('feedItemService', function ($http, $resource) {

    this.getFeedItems = function (_categoryId, _feedId, suc, fail, _page, _saved) {
        var feedResource = createFeedItemResource(_categoryId, _feedId, null, _page, _saved);
        return feedResource.query(suc, fail);
    };


    this.markAsRead = function(feedItem, callback) {
        _feedItem = $.extend({}, feedItem);
        _feedItem.read = true;
        saveFeedItem(_feedItem, callback);
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


    this.deleteAllFeedItemsInFeed = function(feedId, callback) {
        var FeedItem = createFeedItemResource(undefined, feedId, undefined);
        var feedItem = new FeedItem({
            feedId : feedId
        });
        feedItem.$deleteFeeds(callback);
    };


    this.deleteAllFeedItemsInCategory = function(categoryId, callback) {
        var FeedItem = createFeedItemResource(categoryId, undefined, undefined);
        var feedItem = new FeedItem({
            categoryId : categoryId
        });
        feedItem.$deleteFeeds(callback);
    };


    this.deleteAllFeedItems = function(callback) {
        var FeedItem = createFeedItemResource(undefined, undefined, undefined);
        var feedItem = new FeedItem({});
        feedItem.$deleteFeeds(callback);
    };


    this.saveFeedItem = function(feedItem, callback) {
        _feedItem = $.extend({}, feedItem);
        _feedItem.saved = true;
        saveFeedItem(_feedItem, callback);
    };


    this.addTag = function(_feedItem, tag, callback) {
        var feedItem = $.extend({}, _feedItem);
        if (!feedItem.tags) {
            feedItem.tags = [];
        }
        if (feedItem.tags.indexOf(tag) == -1) {
            feedItem.tags.push(tag);
        }
        saveFeedItem(feedItem, callback);
    };


    this.deleteTag = function(_feedItem, tag, callback) {
        var feedItem = $.extend({}, _feedItem);
        var index = feedItem.tags.indexOf(tag);
        if (index > -1) {
            feedItem.tags.splice(index, 1);
        }
        saveFeedItem(feedItem, callback);
    };


    this.getTags = function() {

    }


    function saveFeedItem(_feedItem, callback) {
        var FeedItem = createFeedItemResource(_feedItem.catId, _feedItem.feedId, _feedItem.feedItemId);
        var feedItem = new FeedItem();
        feedItem.catId = _feedItem.catId;
        feedItem.feedId = _feedItem.feedId;
        feedItem.feedItemId = _feedItem.feedItemId;
        feedItem.description = _feedItem.description;
        feedItem.title = _feedItem.title;
        feedItem.tags = _feedItem.tags;
        feedItem.username = _feedItem.username;
        feedItem.source = _feedItem.source;
        feedItem.link = _feedItem.link;
        feedItem.pubDate = _feedItem.pubDate;
        feedItem.formattedDate = _feedItem.formattedDate;
        feedItem.read = _feedItem.read;
        feedItem.saved = _feedItem.saved;
        feedItem.$save(callback);
    }


    function createFeedItemResource(_categoryId, _feedId, _feedItemId,  _page, _saved) {
        if (_feedItemId === null || typeof _feedItemId === "undefined") {
            _feedItemId = "@id";
        }
        if (_feedId === null || typeof _feedId === "undefined") {
            _feedId = "@id";
        }
        if (_categoryId  === null || typeof _categoryId === "undefined") {
            _categoryId = "@id";
        }
        if (_page  === null || typeof _page === "undefined") {
            _page = "@page";
        }
        if (_saved  === null || typeof _saved === "undefined") {
            _saved = "@saved";
        }
        var feedItemResource = $resource(readerConstants.appContextPath + '/feeds/category/:categoryId/feed/:feedId/feedItem/:feedItemId',
            {
                feedId : _feedId,
                categoryId : _categoryId,
                feedItemId : _feedItemId,
                page : _page,
                saved : _saved
            },
            {
                markAsRead : {method:'PUT', params:{markAsRead:true}},
                deleteFeeds : {method:'DELETE', params:{deleteAll:true}}
            }
        );
        return feedItemResource;
    }

});