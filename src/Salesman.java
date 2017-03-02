import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Map.Entry;

public class Salesman {

	int mapSize;

	List<City> allNodes = new ArrayList<City>();
	List<City> openSet = new ArrayList<City>();
	List<City> closedSet = new ArrayList<City>();

	City start;
	City goal;

	String file;

	public Salesman(String filename) {
		file = filename;
		processFile();
		setHeuristics();
		setParents();
	}

	public double dist(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
	}

	/**
	 * Returns euclidean distance between a and b
	 */
	public double dist(City a, City b) {
		return dist(a.getX(), a.getY(), b.getX(), b.getY());
	}

	/**
	 * Print important information.
	 */
	public void printStatus() {
		System.out.println("Finding Route from " + start.name + " to " + goal.name);
		System.out.println("Open: ");
		for (City c : openSet) {
			c.print();
		}
		System.out.println("Closed Set: ");
		for (City c : closedSet) {
			c.print();
		}
	}

	/**
	 * Sets hVal for all nodes. hVal is distance to goal.
	 */
	void setHeuristics() {
		for (City c : openSet) {
			c.setHeuristic(dist(c, goal));
		}
		for (City c : closedSet) {
			c.setHeuristic(dist(c, goal));
		}
	}

	double heuristicEstimate(City c) {
		return dist(c, goal);
	}

	/**
	 * Sets parents of all nodes to start
	 */
	void setParents() {
		for (City c : openSet) {
			c.setParent(start);
		}
		start.setParent(start);
	}

	void closeNode(City c) {
		if (openSet.contains(c)) {
			openSet.remove(c);
			closedSet.add(c);
		} else {
			System.err.println("error on closeNode " + c.getName());
			printStatus();
		}
	}

	void openNode(City c) {
		if (closedSet.contains(c)) {
			closedSet.remove(c);
			openSet.add(c);
		} else {
			System.err.println("error on openNode " + c.getName());
		}
	}

	TreeMap<Double, City> scoreOpenSet() {
		TreeMap<Double, City> scoreMap = new TreeMap<Double, City>();
		for (City c : openSet) {
			scoreMap.put(c.getFScore(), c);
		}
		return scoreMap;
	}

	TreeMap<Double, City> expandNode(City c) {
		closedSet.add(c);
		TreeMap<Double, City> distMap = new TreeMap<Double, City>(); // liskov
																		// sub
																		// principal?
		System.out.print("Expanding node: ");
		c.print();
		for (City i : openSet) { // and isnt in teh explored list
			if (!i.equals(c) && !closedSet.contains(i)) {
				double dist = dist(c, i);
				distMap.put(dist, i);
				System.out.println(i.info() + " -- " + dist);
			}
		}
		return distMap;
	}

	/**
	 * Reads from file. Sets initial openSet, closedSet
	 */
	public void processFile() {
		try {
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				String line;
				mapSize = Integer.parseInt(br.readLine());
				while ((line = br.readLine()) != null) {
					String[] parts = line.split(" ");
					City c = new City(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
					if (closedSet.size() == 0) {// start value goes in closed
												// set
						closedSet.add(c);
					} else {
						openSet.add(c);
					}
					allNodes.add(c);
				}
				if (mapSize >= 2) { // 0 or 1 value means A* is dumb
					start = closedSet.get(0);
					goal = openSet.get(openSet.size() - 1);
				} else if (mapSize == 1) {
					start = closedSet.get(0);
					goal = start;
				} else {
					System.err.println("No Nodes detected");
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	/**
	 * Runner Method
	 */
	public void AStar() {
		while (!openSet.isEmpty()) {
			// current node is node in open set with lowest score
			City node_current = scoreOpenSet().firstEntry().getValue();
			// if we are at the goal
			/*
			 * if (node_current.equals(goal) && openSet.size() == 1) {
			 * System.out.println("found it"); // exit; }
			 */
			// close the current node
			closeNode(node_current);
			// expand nodes
			List<City> neighbors = allNodes;
			neighbors.remove(node_current);
			for (City node_successor : neighbors) {
				printStatus();
				// dont want to move from ourself to ourself

				Boolean goodNode = true;
				//
				if (closedSet.contains(node_successor)) {
					goodNode = false;
				}
				// cost to add successor to path
				double successor_current_cost = node_current.getGScore() + dist(node_current, node_successor);
				//
				if (successor_current_cost >= node_successor.getGScore()) {
					goodNode = false;
				}
				if (goodNode) {
					node_successor.setParent(node_current);
					node_successor.setGScore(successor_current_cost);
					node_successor.setFScore(node_successor.getGScore() + heuristicEstimate(goal));
				}

			}
		}
		// call optimalPath
	}

	public List<City> optimalPath(City c) {
		List<City> Visited = new ArrayList<City>();
		City current = c;
		while (current.getParent() != null) {
			Visited.add(current);
			current = current.getParent();
		}
		return Visited;
	}

	public static void main(String[] args) {
		Salesman sam = new Salesman("./problem/randTSP/4/instance_1.txt");
		sam.printStatus();
		System.out.println("___________");
		sam.AStar();
		// sam.printStatus();
	}
}
