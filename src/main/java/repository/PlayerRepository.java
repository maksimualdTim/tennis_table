package repository;

import java.util.Optional;


import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.AllArgsConstructor;
import model.Player;

@AllArgsConstructor
public class PlayerRepository {
	private EntityManager entityManager;
	
	public Optional<Player> findByName(String name){
		String hql = "SELECT p FROM Player p WHERE p.name=:name";
		try {
			Player player = entityManager.createQuery(hql, Player.class)
					.setParameter("name", name)
					.getSingleResult();
			return Optional.of(player);
		} catch (NoResultException e) {
			return Optional.empty();
		}

	}
	
    public Player createByName(String name) {
        Player player = new Player();
        player.setName(name);

        entityManager.persist(player);
        return player;
    }
}
