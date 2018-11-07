package ConsoleGame;
import BaseAlphabit.Converter;
import Models.Game.GameStatus;
import Models.Game.NormalGame;
import Models.Grid.Square;
import Models.Player.Player;
import Models.Move.PlayerMove;
import Models.Player.PlayerStatus;
import userDefineException.IllegalGameMove;
import userDefineException.MineSweeperGameExeption;

import java.util.List;

public class ConsoleGame extends NormalGame {

    public ConsoleGame(List _players){
        super(_players);
    }
    public ConsoleGame(int Width, int Height, int NumMines, List ListOfPlayers) {// Constructor
        super(Width,Height,NumMines,ListOfPlayers);
    }

    @Override
    public void StartGame() {
        UpdateVeiw();
        GetMove();
    }

    @Override
    public void GetMove(){// get The Move From The Console Player and then Apply it
        PlayerMove move = this.currentPlayer.GetPlayerMove();
        try {
            AcceptMove(move);
        } catch (IllegalGameMove illegalGameMove) {
            illegalGameMove.handle();
        }
        if(this.status== GameStatus.Finish){
            EndGame();
        }
        else{
            UpdateVeiw();
            GetMove();
        }
    }
    @Override
    protected void UpdateVeiw() {
        //PrintGrid();
        //print in One row number Of each Column In Grid
        System.out.print("   ");
        for(int i=0;i+1<this.grid.getWidth();i++){
            System.out.print(" "+Converter.valueOf(i));
        }
       // System.out.println();
        Square[][] feild=this.grid.getField();
        for(int i=1;i<this.grid.getHeight();i++){
            System.out.println();
           String number = ConsoleGame.fixedLengthString(String.valueOf(i), 2);
           System.out.print(number);
                     for (int j=1;j<this.grid.getWidth();j++){
                switch (feild[i][j].getStatus()){
                    case Closed:
                        System.out.print("O ");
                        break;
                    case Marked:
                        System.out.print("p ");
                        break;
                    case OpenedMine:
                        System.out.print("B ");
                        break;
                    case OpenedNumber:
                        System.out.print(feild[i][j].getNumberOfSurroundedMines()+" ");
                        break;
                    case OpenedEmpty:
                        System.out.print(". ");
                        break;
                }
            }
        }
        System.out.println();

    }
    @Override
    protected void EndGame() {
        UpdateVeiw();
        System.out.println("scores:");
        Player winner=players.get(0);
        for(int i=0;i<players.size();i++) {
            System.out.println(players.get(i).getName() + ": "+players.get(i).getCurrentScore());
            if(players.get(i).getCurrentScore().getScore()>winner.getCurrentScore().getScore()){
                winner=players.get(i);
            }
        }
        winner.setCurrentStatus(PlayerStatus.win);
        String WinnerStr=winner.getName() + " Win The Game yyyhaaa";
        if(players.size()==1){
            WinnerStr = winner.getCurrentStatus()==PlayerStatus.Lose ?"You Lose" : "You Win";
        }
        winner.setCurrentStatus(PlayerStatus.win);
        System.out.println(WinnerStr);
    }

}
