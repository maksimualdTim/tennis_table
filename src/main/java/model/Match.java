package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Matches")
public class Match {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	
    @ManyToOne
    @JoinColumn(name = "Player1", referencedColumnName = "ID", nullable = false)
    private Player player1;

    @ManyToOne
    @JoinColumn(name = "Player2", referencedColumnName = "ID", nullable = false)
    private Player player2;

    @ManyToOne
    @JoinColumn(name = "Winner", referencedColumnName = "ID", nullable = false)
    private Player winner;
    
    @Transient
    private boolean tieBreak = false;

}
