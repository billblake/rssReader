<ng-include src="'app/partials/loggedInHeader.html'"></ng-include>
<i class="icon-menu" ng-click="toggleSideBar()" /> 
<div id="feeds" class="container-fluid">
    <div class="row-fluid">
        <div id="sideBar" class="{{sideBarClass}}">
            <!--Sidebar content-->
            <ul id="categories">
                <li id="allFeeds" ng-click="displayFeedsForAllCategory() | trackEvent:'Filter Feeds':'All'">
                	<span>
                		<i class="icon-rss"></i>
                	</span>
               		<span>All</span>
                </li>
                <li id="savedFeeds" ng-click="displaySavedFeeds() | trackEvent:'Filter Feeds':'Saved'">
                	<span>
                		<i class="icon-floppy"></i>
                	</span>
               		<span>Saved</span>
                </li>
                <li ng-repeat="feedCategory in feedCategories">
                    <span ng-click="show_$index = ! show_$index">
                        <i class="icon-right-open" ng-show="! show_$index"></i>
                        <i class="icon-down-open" ng-show="show_$index"></i>
                    </span>
                    <span ng-click="displayFeedsForCategory(feedCategory) | trackEvent:'Filter Feeds':'Category'" title="{{ feedCategory.name }} ({{ feedCategory.unReadCount }}/{{ feedCategory.totalCount }})">
                        {{ feedCategory.name }} ({{ feedCategory.unReadCount }}/{{ feedCategory.totalCount }})
                    </span>
                    <ul id="feedContents_$index" class="feeds" ng-show="show_$index">
                        <li class="feed" ng-repeat="feed in feedCategory.feeds"  ng-click="displayFeedsForFeed(feed) | trackEvent:'Filter Feeds':'Feed'" title="{{feed.name}} ({{ feed.unReadCount }}/{{ feed.totalCount }})">
                            {{feed.name}} ({{ feed.unReadCount }}/{{ feed.totalCount }})
                        </li>
                    </ul>
                </li>
            </ul>
            <h3>Feed Item Tags</h3>
            <div id="tagCloud">
	            <jqcloud words="tags" width="170" height="170" steps="10" shape="rectangular"></jqcloud>
            </div>
        </div>
        <div id="main-content" class="span11">
            <!--Body content-->
            <header>
            	<h2 class="ng-binding">{{title}}</h2>
            	<span id="actions">
            		<span id="layout" class="dropdown">
					    <span class="dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown">
					        <i class="icon-newspaper"></i>Layout
					        <span class="caret"></span>
					    </span>
					    <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="dropdownMenu1">
						    <li role="presentation"><a role="menuitem" tabindex="-1" ng-click="showTitleOnlyLayout()">
						    	<i class="icon-doc-text-1"></i>Title Only</a>
						    </li>
						    <li role="presentation"><a role="menuitem" tabindex="-1" ng-click="showMagazineLayout()">
						    	<i class="icon-th-list-outline"></i>Magazine</a>
						    </li>
						    <!-- <li role="presentation"><a role="menuitem" tabindex="-1" ng-click="showCardsLayout()">
						    	<i class="icon-th-1"></i>Cards</a>
						    </li> -->
						</ul>
					</span>
					<a id="deleteAll" ng-click="displayDeleteAllConfirmation()" data-toggle="modal" data-target="#confirmationModal">
						<i class="icon-trash-empty"></i>Delete All
					</a>
					<a id="markAllAsRead" ng-click="markAllAsRead()"><i class="icon-check"></i>Mark All As Read</a>
            		<a id="showFilter"><i class="icon-search"></i>Show Filter</a>
            	</span>
            	<span id="actionsDropdown" class="dropdown">
				    <span class="dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown">
				        Actions
				        <span class="caret"></span>
				    </span>
				    <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="dropdownMenu1">
					    <li role="presentation">
					    	<a role="menuitem" tabindex="-1" id="deleteAll" ng-click="displayDeleteAllConfirmation()" data-toggle="modal" data-target="#confirmationModal">
					    		<i class="icon-trash-empty"></i>Delete All
					    	</a>
				    	</li>
					    <li role="presentation"><a role="menuitem" tabindex="-1" id="markAllAsRead" ng-click="markAllAsRead()"><i class="icon-check"></i>Mark All As Read</a></li>
					    <li role="presentation"><a role="menuitem" tabindex="-1" id="showFilter"><i class="icon-search"></i>Show Filter</a></li>
					</ul>
				</span>
           	</header>
            <div id="spinner"></div>
            <div ng-show="!feeds.length && !loading">There are no feeds</div>
            <ul id="feed-list" ng-class="getLayoutClass()" infinite-scroll='loadMore()' infinite-scroll-distance='1'>
                <li class="feed-item" ng-repeat="feedItem in feeds" ng-class-odd="'odd'" ng-class-even="'even'" ng-class="readOrUnread(feedItem)">
                    <span class="feedHeader" ng-click="toggleArticle($index);markAsRead(feedItem) | trackEvent:'List Feeds':'Expand'">
                        <span class="feedTitle" ng-bind-html="feedItem.title"></span>
                        <span class="feedSource" title="{{feedItem.source}}">{{feedItem.source}}</span>
                        <span class="feedPubDate">{{feedItem.formattedDate}}</span>
                    </span>
                    <span class="feedItemBtns">
                    	<i class="icon-tag" ng-click="showTagPopup(feedItem)" data-toggle="modal" data-target="#addTagModal"></i>
                    	<i ng-class="isSaved(feedItem)" ng-click="saveFeedItem(feedItem)"></i>
                    	<i class="deleteFeed icon-trash-empty" ng-click="deleteFeedItem(feedItem)"></i>
                    </span>
                    <article ng-class="articleClass($index)">
	                    <!-- contrived reverse example--> 
	                    <h3 ng-bind-html="feedItem.title"></h3>
	                    <span class="publishInfo">{{feedItem.source}} {{feedItem.formattedDate}}</span>
	                    <img class="articleImage" src="{{feedItem.imageLink}}" ng-show="feedItem.imageLink">
	                    <img class="articleImage fallbackImage" src="Content/images/default-thumbnail.png" ng-hide="feedItem.imageLink">
	                    <div id="contents">
	                        <span class="description" ng-bind-html="feedItem.description"></span>
                        	<div class="tags" ng-show="feedItem.tags">
	                        		Tags:
	                        		<span class="tag" ng-repeat="tag in feedItem.tags">
	                        			<span ng-click="getFeedsByTag(tag)">{{tag}}</span>
	                        			<i class="deleteTag icon-cancel-circled-outline" ng-click="deleteTag(feedItem, tag)"></i>
	                        		</span>
	                        	</div>
	                        <div>
	                        	<a class="readMore" href="{{feedItem.link}}" target="_blank" ng-click="readMore()">Read More >></a>
	                    		<span class="feedItemBtns">
	                    			<i class="icon-tag" ng-click="showTagPopup(feedItem)" data-toggle="modal" data-target="#addTagModal"></i>
	                    			<i ng-class="isSaved(feedItem)" ng-click="saveFeedItem(feedItem)"></i>
	                    			<i class="deleteFeed icon-trash-empty" ng-click="deleteFeedItem(feedItem)"></i>
	                    		</span>
	                        </div>
	                    </div>
                    </article>
                </li>
            </ul>
            <div ng-show="loading">
	            <img id="mini-logo" src="Content/images/ajax-loader.gif" style="margin: 0 auto;display:block;"/>
            	<div style="text-align : center;margin-bottom : 30px;">{{loadingMessage}}</div>
            </div>
        </div> 
    </div>
</div>

<div id="footer_container">
    <div id="footer">
        Footer Content
    </div>
</div>

<div class="modal fade" id="confirmationModal" tabindex="-1" role="dialog" aria-labelledby="confirmationModal" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <form name="confirmationForm" role="form">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title" id="myModalLabel">{{modalTitle}}</h4>
	      </div>
	      <div class="modal-body">
	        {{modalMessage}}
	      </div>
	      <div class="modal-footer">
	      	<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	        <button type="button" class="btn btn-primary" ng-click="confirm(onClickAction)">{{modalButtonLabel}}</button>
	      </div>
      </form>
    </div>
  </div>
</div>


<div class="modal fade" id="addTagModal" tabindex="-1" role="dialog" aria-labelledby="addTagModal" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <form name="tagForm" role="form">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title" id="myModalLabel">Add Tag</h4>
	      </div>
	      <div class="modal-body">
	        <label for="firstName">Tag *</label>
		    <input type="text" class="form-control" id="tag" placeholder="Enter tag" ng-model="tag" autocomplete="off" required>
	      </div>
	      <div class="modal-footer">
	      	<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	        <button type="button" class="btn btn-primary" ng-click="addTag(currentFeedItem, tag)">Add Tag</button>
	      </div>
      </form>
    </div>
  </div>
</div>