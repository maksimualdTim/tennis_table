package service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import dto.NewMatchDTO;
import model.Match;
import model.Player;
import repository.PlayerRepository;

public class OngoingMatchService {
	private PlayerRepository playerRepository;
	
	private ConcurrentHashMap<UUID, Match> currentMatches;
	
	public OngoingMatchService(PlayerRepository playerRepository) {
		this.playerRepository = playerRepository;
		currentMatches = new ConcurrentHashMap<UUID, Match>();
	}
	
	public UUID createNewMatch(NewMatchDTO matchDTO) {
		Player player1 = extractPlayer(matchDTO.player1());
		Player player2 = extractPlayer(matchDTO.player2());
		
		Match match = new Match();
		match.setPlayer1(player1);
		match.setPlayer2(player2);

		UUID matchUuid;
		do {
			matchUuid = UUID.randomUUID();
		} while (currentMatches.putIfAbsent(matchUuid, match) != null);
		
		return matchUuid;
	}
	
	private Player extractPlayer(String name) {
		Optional<Player> playerOptional = playerRepository.findByName(name);
		if (playerOptional.isEmpty()) {
			return playerRepository.createByName(name);
		} else {
			return playerOptional.get();
		}
	}
}
