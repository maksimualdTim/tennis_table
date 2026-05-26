<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tennis Scoreboard | Finished Matches</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css">

    <script src="js/app.js"></script>
</head>

<body>
<header class="header">
    <section class="nav-header">
        <div class="brand">
            <div class="nav-toggle">
                <img src="images/menu.png" alt="Logo" class="logo">
            </div>
            <span class="logo-text">TennisScoreboard</span>
        </div>
        <div>
            <nav class="nav-links">
                <a class="nav-link" href="/">Home</a>
                <a class="nav-link" href="/matches">Matches</a>
            </nav>
        </div>
    </section>
</header>
<main>
    <div class="container">
        <h1>Matches</h1>
		<form class="input-container" method="get" action="/tennis/matches">
		    <input
		        class="input-filter"
		        name="filter_by_player_name"
		        placeholder="Filter by name"
		        type="text"
		        value="${filterName}"
		    />
		
		    <button class="btn-filter" type="submit">
		        Filter
		    </button>
		
		    <a href="/tennis/matches" class="btn-filter reset-link">
		        Reset Filter
		    </a>
		</form>

        <table class="table-matches">
	        <c:forEach var="match" items="${matches}">
	            <tr>
	                <td>${match.player1.name}</td>
	                <td>${match.player2.name}</td>
	                <td>${match.winner.name}</td>
	            </tr>
	        </c:forEach>
        </table>

		<c:if test="${pageCount > 1}">
		    <div class="pagination">
		
		        <c:choose>
		            <c:when test="${currentPage > 1}">
		                <a class="prev"
		                   href="/matches?page=${currentPage - 1}&filter_by_player_name=${filterName}">
		                    &lt;
		                </a>
		            </c:when>
		            <c:otherwise>
		                <span class="prev disabled">&lt;</span>
		            </c:otherwise>
		        </c:choose>
		
		        <c:forEach var="i" begin="1" end="${pageCount}">
		            <a class="num-page ${i == currentPage ? 'current' : ''}"
		               href="/matches?page=${i}&filter_by_player_name=${filterName}">
		                ${i}
		            </a>
		        </c:forEach>
		
		        <c:choose>
		            <c:when test="${currentPage < pageCount}">
		                <a class="next"
		                   href="/matches?page=${currentPage + 1}&filter_by_player_name=${filterName}">
		                    &gt;
		                </a>
		            </c:when>
		            <c:otherwise>
		                <span class="next disabled">&gt;</span>
		            </c:otherwise>
		        </c:choose>
		
		    </div>
		</c:if>
    </div>
</main>
<footer>
    <div class="footer">
        <p>&copy; Tennis Scoreboard, project from <a href="https://zhukovsd.github.io/java-backend-learning-course/">zhukovsd/java-backend-learning-course</a>
            roadmap.</p>
    </div>
</footer>
</body>
</html>
