package service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import config.JpaConfig;
import dto.NewMatchDTO;
import jakarta.persistence.EntityManager;
import model.Match;
import model.Player;
import repository.PlayerRepository;

public class MatchService {

    private final ConcurrentHashMap<UUID, Match> currentMatches = new ConcurrentHashMap<>();

    public UUID createNewMatch(NewMatchDTO matchDTO) {
        EntityManager entityManager = JpaConfig.getEntityManager();

        try {
            entityManager.getTransaction().begin();

            PlayerRepository playerRepository =
                    new PlayerRepository(entityManager);

            Player player1 = extractPlayer(playerRepository, matchDTO.player1());
            Player player2 = extractPlayer(playerRepository, matchDTO.player2());

            validatePlayers(player1, player2);

            Match match = new Match();
            match.setPlayer1(player1);
            match.setPlayer2(player2);

            UUID matchUuid;

            do {
                matchUuid = UUID.randomUUID();
            } while (currentMatches.putIfAbsent(matchUuid, match) != null);

            entityManager.getTransaction().commit();

            return matchUuid;

        } catch (RuntimeException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw e;

        } finally {
            entityManager.close();
        }
    }

    public Match getMatch(UUID id) {
        Match match = currentMatches.get(id);

        if (match == null) {
            throw new IllegalArgumentException("Match not found");
        }

        return match;
    }

    public void addPoint(Match match, int playerNumber) {
        synchronized (match) {
            Player pointWinner;
            Player opponent;

            if (playerNumber == 1) {
                pointWinner = match.getPlayer1();
                opponent = match.getPlayer2();
            } else if (playerNumber == 2) {
                pointWinner = match.getPlayer2();
                opponent = match.getPlayer1();
            } else {
                throw new IllegalArgumentException("Invalid player number");
            }

            applyPoint(match, pointWinner, opponent);
        }
    }

    private void applyPoint(Match match, Player pointWinner, Player opponent) {

        if (match.getWinner() != null) {
            throw new IllegalStateException("Match is already finished");
        }

        if (pointWinner.isAdvantage()) {
            winGame(match, pointWinner, opponent);
            return;
        }

        if (opponent.isAdvantage()) {
            opponent.setAdvantage(false);
            return;
        }

        if (pointWinner.getScore() == 40 && opponent.getScore() == 40) {
            pointWinner.setAdvantage(true);
            return;
        }

        if (pointWinner.getScore() == 40 && opponent.getScore() < 40) {
            winGame(match, pointWinner, opponent);
            return;
        }

        pointWinner.nextScore();
    }
    
    private void winGame(Match match, Player gameWinner, Player opponent) {
        gameWinner.addGame();

        gameWinner.resetScore();
        opponent.resetScore();

        checkSetWin(match, gameWinner, opponent);
    }
    
    private void checkSetWin(Match match, Player setWinner, Player opponent) {
        if (setWinner.getGame() >= 6 &&
            setWinner.getGame() - opponent.getGame() >= 2) {

            setWinner.addSet();

            setWinner.resetGame();
            opponent.resetGame();

            checkMatchWin(match, setWinner);
        }
    }
    
    private void checkMatchWin(Match match, Player matchWinner) {
        if (matchWinner.getSet() >= 2) {
            match.setWinner(matchWinner);
        }
    }

    private Player extractPlayer(PlayerRepository playerRepository, String name) {
        String normalizedName = normalizeName(name);

        Optional<Player> playerOptional =
                playerRepository.findByName(normalizedName);

        return playerOptional.orElseGet(
                () -> playerRepository.createByName(normalizedName)
        );
    }

    private String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be empty");
        }

        return name.trim();
    }

    private void validatePlayers(Player player1, Player player2) {
        if (player1.getId() != null && player1.getId().equals(player2.getId())) {
            throw new IllegalArgumentException("Players must be different");
        }
    }
}