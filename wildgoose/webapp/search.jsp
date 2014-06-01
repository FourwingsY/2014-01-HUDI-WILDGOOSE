<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-zoom=1, user-scalable=no">
<link type="text/css" rel="stylesheet" href="/stylesheet/base.css" />
<link type="text/css" rel="stylesheet" href="/stylesheet/search_reporter.css" />
<link type="text/css" rel="stylesheet" href="/stylesheet/search.css" />
<link type="text/css" rel="stylesheet" href="/stylesheet/card.css" />
<link type="text/css" rel="stylesheet" href="/stylesheet/card-media.css" />

<title>Wildgoose</title>

<div class="wrap">
	<header class="header">
		<%@ include file ="jsp_templates/header.jsp" %>
	</header>
	<div class="container">
		<div class="logo">
			<a href="/"><img src="image/logo.png" alt="wildgoose logo" class="logo-image"/></a>
		</div>
		<div class="search search-column">
			<form class="search_form" action="./search" method="get" >
			<ul class="search-column-box">
				<li class="search-query-entry">
					<input type="search" autocomplete="off" id="query-entry" name="q" placeholder="기자, URL검색" value="${ requestScope.data.searchQuery }" />
				</li>
				<li class="search-button">
					<input type="submit" id="search-action" value ="Search"/>
				</li>
			</ul>
			<%-- 자동완성 --%>
			<ul class="auto-completion-list"></ul>
			</form>
		</div>
		<div class="search-result">
			<ul>
				<c:choose>

				<%-- message 존재시 --%>
				<c:when test="${ requestScope.data.status == 500 }">
					<span>${ requestScope.data.message }</span>
				</c:when>

				<%-- searchQuery 존재시 --%>
				<c:when test="${ not empty requestScope.data.searchQuery }">
					<c:if test="${ empty requestScope.data.reporters }">
					<span>${ requestScope.data.searchQuery }에 대한 검색 결과가 없습니다.</span>
					</c:if>
					<%-- searchResult 표시부 --%>
					<c:forEach var="reporter" items="${ requestScope.data.reporters }" ><li class="card card-reporter">
						<%@ include file = "jsp_templates/reporterCard.jsp" %>
					</li></c:forEach>
				</c:when>

				</c:choose>
			</ul>
		</div>
		<%-- searchQuery 존재시 and 검색 결과가 더 많을 때 --%>
		<c:if test = "${ not empty requestScope.data.searchQuery }" >
		<div class="search-more">
			<span class="search-button-ajax">더보기</span>
			<div class="search-state search-state-hidden">
				<span class="state-search-curNum hidden">${ requestScope.data.reporters.size() }</span>
				<span class="state-search-query hidden">${ requestScope.data.searchQuery }</span>
				<span class="state-search-totalNum hidden">${ requestScope.data.totalNum }</span>
			</div>
		</div>
		</c:if>
	</div>
	<footer class="footer"></footer>
</div>

<script type="text/javascript" src="/scripts/WILDGOOSE/etc.js"></script>
<script type="text/javascript" src="/scripts/WILDGOOSE/WILDGOOSE.favorite.js"></script>
<script type="text/javascript" src="/scripts/WILDGOOSE/WILDGOOSE.ui.search_more.js"></script>
<script type="text/javascript" src="/scripts/auto-complement.js"></script>

<script>
//var favs = [];
//getFavs();
</script>