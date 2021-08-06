
public class Cell {
	// coordinates public 
	int i, j; 
	// parent cell for path public 
	public Cell parent; 
	// Heuristic cost of the current cell public 
	int heuristicCost; // h(current)
	// chi phi cuối cùng
	public int finalCost; // f(current) 
	public int GCost; // G(current) 
	// Gn chi phí của đường dẫn từ nút bắt đầu đến n
	// and Hn the heuristic that estimates the cost of the cheapest path from n to the goal
	public boolean solution; // if cell is part of the solution path
	
	public Cell(int i, int j) {
	
		this.i = i;
		this.j = j;
	}


	@Override public String toString() {
	return "[" + i + ", " + j + "]";
	}
}
