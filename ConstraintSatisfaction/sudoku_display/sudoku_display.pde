import java.util.List;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Collections;
import java.util.Random;

int[][] grid = new int[9][9];
// starting sudoku, so we can display it if we want
int[][] startGrid = new int[9][9];
// processing opens a new thread for getting files, so i need this
boolean readInFile = false;
// do i wanna show the solved puzzle?
boolean displaySolved = false;
sudoku_solver solver = new sudoku_solver();

void setup() {
  // seting up the image and processing the file
  size(500, 500);
  background(255);
  stroke(0);
  strokeWeight(4);
  fill(255);
  textAlign(CENTER, CENTER);
  textFont(createFont("Georgia", 32));

  selectInput("Pick a puzzle to solve", "processFile");
}
public void processFile(File selection) {
  try {
    BufferedReader br = new BufferedReader(new FileReader(selection)); 
    String line; 
    int lineCount = 0;
    while ((line = br.readLine()) != null) {
      //println(line);
      String[] parts = line.split(" ");
      if (parts.length == 9) {
        for (int i = 0; i < parts.length; i++) {
          grid[i][lineCount] = Integer.parseInt(parts[i]);
        }
        lineCount++;
      }
    }

    br.close();
    readInFile = true;
    println("read in file: " + selection.getName() + " from " + selection.getParent());

    solver.solve(grid);
    startGrid = solver.startingGrid;
  } 
  catch (Exception e) {
    System.err.println(e);
  }
}

public void draw() {
  if (!readInFile) {
    return;
  }
  background(255);
  for (int i = 0; i < grid.length; i++) {
    for (int j = 0; j < grid[i].length; j++) {
      strokeWeight(2);
      fill(255);
      rect(i*50+23, j*50+2, 50, 50);
      fill(0);
      int t;
      if (displaySolved)
        t = grid[i][j];
      else 
      t = startGrid[i][j];
      if (t!=0)
        text(t, i*50+48, j*50+23);
      strokeWeight(6);
      if (i == 8 && j%3 == 0)
      {
        line(23, 50*j+2, 473, 50*j+2);
      }
    }

    if (i%3 == 0) {
      line(i*50+23, 4, i*50+23, 450);
    }
  }
  line(9*50+23, 4, 9*50+23, 450);
  line(23, 50*9+2, 473, 50*9+2);
  if (displaySolved) {
    text(solver.opCount + " ops",250,470);
  } else 
  {
    text("Click to solve / unsolve",250,470);
  }
}

void mouseClicked() {
  displaySolved = !displaySolved;
}