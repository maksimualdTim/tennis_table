package controller;

import java.io.IOException;
import java.util.List;

import config.AppContext;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Match;
import service.MatchService;

/**
 * Servlet implementation class MatchesServlet
 */
@WebServlet("/matches")
public class MatchesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MatchService matchService;

	@Override
	public void init() throws ServletException {
		matchService = AppContext.getMatchService();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String playerName = request.getParameter("filter_by_player_name");
		String pageString = request.getParameter("page");
		int page;

		try {
			if (pageString != null && !pageString.isEmpty()) {
				page = Integer.parseInt(pageString);
			} else {
				page = 1;
			}

			List<Match> matches = matchService.filterMatches(playerName, page);
			
			request.setAttribute("pageCount", matchService.countPage());
			request.setAttribute("matches", matches);
			request.setAttribute("currentPage", page);
			request.setAttribute("filterName", playerName == null ? "" : playerName);
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/matches.jsp");

			dispatcher.forward(request, response);
		} catch (NumberFormatException e) {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
