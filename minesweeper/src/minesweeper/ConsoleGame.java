/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minesweeper;

import java.io.Console;

/**
 *
 * @author Omar
 */
public class ConsoleGame extends NormalGame {
    @Override
    protected void printGrid() {
        for(int i=0;i<this.grid.getwidth();i++){

        }
    }

    @Override
    protected void Lose() {
        // must Do some things in Grid make user feel unhappy because he Lose the game 🌚_🌚
        System.out.println("You catch all Mines and win the game!!\n 💃💃💃^___^💃💃💃");
    }

    @Override
    protected void Win() {
        // must Do some things in Grid make user feel happy because he win the game 💙_💙
        System.out.println("You catch all Mines and win the game!!\n 💃💃💃^___^💃💃💃");
    }

    @Override
    public void ApplyPlayerMove(PlayerMove move) {

    }

    @Override
    public boolean AcceptMove(PlayerMove move){return true;}
}
