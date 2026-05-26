package repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import model.Match;

@AllArgsConstructor
public class MatchRepository {
	private EntityManager entityManager;
	
	public List<Match> findByName(String name, int offset) {
	    String hql = """
	            select m
	            from Match m
            	join fetch m.player1 p1
                join fetch m.player2 p2
                join fetch m.winner w
	            """;

	    boolean hasNameFilter = name != null && !name.isBlank();

	    if (hasNameFilter) {
	        hql += """
	                where lower(p1.name) like lower(:name)
	                   or lower(p2.name) like lower(:name)
	                """;
	    }

	    hql += " order by m.id desc";

	    TypedQuery<Match> query = entityManager
	            .createQuery(hql, Match.class)
	            .setFirstResult(offset)
	            .setMaxResults(5);

	    if (hasNameFilter) {
	        query.setParameter("name", "%" + name.trim() + "%");
	    }

	    return query.getResultList();
	}
}
