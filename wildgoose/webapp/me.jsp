<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-zoom=1, user-scalable=no">
<link type="text/css" rel="stylesheet" href="/stylesheet/base.css" />
<link type="text/css" rel="stylesheet" href="/stylesheet/basic_layout.css" />
<link type="text/css" rel="stylesheet" href="/stylesheet/card.css" />
<link type="text/css" rel="stylesheet" href="/stylesheet/card-media.css" />
<link type="text/css" rel="stylesheet" href="/stylesheet/article_card.css" />
<style>
.card {
	margin: 10px 0;
	width: 100%;
	box-sizing:border-box;
	-moz-box-sizing:border-box; /* Firefox */
} 
</style>
<title>Wildgoose</title>

<div class="wrap">
	<header class="header">
		<%@ include file ="jsp_templates/header.jsp" %>
	</header>
	<link type="text/css" rel="stylesheet" href="/stylesheet/me.css" />
	<div class="container">
		<div class="dashboard dashboard-left">
			<div class="dashboard-header">
				<h2>나의 기자</h2>
			</div>
			<ul>
				<c:if test="${ not empty sessionScope.userId }">
					<c:forEach var="reporter" items="${ requestScope.data.favorites }">
					<li class="card card-reporter">
						<%@ include file = "/jsp_templates/reporterCard.jsp" %>
					</li>
					</c:forEach>
				</c:if>
			</ul>
		</div>
		<div class="content-main">
		
		<div class="content-main-header">
			<h2>타임라인</h2>
		</div>
		<div class="timeline-result">
			<ul>
				<%-- session 존재시 --%>
				<c:if test="${ not empty sessionScope.userId }">
					<c:forEach var="article" items="${ requestScope.data.articles }">
					<li class="card">
						<%@ include file = "/jsp_templates/articleCard.jsp" %>
					</li>
					</c:forEach>
					

				</c:if>
				<%-- session 없을시 --%>
				<c:if test="${ empty sessionScope.userId }">
					로그인 필요
				</c:if>
			</ul>
		</div>
		</div>
		<div class="dashboard dashboard-right">
		<div class="dashboard-header">
			<h2>추천기자</h2>
		</div>
		</div>
	</div>
	<footer class="footer"></footer>
</div>

<c:choose>
	<c:when test="${ initParam.debuggerMode eq 'on' }">
		<script type="text/javascript" src="/CAGE/src/CAGE.util.js"></script>
		<script type="text/javascript" src="/CAGE/src/CAGE.ajax.js"></script>
		<script type="text/javascript" src="/scripts/WILDGOOSE/user/WILDGOOSE.user.js"></script>
		<script type="text/javascript" src="/scripts/WILDGOOSE/ui/WILDGOOSE.ui.favorite.js"></script>
		
		<script type="text/javascript" src="/scripts/APP/APP.page.favorite.js"></script>
	</c:when>
	<c:otherwise>
		<script type="text/javascript" src="/CAGE/src/CAGE.min.js"></script>
		<script type="text/javascript" src="/scripts/WILDGOOSE/WILDGOOSE.min.js"></script>
		<script type="text/javascript" src="/scripts/APP/APP.min.js"></script>
	</c:otherwise>
</c:choose>
