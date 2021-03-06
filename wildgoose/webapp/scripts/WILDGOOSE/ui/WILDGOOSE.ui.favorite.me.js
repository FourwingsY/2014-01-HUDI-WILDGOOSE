(function() {
	'use strict';
	var document = window.document;
	var console = window.console;

	var WILDGOOSE = window.WILDGOOSE || {};
	WILDGOOSE.ui = WILDGOOSE.ui || {};
	WILDGOOSE.ui.favorite = WILDGOOSE.ui.favorite || {};
	WILDGOOSE.ui.favorite.me = WILDGOOSE.ui.favorite.me || {};
	
	// 의존성 선언
	var Ajax = CAGE.ajax;
	var Dom = CAGE.util.dom;
	var User = WILDGOOSE.user;
	
	function Star(element) {
		if (element == null) {
			console.error("No element found");
			return;
		}
		this.star = element;
		this.reporterId = this.getReporterId();
		this.show();
		this.attatchEvent();
	}
	
	Star.prototype = {
		constructor: Star,
		show: function() {
			Dom.removeClass(this.star, "invisible");
		},
		getReporterId: function(){
			var reporterId = this.star.parentElement.parentElement.dataset["reporter_id"];
			return parseInt(reporterId);
		},
		toggleStar: function(onoff) {
			var star = this.star;
			var card = star.parentElement.parentElement.parentElement;
			var leftList = document.querySelector(".dashboard-left");
			var rightList = document.querySelector(".dashboard-right");
			if (onoff == true) {
				Dom.addClass(star, "on");
				Dom.removeClass(star, "off");
				Dom.removeClass(card, "blur");
				if (Dom.isDescendant(rightList, card)) {
					console.log("right to left");
					
					var container = document.querySelector(".container");
					var containerPos = container.getBoundingClientRect();
					var curPos = card.getBoundingClientRect();
					card.style.position = "absolute";
					card.style.width = curPos.width + "px";
					card.style.height = curPos.height + "px";
					card.style.top = curPos.top - containerPos.top + "px";
					card.style.left = curPos.left - containerPos.left + "px";
					card.style.zIndex = 1;
					
					var lastCard = document.querySelector(".card-last");
					var lastCardPos = lastCard.getBoundingClientRect();
					var margin = 10;
					card.style.top = lastCardPos.top - containerPos.top - margin + "px";
					card.style.left = lastCardPos.left - containerPos.left + "px";
					
					setTimeout(function() {
						document.querySelector(".dashboard-left > ul").insertBefore(card.parentNode.removeChild(card), lastCard);
						card.style.position = "";
						card.style.width = "";
						card.style.height = "";
						card.style.top = "";
						card.style.left = "";
						card.style.zIndex = "";
					}, 500);
				}
			} else if (onoff == false) {
				Dom.removeClass(star, "on");
				Dom.addClass(star, "off");
				if (Dom.isDescendant(leftList, card)) {
					Dom.addClass(card, "blur");
				}
			}
		},

		clickStar : function(e) {
			var star = e.target;
			var card = star.parentElement.parentElement.parentElement;
			var reporterId = card.firstElementChild.dataset.reporter_id;
			var url = "/api/v1/users/" + Favorite.userId + "/favorites/?reporter_id="
					+ reporterId;

			if (Dom.hasClass(star, "on")) {
				Ajax.DELETE({
					"url" : url,
					"success" : function(responseObj) {
						this.toggleStar(false);
					}.bind(this),
					"failure" : function(responseObj) {
						console.log("Failure!");
					},
					"error" : function(responseObj) {
						console.log("Error!");
					}
				});
			} else {
				Ajax.POST({
					"url" : url,
					"success" : function(responseObj) {
						this.toggleStar(true);
					}.bind(this),
					"failure" : function(responseObj) {
						console.log("Failure!");
					},
					"error" : function(responseObj) {
						console.log("Error!");
					}
				});
			}
		},
		attatchEvent: function() {
			this.star.addEventListener("click", this.clickStar.bind(this), false);
			this.star.addEventListener("click", function(e) {
				Dom.addClass(e.target, "pumping");
				setTimeout(function() {
					Dom.removeClass(e.target, "pumping");
				}, 300)
			}, false);
		},
		updateStar: function() {
			var url = "/api/v1/users/:userId/favorites/:reporterId";
			url.replace(":userId", this.userId);
			url.replace(":reporterId", this.reporterId)
			Ajax.GET({
				"url" : url,
				"callback" : function(jsonStr) {
					var result = JSON.parse(jsonStr);
					if (result.data.bool == true) {
						this.toggleStar();
					}
				}.bind(this)
			});
		}

	}
	
	var Favorite = {
		starList: [],
		userFavorites: [],
		
		init: function() {
			var userId = User.getId();
			if (userId == "" || userId == undefined) {
				console.error("Not logined");
				return;
			}
			this.userId = userId;
			
			var favStars = document.querySelectorAll(".star");
			for (var i = 0; i < favStars.length; i++) {
				var favStar = favStars[i];
				var star = new Star(favStar);
				this.starList.push(star);
			}
			this.getStarListFromServer();
		},

		addCards: function(conatiner) {
			var userId = User.getId();
			if (userId == "" || userId == undefined) {
				return;
			}
			var stars = conatiner.querySelectorAll(".star");
			Array.prototype.forEach.call(stars, function(value){
				var star = new Star(value);
				if (this.userFavorites.indexOf(star.reporterId) >= 0) {
					star.toggleStar(true);
				}
				
			}.bind(this));
		},
		updateStars: function(stars) {
			for (var i = 0; i < stars.length; i++) {
				var star = stars[i];
				if (this.userFavorites.indexOf(star.reporterId) >= 0) {
					star.toggleStar(true);
				}
			}
		},
		getStarListFromServer: function() {
			var url = "/api/v1/users/:userId/favorites/";
			url = url.replace(":userId", this.userId);
			Ajax.GET({
				"url" : url,
				"callback" : function(jsonStr) {
					var result = JSON.parse(jsonStr);
					var reporterCards = result["data"]["reporters"];
					for (var i=0; i<reporterCards.length; i++) {
						var card = reporterCards[i];
						Favorite.userFavorites.push(card["id"]);
					}
					this.updateStars(this.starList);
				}.bind(this)
			});
		}
	};
	
	WILDGOOSE.ui.favorite.me = Favorite;
	
	// 글로벌 객체에 모듈을 프로퍼티로 등록한다.
	if (typeof module !== 'undefined' && module.exports) {
		module.exports = WILDGOOSE;
		// browser export
	} else {
		window.WILDGOOSE = WILDGOOSE;
	}	

})();