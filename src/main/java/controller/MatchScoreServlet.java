package controller;

import java.io.IOException;
import java.util.UUID;

import config.AppContext;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Match;
import service.MatchService;

@WebServlet("/match-score")
public class MatchScoreServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private MatchService matchService;

    @Override
    public void init() throws ServletException {
        matchService = AppContext.getMatchService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            UUID matchId = UUID.fromString(request.getParameter("uuid"));

            Match match = matchService.getMatch(matchId);

            request.setAttribute("uuid", matchId);
            request.setAttribute("match", match);
            
            fillScoreTable(request, match);

            RequestDispatcher dispatcher =
                    request.getRequestDispatcher("/WEB-INF/jsp/match-score.jsp");

            dispatcher.forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());

            RequestDispatcher dispatcher =
                    request.getRequestDispatcher("/WEB-INF/jsp/new-match.jsp");

            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            UUID matchId = UUID.fromString(request.getParameter("uuid"));
            
            Match match = matchService.getMatch(matchId);

            if (request.getParameter("player1") != null) {
                matchService.addPoint(match, 1);
            } else if (request.getParameter("player2") != null) {
                matchService.addPoint(match, 2);
            } else {
                throw new IllegalArgumentException("Player is not selected");
            }
            fillScoreTable(request, match);
            response.sendRedirect(
                    request.getContextPath() + "/match-score?uuid=" + matchId
            );

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());

            RequestDispatcher dispatcher =
                    request.getRequestDispatcher("/WEB-INF/jsp/new-match.jsp");

            dispatcher.forward(request, response);
        }
    }
    
	private void fillScoreTable(HttpServletRequest request, Match match) {
		request.setAttribute("player1", match.getPlayer1().getName());
		request.setAttribute("player2", match.getPlayer2().getName());
		
		request.setAttribute("player1score", match.getPlayer1().getScore());
		request.setAttribute("player2score", match.getPlayer2().getScore());
		
		request.setAttribute("player1game", match.getPlayer1().getGame());
		request.setAttribute("player2game", match.getPlayer2().getGame());
		
		request.setAttribute("player1set", match.getPlayer1().getSet());
		request.setAttribute("player2set", match.getPlayer1().getSet());
	}
}