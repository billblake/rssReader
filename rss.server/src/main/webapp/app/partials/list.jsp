<!-- BEGIN: Sticky Header 2-->
<div id="header_container">
    <div id="header">
    	<i class="icon-menu" ng-click="toggleSideBar()" />
        <span id="mini-logo"><i class="icon-rss"></i>Old News</span>
        <span id="header-nav">
            <span class="dropdown">
			    <span class="dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown">
			        {{name}}
			        <span class="caret"></span>
			    </span>
			    <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="dropdownMenu1">
				    <li role="presentation"><a role="menuitem" tabindex="-1" ng-click="refresh()">Refresh</a></li>
				    <li role="presentation"><a role="menuitem" tabindex="-1" href="#/manage">Manage Feeds</a></li>
				    <li role="presentation"><a role="menuitem" tabindex="-1">Settings</a></li>
				    <li role="presentation" class="divider"></li>
				    <li role="presentation"><a role="menuitem" tabindex="-1" ng-click="logout()">Log out</a></li>
				</ul>
			</span>
            <i id="settingsIcon" class="icon-cog"></i>
        </span>
    </div>
</div>
<!-- END: Sticky Header -->
 
<div id="feeds" class="container-fluid">
    <div class="row-fluid">
        <div id="sideBar" class="{{sideBarClass}}">
            <!--Sidebar content-->
            <ul id="categories">
                <li id="allFeeds" ng-click="displayFeedsForAllCategory()">
                	<span>
                		<i class="icon-rss"></i>
                	</span>
               		<span>All</span>
                </li>
                <li ng-repeat="feedCategory in feedCategories">
                    <span ng-click="show_$index = ! show_$index">
                        <i class="icon-right-open" ng-show="! show_$index"></i>
                        <i class="icon-down-open" ng-show="show_$index"></i>
                    </span>
                    <span ng-click="displayFeedsForCategory(feedCategory)">
                        {{ feedCategory.name }}
                    </span>
                    <ul id="feedContents_$index" class="feeds" ng-show="show_$index">
                        <li class="feed" ng-repeat="feed in feedCategory.feeds"  ng-click="displayFeedsForFeed(feed)">
                            {{feed.name}}
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
        <div id="main-content" class="span11">
            <!--Body content-->
            <h2>{{title}}</h2>
            <div id="spinner"></div>
            <ul id="feed-list" infinite-scroll='loadMore()' infinite-scroll-distance='1'>
                <li class="feed-item" ng-repeat="feedItem in feeds" ng-class-odd="'odd'" ng-class-even="'even'" ng-class="readOrUnread(feedItem)">
                    <span ng-click="toggleArticle($index);markAsRead(feedItem)">
                        <span class="feedTitle">{{ feedItem.title }}</span>
                        <span class="feedSource" title="{{feedItem.source}}">{{feedItem.source}}</span>
                        <span class="feedPubDate">{{feedItem.formattedDate}}</span>
                    </span>
                    <span class="feedItemBtns"><i class="deleteFeed icon-trash-empty" ng-click="deleteFeedItem(feedItem)"></i></span>
                    <article ng-class="articleClass($index)">
	                    <!-- contrived reverse example--> 
	                    <h3>{{feedItem.title}}</h3>
	                    <span>{{feedItem.source}} {{feedItem.formattedDate}}</span>
	                    <div id="contents">
	                        {{ feedItem.description}} <a href="{{feedItem.link}}" target="new">Read More</a>
                    		<span class="feedItemBtns"><i class="deleteFeed icon-trash-empty" ng-click="deleteFeedItem(feedItem)"></i></span>
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

