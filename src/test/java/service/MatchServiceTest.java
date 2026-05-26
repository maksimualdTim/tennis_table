package service;

import model.Match;
import model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MatchServiceTest {

    private MatchService matchService;
    private Match match;
    private Player player1;
    private Player player2;
    private UUID matchId;

    @BeforeEach
    void setUp() {
        matchService = new MatchService();

        player1 = new Player();
        player1.setName("Player 1");

        player2 = new Player();
        player2.setName("Player 2");

        match = new Match();
        match.setPlayer1(player1);
        match.setPlayer2(player2);

        matchId = matchService.registerOngoingMatch(match);
    }

    @Test
    void player1WinsPointAtZeroZero_scoreBecomesFifteenZero() {
        matchService.addPointAndCheckFinishMatch(match, 1);

        assertEquals(15, player1.getScore());
        assertEquals(0, player2.getScore());
        assertEquals(0, player1.getGame());
        assertEquals(0, player2.getGame());
    }

    @Test
    void player1WinsPointAtFifteenZero_scoreBecomesThirtyZero() {
        player1.setScore(15);

        matchService.addPointAndCheckFinishMatch(match, 1);

        assertEquals(30, player1.getScore());
        assertEquals(0, player2.getScore());
    }

    @Test
    void player1WinsPointAtThirtyZero_scoreBecomesFortyZero() {
        player1.setScore(30);

        matchService.addPointAndCheckFinishMatch(match, 1);

        assertEquals(40, player1.getScore());
        assertEquals(0, player2.getScore());
    }

    @Test
    void player1WinsPointAtFortyZero_player1WinsGame() {
        player1.setScore(40);
        player2.setScore(0);

        matchService.addPointAndCheckFinishMatch(match, 1);

        assertEquals(1, player1.getGame());
        assertEquals(0, player2.getGame());

        assertEquals(0, player1.getScore());
        assertEquals(0, player2.getScore());

        assertFalse(player1.isAdvantage());
        assertFalse(player2.isAdvantage());
    }

    @Test
    void player1WinsPointAtFortyForty_gameDoesNotEnd_player1GetsAdvantage() {
        player1.setScore(40);
        player2.setScore(40);

        matchService.addPointAndCheckFinishMatch(match, 1);

        assertEquals(0, player1.getGame());
        assertEquals(0, player2.getGame());

        assertEquals(40, player1.getScore());
        assertEquals(40, player2.getScore());

        assertTrue(player1.isAdvantage());
        assertFalse(player2.isAdvantage());
    }

    @Test
    void player1WinsPointWithAdvantage_player1WinsGame() {
        player1.setScore(40);
        player2.setScore(40);
        player1.setAdvantage(true);

        matchService.addPointAndCheckFinishMatch(match, 1);

        assertEquals(1, player1.getGame());
        assertEquals(0, player2.getGame());

        assertEquals(0, player1.getScore());
        assertEquals(0, player2.getScore());

        assertFalse(player1.isAdvantage());
        assertFalse(player2.isAdvantage());
    }

    @Test
    void player2WinsPointWhenPlayer1HasAdvantage_backToDeuce() {
        player1.setScore(40);
        player2.setScore(40);
        player1.setAdvantage(true);

        matchService.addPointAndCheckFinishMatch(match, 2);

        assertEquals(40, player1.getScore());
        assertEquals(40, player2.getScore());

        assertFalse(player1.isAdvantage());
        assertFalse(player2.isAdvantage());

        assertEquals(0, player1.getGame());
        assertEquals(0, player2.getGame());
    }

    @Test
    void player1WinsGameAtFiveFour_player1WinsSet() {
        player1.setGame(5);
        player2.setGame(4);

        player1.setScore(40);
        player2.setScore(0);

        matchService.addPointAndCheckFinishMatch(match, 1);

        assertEquals(1, player1.getSet());
        assertEquals(0, player2.getSet());

        assertEquals(0, player1.getGame());
        assertEquals(0, player2.getGame());

        assertEquals(0, player1.getScore());
        assertEquals(0, player2.getScore());
    }

    @Test
    void player1WinsGameAtFiveFive_setDoesNotEnd_scoreBecomesSixFive() {
        player1.setGame(5);
        player2.setGame(5);

        player1.setScore(40);
        player2.setScore(0);

        matchService.addPointAndCheckFinishMatch(match, 1);

        assertEquals(0, player1.getSet());
        assertEquals(0, player2.getSet());

        assertEquals(6, player1.getGame());
        assertEquals(5, player2.getGame());
    }

    @Test
    void player1WinsGameAtSixFive_player1WinsSetSevenFive() {
        player1.setGame(6);
        player2.setGame(5);

        player1.setScore(40);
        player2.setScore(0);

        matchService.addPointAndCheckFinishMatch(match, 1);

        assertEquals(1, player1.getSet());
        assertEquals(0, player2.getSet());

        assertEquals(0, player1.getGame());
        assertEquals(0, player2.getGame());
    }

    @Test
    void atSixSix_tiebreakStartsInsteadOfRegularGame() {
        player1.setGame(6);
        player2.setGame(6);

        matchService.addPointAndCheckFinishMatch(match, 1);

        assertTrue(match.isTieBreak());
        assertEquals(1, player1.getTieBreakScore());
        assertEquals(0, player2.getTieBreakScore());

        assertEquals(6, player1.getGame());
        assertEquals(6, player2.getGame());
    }

    @Test
    void player1WinsTiebreakSevenZero_player1WinsSet() {
        match.setTieBreak(true);

        player1.setGame(6);
        player2.setGame(6);

        player1.setTieBreakScore(6);
        player2.setTieBreakScore(0);

        matchService.addPointAndCheckFinishMatch(match, 1);

        assertEquals(1, player1.getSet());
        assertEquals(0, player2.getSet());

        assertEquals(0, player1.getGame());
        assertEquals(0, player2.getGame());

        assertEquals(0, player1.getTieBreakScore());
        assertEquals(0, player2.getTieBreakScore());

        assertFalse(match.isTieBreak());
    }

    @Test
    void tiebreakAtSixSix_player1WinsPoint_tiebreakDoesNotEnd() {
        match.setTieBreak(true);

        player1.setTieBreakScore(6);
        player2.setTieBreakScore(6);

        matchService.addPointAndCheckFinishMatch(match, 1);

        assertEquals(7, player1.getTieBreakScore());
        assertEquals(6, player2.getTieBreakScore());

        assertEquals(0, player1.getSet());
        assertTrue(match.isTieBreak());
    }

    @Test
    void tiebreakAtSevenSix_player1WinsPoint_player1WinsSetEightSix() {
        match.setTieBreak(true);

        player1.setGame(6);
        player2.setGame(6);

        player1.setTieBreakScore(7);
        player2.setTieBreakScore(6);

        matchService.addPointAndCheckFinishMatch(match, 1);

        assertEquals(1, player1.getSet());
        assertEquals(0, player2.getSet());

        assertFalse(match.isTieBreak());
    }

    @Test
    void playerWinsTwoSets_matchIsFinished() {
        player1.setSet(1);

        player1.setGame(5);
        player2.setGame(0);

        player1.setScore(40);
        player2.setScore(0);

        matchService.addPointAndCheckFinishMatch(match, 1);

        assertEquals(player1, match.getWinner());
    }

    @Test
    void cannotAddPointAfterMatchFinished() {
        match.setWinner(player1);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> matchService.addPointAndCheckFinishMatch(match, 1)
        );

        assertEquals("Match is already finished", exception.getMessage());
    }
}