package minesweeper;

import java.util.ArrayList;
import java.util.List;

public abstract class NormalGame extends Game{

    abstract class DefaultRules extends GameRules{ // inner Class
        int GetScoreChange(ArrayList moves){
            return 0;
        }
        Player DecideNextPlayer(ArrayList moves){
            System.out.println("assadasd");
            System.out.println("assadasd"g);
            System.out.println("assadasd");
            return null;
        }
    }
    GameRules currentRules;
    public void ApplyPlayerMove(PlayerMove move) {
        // here We ApPly The move And then Check The Status Of The Game
        List<PlayerMove> PlayerMoves =this.grid.AcceptMove(move);
        Square[][] feild=this.grid.getField();
        int ScoreChange=currentRules.GetScoreChange((ArrayList) PlayerMoves);
        currentPlayer.addScore(ScoreChange);
        currentPlayer=currentRules.DecideNextPlayer((ArrayList) PlayerMoves);
    }
}
