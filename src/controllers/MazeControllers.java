package controllers;

public class MazeControllers {

    public MazeControllers(boolean[][] predefinedMaze) {
        
    }

    public void printMaze() {
        boolean[][] predefinedMaze = {
            {true, true, true, true},
            {false, true, true, true},
            {true, true, false, false},
            {true, true, true, true}
        };
        for (int i = 0; i < predefinedMaze.length; i++) {
            for (int j = 0; j < predefinedMaze[i].length; j++) {
                System.out.print(predefinedMaze[i][j] ? " - " : " * ");
            }
            System.out.println();
        }
    }

    public boolean[][] getGrid() {
        return null;
    }
    

}
