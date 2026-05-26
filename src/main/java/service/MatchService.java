package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import config.JpaConfig;
import dto.NewMatchDTO;
import jakarta.persistence.EntityManager;
import model.Match;
import model.Player;
import repository.MatchRepository;
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

            UUID uuid = registerOngoingMatch(match);
            
            entityManager.getTransaction().commit();

            return uuid;

        } catch (RuntimeException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw e;

        } finally {
            entityManager.close();
        }
    }
    
    public UUID registerOngoingMatch(Match match) {
        UUID uuid;

        do {
            uuid = UUID.randomUUID();
        } while (currentMatches.putIfAbsent(uuid, match) != null);

        return uuid;
    }

    public Match getMatch(UUID id) {
        Match match = currentMatches.get(id);

        if (match == null) {
            throw new IllegalArgumentException("Match not found");
        }

        return match;
    }

    public boolean addPointAndCheckFinishMatch(Match match, int playerNumber) {
        synchronized (match) {
            if (checkFinish(match)) {
                throw new IllegalStateException("Match is already finished");
            }

            Player pointWinner = playerNumber == 1
                    ? match.getPlayer1()
                    : match.getPlayer2();

            Player opponent = playerNumber == 1
                    ? match.getPlayer2()
                    : match.getPlayer1();

            if (match.isTieBreak() || isSixSix(match)) {
                match.setTieBreak(true);
                applyTieBreakPoint(match, pointWinner, opponent);
                return false;
            }

            applyRegularPoint(match, pointWinner, opponent);
            
            if(checkFinish(match)) {
            	return true;
            }
            return false;
        }
    }
    
    public List<Match> filterMatches(String name, int page) {
    	EntityManager entityManager = JpaConfig.getEntityManager();
    	
    	try {
			MatchRepository matchRepository = new MatchRepository(entityManager);
			return matchRepository.findByName(name, (page-1)*5);
		} finally {
			entityManager.close();
		}
	}
    
    public Long countPage() {
    	EntityManager entityManager = JpaConfig.getEntityManager();
    	try {
    		Long count = entityManager.createQuery("select count(*) from Match", Long.class).getSingleResult();
    		Long pageNumQuotient = count / 5;
    		Long pageNumRemainder = count % 5;
    		if (pageNumRemainder == 0) {
				return pageNumQuotient;
			}
    		return pageNumQuotient + 1;
		} finally {
			entityManager.close();
		}
	}
    
    private boolean checkFinish(Match match) {
		return match.getWinner() != null;
	}
    
    public void finishMatch(UUID matchId, Match match) {
        EntityManager entityManager = JpaConfig.getEntityManager();

        try {
            entityManager.getTransaction().begin();

            Player player1 = entityManager.merge(match.getPlayer1());
            Player player2 = entityManager.merge(match.getPlayer2());
            Player winner = entityManager.merge(match.getWinner());

            match.setPlayer1(player1);
            match.setPlayer2(player2);
            match.setWinner(winner);

            entityManager.merge(match);

            entityManager.getTransaction().commit();

            currentMatches.remove(matchId);

        } catch (RuntimeException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw e;

        } finally {
            entityManager.close();
        }
    }

    private void applyRegularPoint(Match match, Player pointWinner, Player opponent) {
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
    
    private boolean isSixSix(Match match) {
        return match.getPlayer1().getGame() == 6
                && match.getPlayer2().getGame() == 6;
    }
    
    private void applyTieBreakPoint(Match match, Player pointWinner, Player opponent) {
        pointWinner.addTieBreakPoint();

        if (pointWinner.getTieBreakScore() >= 7
                && pointWinner.getTieBreakScore() - opponent.getTieBreakScore() >= 2) {

            pointWinner.addSet();

            pointWinner.resetGame();
            opponent.resetGame();

            pointWinner.resetScore();
            opponent.resetScore();

            pointWinner.resetTieBreakScore();
            opponent.resetTieBreakScore();

            match.setTieBreak(false);

            checkMatchWin(match, pointWinner);
        }
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