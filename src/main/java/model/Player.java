package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "Name")
    private String name;

    @Transient
    private int score = 0;

    @Transient
    private int game = 0;

    @Transient
    private int set = 0;
    
    @Transient
    private boolean advantage = false;

    public void nextScore() {
        if (score == 0) {
            score = 15;
        } else if (score == 15) {
            score = 30;
        } else if (score == 30) {
            score = 40;
        } else {
            throw new IllegalStateException("Cannot increment score from " + score);
        }
    }

    public void resetScore() {
        score = 0;
    }

    public void addGame() {
        game++;
    }

    public void resetGame() {
        game = 0;
    }

    public void addSet() {
        set++;
    }
}