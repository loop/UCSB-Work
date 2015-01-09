import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Puzzle implements Comparable<Puzzle> {

	public final static int N = 3;
	public static int theLastX, thelastY;
	public final static int[] path = new int[100000];
	public static int numberOfPath = 0;
	public final static String[] names = { "   ", "  1", "  2", "  3", "  4",
			"  5", "  6", "  7", "  8" };
	public static int nodesOnQueue;
	public final static int[][] solved = { { 1, 2, 3 }, { 4, 5, 6 },
			{ 7, 8, 0 } };
	public int totalMovesCompleted;
	public int[][] numberOfTilesOnSquare;
	public Puzzle mainPuzzle;
	public int priority;
	public int totalDistance;
	public int locationOfZeroX = 0;
	public int locationOfZeroY = 0;

	Puzzle(int[][] tiles) {
		this.numberOfTilesOnSquare = new int[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				this.numberOfTilesOnSquare[i][j] = tiles[i][j];

	}

	Puzzle(int[][] tiles, Puzzle parent, int moves) {
		this.numberOfTilesOnSquare = new int[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				this.numberOfTilesOnSquare[i][j] = tiles[i][j];
		this.mainPuzzle = parent;
		this.totalMovesCompleted = moves;
		priority();
	}

	public int priority() {
		int manhattanDistance = 0;
		for (int x = 0; x < N; x++)
			for (int y = 0; y < N; y++) {
				switch (numberOfTilesOnSquare[x][y]) {

				case 1:
					manhattanDistance = manhattanDistance + differenceOfPosition(x, y, 0, 0);
					break;
				case 2:
					manhattanDistance = manhattanDistance + differenceOfPosition(x, y, 0, 1);
					break;
				case 3:
					manhattanDistance = manhattanDistance + differenceOfPosition(x, y, 0, 2);
					break;
				case 4:
					manhattanDistance = manhattanDistance + differenceOfPosition(x, y, 1, 0);
					break;
				case 5:
					manhattanDistance = manhattanDistance + differenceOfPosition(x, y, 1, 1);
					break;
				case 6:
					manhattanDistance = manhattanDistance + differenceOfPosition(x, y, 1, 2);
					break;
				case 7:
					manhattanDistance = manhattanDistance + differenceOfPosition(x, y, 2, 0);
					break;
				case 8:
					manhattanDistance = manhattanDistance + differenceOfPosition(x, y, 2, 1);
					break;
				case 0:
					manhattanDistance = manhattanDistance + differenceOfPosition(x, y, 2, 2);
					break;
				default:
					break;
				}

			}
		return manhattanDistance + totalMovesCompleted;
	}

	private int differenceOfPosition(int xPos, int yPos, int xGoal, int yGoal) {
		int diff = Math.abs(xPos - xGoal);
		diff += Math.abs(yPos - yGoal);

		return diff;
	}

	@Override
	public int compareTo(Puzzle b) {
		if (b.distance() == distance()) {
			for (int i = 0; i < N; i++) {
				if (!(Arrays.equals(b.getBoard()[i], this.getBoard()[i]))) {
					if (b.priority() > priority())
						return -1;
					return 1;
				}
			}
			return 0;
		} else if (b.priority() > priority())
			return -1;
		else
			return 1;
	}

	public int[][] getBoard() {
		return numberOfTilesOnSquare;
	}

	public boolean puzzleSolved() {
		for (int i = 0; i < N; i++) {
			if (!(Arrays.equals(numberOfTilesOnSquare[i], solved[i]))) {
				return false;
			}

		}
		return true;
	}

	public int[][] examineGoalState() {
		int[][] solved = new int[N][N];
		int nums = 1;

		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				solved[i][j] = nums;
				nums++;
				if (nums == (N * N)) {
					solved[N - 1][N - 1] = 0;
					break;
				}
			}
		return solved;
	}

	private int distance() {
		this.totalDistance = priority() - totalMovesCompleted;
		return this.totalDistance;
	}

	private int getZeroXLocation() {
		for (int x = 0; x < N; x++)
			for (int y = 0; y < N; y++)
				if (numberOfTilesOnSquare[x][y] == 0) {
					locationOfZeroX = x;
					locationOfZeroY = y;
				}
		return locationOfZeroX;
	}

	private int getZeroYLocation() {
		getZeroXLocation();
		return locationOfZeroY;
	}

	private void setZeroLocation() {
		getZeroYLocation();
	}

	public Puzzle[] neighbours() {
		ArrayList<Puzzle> tempneighbors = new ArrayList<Puzzle>();

		setZeroLocation();

		for (int i = -1; i < 2; i++) {
			int p = locationOfZeroX + i;
			if (p < 0 || p > N - 1)
				continue;
			for (int j = -1; j < 2; j++) {
				int q = locationOfZeroY + j;
				if (q < 0
						|| q > N - 1
						|| (p == locationOfZeroX && q == locationOfZeroY)
						|| ((Math.abs(locationOfZeroX - p) + Math
								.abs(locationOfZeroY - q))) > 1)
					continue;

				int[][] tempTiles = new int[N][N];

				for (int m = 0; m < N; m++)
					tempTiles[m] = Arrays.copyOf(numberOfTilesOnSquare[m], N);
				int tempQ = tempTiles[p][q];
				tempTiles[p][q] = 0;
				tempTiles[locationOfZeroX][locationOfZeroY] = tempQ;
				Puzzle neighbor = new Puzzle(tempTiles, this,
						this.totalMovesCompleted + 1);
				tempneighbors.add(neighbor);
				nodesOnQueue++;

			}

		}

		Puzzle[] neighbors = new Puzzle[tempneighbors.size()];

		return tempneighbors.toArray(neighbors);
	}

	public void showSolution() {
		if (mainPuzzle != null)
			mainPuzzle.showSolution();
		System.out.println(toString());
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				s += names[numberOfTilesOnSquare[i][j]] + " ";
				if (numberOfTilesOnSquare[i][j] == 0) {
					// System.out.println("here");
					if (i < theLastX && j == thelastY)
						path[numberOfPath++] = 1;
					else if (i > theLastX && j == thelastY)
						path[numberOfPath++] = -1;
					else if (j < thelastY && i == theLastX)
						path[numberOfPath++] = 2;
					else if (j > thelastY && i == theLastX)
						path[numberOfPath++] = -2;
					theLastX = i;
					thelastY = j;
				}
			}
			s += "\n";
		}

		return s;
	}

	public boolean calculateSolveability() {
		int[] row = new int[(N * N) - 1];
		int rowIndex = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (numberOfTilesOnSquare[i][j] != 0) {
					row[rowIndex] = numberOfTilesOnSquare[i][j];
					rowIndex++;
				} else {
					theLastX = i;
					thelastY = j;
				}

			}
		}

		int puzzleInversions = 0;
		for (int x = 0; x < row.length; x++) {
			for (int y = x; y < row.length; y++)
				if (row[x] > row[y])
					puzzleInversions++;
		}
		return puzzleInversions % 2 == 0;
	}

	public static void main(String[] args) throws FileNotFoundException {
		long startTime = System.currentTimeMillis();
		long endTime = 0;
		int inputEntered = 0;
		int[][] fileIn = new int[N][N];
		String input = args[0];
		String output = args[1];
		Scanner scanner = new Scanner(new File(input));
		String solutionPath = "";
		try {
			FileInputStream fis = new FileInputStream(input);
			char current;
			int i = 0;
			while (fis.available() > 0) {
				current = (char) fis.read();
				if (current == 'X') {
					fileIn[inputEntered][i++] = 0;
				} else if (current == '1') {
					fileIn[inputEntered][i++] = 1;
				} else if (current == '2') {
					fileIn[inputEntered][i++] = 2;
				} else if (current == '3') {
					fileIn[inputEntered][i++] = 3;
				} else if (current == '4') {
					fileIn[inputEntered][i++] = 4;
				} else if (current == '5') {
					fileIn[inputEntered][i++] = 5;
				} else if (current == '6') {
					fileIn[inputEntered][i++] = 6;
				} else if (current == '7') {
					fileIn[inputEntered][i++] = 7;
				} else if (current == '8') {
					fileIn[inputEntered][i++] = 8;
				}

				if (i == 3) {
					inputEntered++;
					i = 0;
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		Puzzle x = new Puzzle(fileIn);
		System.out.println(x);

		PriorityQueue<Puzzle> prioityQueue = new PriorityQueue<Puzzle>();
		prioityQueue.insert(x);

		int nodes = 0;
		ArrayList<Puzzle> seen = new ArrayList<Puzzle>();
		while (!prioityQueue.isEmpty()) {
			x = prioityQueue.deleteMin(); // get the minimum key
			if (nodes == 0) {
				if (!x.calculateSolveability()) {
					System.out
							.println("This is an unsolvable puzzle, please enter a solvable one");
					System.exit(0);
				}
			}
			nodes++;

			for (int i = 0; i < seen.size(); i++) {
				if (x.compareTo(seen.get(i)) == 0) {
					x = prioityQueue.deleteMin();
				}
			}

			seen.add(x);

			if (x.puzzleSolved()) {
				break;
			}

			Puzzle[] neighbors = x.neighbours();
			for (int i = 0; i < neighbors.length; i++)
				if (!(x.compareTo(neighbors[i]) == 0)
						&& neighbors[i].calculateSolveability())
					prioityQueue.insert(neighbors[i]);

		}
		x.showSolution();
		for (int i = 0; i < numberOfPath; ++i) {
			if (path[i] == 1) {
				solutionPath += ('u');
			} else if (path[i] == -1) {
				solutionPath += ('d');
			} else if (path[i] == 2) {
				solutionPath += ('l');
			} else {
				solutionPath += ('r');
			}
		}
		System.out.print("\n");
		endTime = System.currentTimeMillis(); // Get the end Time
		System.out.println((endTime - startTime) + " miliseconds");

		try {
			PrintWriter solution = new PrintWriter(output);
			solution.println(solutionPath + "\n" + (endTime - startTime)
					+ " milliseconds");
			solution.close();
			if (!x.calculateSolveability()) {

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (true) {
			if (startTime > 30000) {
				System.exit(0);
			}
		}
	}

}

class PriorityQueue<Key> implements Iterable<Key> {
	private Key[] pq;
	private int N;
	private Comparator<Key> compare;

	private static Scanner scanner;

	@SuppressWarnings("unchecked")
	public PriorityQueue(int initCapacity) {
		pq = (Key[]) new Object[initCapacity + 1];
		N = 0;
	}

	public PriorityQueue() {
		this(1);
	}

	@SuppressWarnings("unchecked")
	public PriorityQueue(int initCapacity, Comparator<Key> comparator) {
		this.compare = comparator;
		pq = (Key[]) new Object[initCapacity + 1];
		N = 0;
	}

	public PriorityQueue(Comparator<Key> comparator) {
		this(1, comparator);
	}

	@SuppressWarnings("unchecked")
	public PriorityQueue(Key[] keys) {
		N = keys.length;
		pq = (Key[]) new Object[keys.length + 1];
		for (int i = 0; i < N; i++)
			pq[i + 1] = keys[i];
		for (int k = N / 2; k >= 1; k--)
			moveDown(k);
		assert isMinHeap();
	}

	public boolean isEmpty() {
		return N == 0;
	}

	public int size() {
		return N;
	}

	public Key min() {
		if (isEmpty())
			throw new NoSuchElementException("Error");
		return pq[1];
	}

	private void resize(int capacity) {
		assert capacity > N;
		@SuppressWarnings("unchecked")
		Key[] temp = (Key[]) new Object[capacity];
		for (int i = 1; i <= N; i++)
			temp[i] = pq[i];
		pq = temp;
	}

	public void insert(Key x) {
		if (N == pq.length - 1)
			resize(2 * pq.length);

		pq[++N] = x;
		moveUp(N);
		assert isMinHeap();
	}

	public Key deleteMin() {
		if (isEmpty())
			throw new NoSuchElementException("Error");
		swap(1, N);
		Key min = pq[N--];
		moveDown(1);
		pq[N + 1] = null; // avoid loitering and help with garbage collection
		if ((N > 0) && (N == (pq.length - 1) / 4))
			resize(pq.length / 2);
		assert isMinHeap();
		return min;
	}

	private void moveUp(int k) {
		while (k > 1 && isBigger(k / 2, k)) {
			swap(k, k / 2);
			k = k / 2;
		}
	}

	private void moveDown(int z) {
		while (2 * z <= N) {
			int j = 2 * z;
			if (j < N && isBigger(j, j + 1))
				j++;
			if (!isBigger(z, j))
				break;
			swap(z, j);
			z = j;
		}
	}

	@SuppressWarnings("unchecked")
	private boolean isBigger(int i, int j) {
		if (compare == null) {
			return ((Comparable<Key>) pq[i]).compareTo(pq[j]) > 0;
		} else {
			return compare.compare(pq[i], pq[j]) > 0;
		}
	}

	private void swap(int i, int j) {
		Key swap = pq[i];
		pq[i] = pq[j];
		pq[j] = swap;
	}

	private boolean isMinHeap() {
		return isMinHeap(1);
	}

	private boolean isMinHeap(int k) {
		if (k > N)
			return true;
		int left = 2 * k, right = 2 * k + 1;
		if (left <= N && isBigger(k, left))
			return false;
		if (right <= N && isBigger(k, right))
			return false;
		return isMinHeap(left) && isMinHeap(right);
	}

	public Iterator<Key> iterator() {
		return new PuzzleIterator();
	}

	private class PuzzleIterator implements Iterator<Key> {

		private PriorityQueue<Key> duplicate;

		public PuzzleIterator() {
			if (compare == null)
				duplicate = new PriorityQueue<Key>(size());
			else
				duplicate = new PriorityQueue<Key>(size(), compare);
			for (int i = 1; i <= N; i++)
				duplicate.insert(pq[i]);
		}

		public void remove() {
		}

		public Key next() {
			if (!hasNext())
				throw new NoSuchElementException();
			return duplicate.deleteMin();
		}

		public boolean hasNext() {
			return !duplicate.isEmpty();
		}

	}

	public static boolean isEmpty2() {
		return !scanner.hasNext();
	}

}