package controller;

import java.io.IOException;
import java.util.UUID;

import config.JpaConfig;
import dto.NewMatchDTO;
import jakarta.persistence.EntityManager;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.PlayerRepository;
import service.OngoingMatchService;

@WebServlet("/new-match")
public class NewMatchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RequestDispatcher dispatcher =
                request.getRequestDispatcher("/WEB-INF/jsp/new-match.jsp");

        dispatcher.forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EntityManager entityManager = JpaConfig.getEntityManager();

        try {
        	String player1Name = request.getParameter("player1Name");
        	String player2Name = request.getParameter("player2Name");
        	
        	NewMatchDTO matchDTO = new NewMatchDTO(player1Name, player2Name);

            PlayerRepository playerRepository =
                    new PlayerRepository(entityManager);

            OngoingMatchService matchService =
                    new OngoingMatchService(playerRepository);

            UUID matchUuid = matchService.createNewMatch(matchDTO);

            response.sendRedirect(
            		request.getContextPath() + "/match-score?uuid=" + matchUuid
            );

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            RequestDispatcher dispatcher =
                    request.getRequestDispatcher("/WEB-INF/jsp/new-match.jsp");
            dispatcher.forward(request, response);
        } finally {
            entityManager.close();
        }
    }
}