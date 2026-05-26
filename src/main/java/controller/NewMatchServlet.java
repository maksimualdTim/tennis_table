package controller;

import java.io.IOException;
import java.util.UUID;

import config.AppContext;
import dto.NewMatchDTO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.MatchService;

@WebServlet("/new-match")
public class NewMatchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MatchService matchService;

	@Override
	public void init() throws ServletException {
		matchService = AppContext.getMatchService();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/new-match.jsp");

		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String player1Name = request.getParameter("player1Name");
			String player2Name = request.getParameter("player2Name");

			NewMatchDTO matchDTO = new NewMatchDTO(player1Name, player2Name);

			UUID matchUuid = matchService.createNewMatch(matchDTO);

			response.sendRedirect(request.getContextPath() + "/match-score?uuid=" + matchUuid);

		} catch (Exception e) {
			request.setAttribute("error", e.getMessage());
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/new-match.jsp");
			dispatcher.forward(request, response);
		}
	}
}