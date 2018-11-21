package GUIGame;

import MineSweeperGameDefineException.IllegalGameMove;
import Models.Game.*;
import Models.Grid.Square;
import Models.Move.MoveType;
import Models.Move.PlayerMove;
import Models.Player.Player;
import Models.Player.PlayerStatus;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import static java.lang.Math.max;

public class GUIGame extends NormalGame {
    protected List<PlayerPanel> PlayersPanel =new ArrayList<>() ;
    protected PlayerPanel currentPanel;
    // <__ DATA MEMBERS __> \\

    protected PlayerMove currentPlayerMove;
    protected static Double ConstBorder=400.0;

    protected GridPane FXgrid;
    protected VBox ScoreBoard;
    protected HBox footer;

    protected HBox top;

    protected Scene scene;
    protected BorderPane layout;

    protected Button BackButton;
    protected GUIGameMainMenu Begin;

    // in footer
    protected Label LastMoveLabel,FlagsNumberLabel,shieldNumberLabel;


    class GUITimer extends Timer{
        public GUITimer(){
            super();
        }
        public GUITimer(int t){
            super(t);
        }
        @Override
        public void Show(int Time) {
            currentPanel.setTime(Time);
        }

        @Override
        public void EndTimer() {
            currentPlayer.stop();
            moves=new ArrayList<>();
            currentRules.DecideNextPlayer(moves);
            updateTimer();
        }
    }
    public void updateTimer() {
        UpdateVeiw(moves);
        if (status != GameStatus.Finish) {
            currentTimer = new GUITimer(currentPlayer.getTimeforTimer());
            currentTimer.setDaemon(true);
            currentTimer.start();
            if (!(currentPlayer instanceof GUIPlayer))
                GetMove();
        }
    }
    // <__ CONSTRUCTOR __> \\
    public GUIGame(List _players){
        super(_players);
        initScene();
    }
    public GUIGame(int Width, int Height, int NumMines, List ListOfPlayers) {// Constructor
        super(Width,Height,NumMines,ListOfPlayers);
        initScene();
    }
    public GUIGame(int Width, int Height, int NumMines, List _players, Points points, WhenHitMine pressMineBehavior,WhenScoreNegative scoreNegativeBehavior){
        super(Width,Height,NumMines,_players,points,pressMineBehavior,scoreNegativeBehavior);
        initScene();
    }

    // <__ GETTERS-SETTERS __> \\
    //Getters;

    public GridPane getFXgrid() { return FXgrid; }
    public VBox getScoreBoard() { return ScoreBoard; }
    public Scene getScene() { return scene; }
    public HBox getFooter() {return footer; }
    public void setTop(PlayerPanel _panel) {
        this.top =_panel.getTopPanel();
        currentPanel=_panel;
    }
    public GUIGameMainMenu getBegin() { return Begin; }

    public void setBegin(GUIGameMainMenu begin) { Begin = begin; }

    protected void initScene() {
        initFXComponoents();
        layout=new BorderPane();
        layout.setCenter(FXgrid);
        layout.setRight(ScoreBoard);
        layout.setBottom(footer);
        layout.setTop(top);
        layout.getStyleClass().add("padding");
        scene = new Scene(layout);
        scene.getStylesheets().add("Styles/style.css");
    }



    private void initFXComponoents() {
        initGrid();
        initScoreBoard();
        initfooter();
    }

    private void initGrid() {
        ConstBorder= Double.valueOf(Math.min(max(grid.getWidth(),grid.getHeight()) * 50,600));
        // initialize Grid
        FXgrid=new GridPane();
        FXgrid.getStyleClass().add("grid");
        FXgrid.getStylesheets().add("Styles/style.css");
        for(int i=1;i<this.grid.getHeight();i++){
            for(int j=1;j<this.grid.getWidth();j++){
                Button currentbutton=new Button();

                currentbutton.getStylesheets().add("Styles/style.css");
                //SettingSize
                double buttonborder = ConstBorder / max(this.grid.getHeight()-1, this.grid.getWidth()-1);
                //System.out.println(buttonborder + " " + grid.getHeight() + " " +grid.getWidth());
                currentbutton.setMaxSize(buttonborder, buttonborder);
                currentbutton.setMinSize(buttonborder, buttonborder);
                //Set Action
                currentbutton.setOnMouseClicked(e->{
                    if(currentPlayer instanceof GUIPlayer) {
                        currentPlayerMove = new PlayerMove(currentPlayer, new Square(GridPane.getRowIndex(currentbutton), GridPane.getColumnIndex(currentbutton)));
                        if (e.getButton() == MouseButton.PRIMARY) currentPlayerMove.setType(MoveType.Reveal);
                        else currentPlayerMove.setType(MoveType.Mark);
                        GetMove();
                    }
                    // TODO: Some Exception for Thar :p
                });
                FXgrid.add(currentbutton, j, i);
            }
        }
    }

    protected void initScoreBoard() {
        // Initialize ScoreBoard
        ScoreBoard = new VBox(20);
        ScoreBoard.setMinWidth(200);
        PlayersPanel=new ArrayList<PlayerPanel>();
        ScoreBoard.setStyle("-fx-alignment: CENTER;");

        for(Player _player:super.players){
            PlayerPanel _playerPanel=new PlayerPanel(_player);
            PlayersPanel.add(_playerPanel);
            ScoreBoard.getChildren().add(_playerPanel.getLeftPanel());
            if(_player==currentPlayer){
                setTop(_playerPanel);
            }
        }
    }
    private void initfooter() {
        // init Last Move Label
        footer=new HBox();
        footer.setPadding(new Insets(20));
        footer.setSpacing(80);
        footer.setAlignment(Pos.CENTER);

        LastMoveLabel =new Label();
        FlagsNumberLabel =new Label("Flags: "+ FlagsNumber +"");
        shieldNumberLabel=new Label("Shields: " +ShildNumber + "");
        FlagsNumberLabel.getStyleClass().addAll("buttonlabel","h3","padding-sm");
        LastMoveLabel.getStyleClass().addAll("buttonlabel","h3","padding-sm");
        shieldNumberLabel.getStyleClass().addAll("buttonlabel","h3","padding-sm");

        BackButton =new Button("Back");
        BackButton.getStyleClass().addAll("menubutton","h3");
        BackButton.setPrefSize(80,40);

        BackButton.setOnAction(e->{
            currentTimer.interrupt();
            Begin.Window.setScene(Begin.getWelcomescene());
            Begin.Window.centerOnScreen();
        });
        footer.getChildren().addAll(FlagsNumberLabel,shieldNumberLabel,LastMoveLabel, BackButton);
    }

    void GUIGameThreadStart(Thread thread){
        thread.setDaemon(true);
        thread.start();
    }
    @Override
    public void StartGame() {
        Thread StartGameThread=new Thread(new Runnable() {
            @Override
            public void run() {
                UpdateVeiw(moves);
                updateTimer();
            }
        });
        GUIGameThreadStart(StartGameThread);
    }

    @Override
    public void GetMove(){
        Thread GetMoveThread=new Thread(new Runnable() {
            @Override
            public void run() {
                PlayerMove move = currentPlayer.GetPlayerMove();
                if(!(currentPlayer instanceof GUIPlayer))
                    currentPlayerMove=move;
                try {
                    AcceptMove(currentPlayerMove);
                } catch (IllegalGameMove illegalGameMove) {
                    illegalGameMove.handle();
                    if(status== GameStatus.Finish){
                        EndGame();
                    }
                    else if (!(currentPlayer instanceof GUIPlayer)){
                        GetMove();
                    }
                    return;
                }

                // need else some thing wrong input Or Some Thing Like that :3
                if(status== GameStatus.Finish){
                    EndGame();
                }
                else{
                    updateTimer();
                }
            }
        });
        GUIGameThreadStart(GetMoveThread);
    }
    @Override
    protected void UpdateVeiw(List<PlayerMove> Moves){
        Thread UpdateViewThread=new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //     Update Grid View
                        for(PlayerMove currentmove:Moves) {
                            int i = currentmove.getSquare().getX();
                            int j = currentmove.getSquare().getY();
                            int Position = (i - 1) * (grid.getWidth() - 1) + (j - 1);
                            Button currentButton = (Button) FXgrid.getChildren().get(Position);
                            Square currentSquare = currentmove.getSquare();
                            if(currentSquare.hasNormalSield()){
                                ShildNumber--;
                            }
                            switch (currentSquare.getStatus()) {
                                case Closed:
                                    currentButton.getStyleClass().removeAll("pressed", "openedMine", "marked");
                                    currentButton.getStyleClass().add("notpressed");
                                    break;
                                case OpenedEmpty:
                                    currentButton.setStyle("-fx-background-color: " + currentSquare.getColor() + "");
                                    currentButton.getStyleClass().add("pressed");
                                    break;
                                case OpenedNumber:
                                    currentButton.getStyleClass().add("f" + (String.valueOf(currentSquare.getNumberOfSurroundedMines())) + "");
                                    currentButton.setText("" + currentSquare.getNumberOfSurroundedMines());
                                    currentButton.setStyle("-fx-background-color: " + currentSquare.getColor() + "");
                                    currentButton.getStyleClass().add("pressed");

                                    break;
                                case OpenedMine:
                                    currentButton.getStyleClass().addAll("pressed", "openedMine");
                                    break;
                                case Marked:
                                    currentButton.getStyleClass().removeAll("notpressed", "closed");
                                    currentButton.getStyleClass().addAll("pressed", "marked");
                                    break;
                            }
                        }

                        //Update ScoreBoard
                        for(int i=0;i<players.size();i++){
                            Player _player=players.get(i);
                            PlayerPanel _currentpanel=PlayersPanel.get(i);
                            _currentpanel.Update();
                            if(_player==currentPlayer){
                                setTop(_currentpanel);
                            }
                            layout.setTop(top);
                        }

                        // Update footer Move Label
                        String LastMove="--";
                        if(currentPlayerMove!=null)
                            LastMove=String.valueOf(currentPlayerMove.getSquare().getX()) + " --- " + String.valueOf((currentPlayerMove.getSquare().getY()));

                        LastMoveLabel.setText(LastMove);

                        FlagsNumberLabel.setText("Flags: "+ FlagsNumber + "");
                        shieldNumberLabel.setText("Shieldsz: "+ShildNumber + "");
                    }
                });
            }
        });
        GUIGameThreadStart(UpdateViewThread);
    }

    @Override
    protected void EndGame() {
        // show All The mines in The Game
        // and Update View For Shows
        Thread EndGameThread=new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        currentTimer.interrupt();
                        List<PlayerMove> curr = new ArrayList<>();
                        for (Square mineSqauer : grid.getMines()) {
                            mineSqauer.ChangeStatus(currentPlayer, MoveType.Reveal);
                            curr.add(new PlayerMove(currentPlayer, mineSqauer));
                        }
                        UpdateVeiw(curr);

                        // Get The Winner
                        Player winner = players.get(0);
                        for (int i = 0; i < players.size(); i++) {
                            if (players.get(i).getCurrentScore().getScore() > winner.getCurrentScore().getScore()) {
                                winner = players.get(i);
                            }
                        }
                        // Update footer Move Label

                        Label LastMoveLabel = (Label) footer.getChildren().get(0);
                        String WinnerStr = winner.getName() + " Win The Game";
                        if (players.size() == 1) {
                            WinnerStr = winner.getCurrentStatus() == PlayerStatus.Lose ? "You Lose" : "You Win";
                        }
                        winner.setCurrentStatus(PlayerStatus.win);
                        LastMoveLabel.setText(WinnerStr);

                        for (int i = 1; i < grid.getHeight(); i++) {
                            for (int j = 1; j < grid.getWidth(); j++) {
                                int H = (i - 1) * (grid.getWidth() - 1) + (j - 1);
                                Button currentButton = (Button) FXgrid.getChildren().get(H);
                                currentButton.setDisable(true);
                            }
                        }
                    }
                });
            }
        });
        GUIGameThreadStart(EndGameThread);
    }

}
