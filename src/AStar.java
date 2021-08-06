import java.nio.channels.ClosedChannelException;
import java.util.PriorityQueue;

public class AStar {
	// chi phí cho việc di chuyển theo đường chéo và dọc / ngang
	public static final int diagonalCost = 14; // chi phí đường chéo
	public static final int straightCost = 10; // chi phí đường ngang dọc
	// các ô của lưới của chúng tôi
	private Cell[][] grid;
	//  xác định một hàng đợi ưu tiên cho các ô đang mở
	// open list: tập hợp các nút được đánh giá
	//  đặt các ô có chi phí thấp nhất vào đầu tiên
	private PriorityQueue<Cell> openList;
	// Closed List: tập hợp các nút đã được đánh giá
	private boolean[][] closeList;
	// start cell
	private int startI, startJ;
	// end cell
	private int endI, endJ;

	// khởi tạo chương trình A*
	public AStar(int width, int height, int si, int sj, int ei, int ej, int [][] blocks) {
		grid = new Cell[width][height]; // làm lưới có độ dài bằng biến truyền vào
		closeList = new boolean[width][height]; // khởi tạo closeList
		//sắp xếp open list
		openList = new PriorityQueue<Cell>((Cell c1, Cell c2)->{
			return c1.finalCost < c2.finalCost ? -1 : c1.finalCost > c2.finalCost ? 1 : 0;
		});
		startCell(si,sj); // khởi tạo điểm bắt đầu
		endCell(ei,ej); // khởi tạo điểm kết thúc
		
		// init heuristic
		for(int i =0; i < grid.length ; i++)
		{
			for(int j = 0; j < grid[i].length; j++) {
				grid[i][j] = new Cell(i, j); // khởi tạo ô
				grid[i][j].heuristicCost = Math.abs(i - endI) + Math.abs(j - endJ); // tạo giá trị h (giá trị được lượng giá từ trạng thái hiện tại đến trạng thái đích) càng gần số càng nhỏ
				grid[i][j].solution = false; // xác nhận ô ban đầu là chưa được xử lí
			}
		}
		
		grid[startI][startJ].finalCost = 0; // tổng chi phí ở ô bắt đầu là không
		
		// bỏ những ô chặn trên lưới - giá trị  = null
		for(int i = 0; i < blocks.length ; i++) {
			addBlockOnCell(blocks[i][0], blocks[i][1]);
		}
	}
		public void addBlockOnCell(int i, int j) {
			grid[i][j] = null;
		}
		
		public void startCell(int i, int j) {
			startI = i;
			startJ = j;
		}
		
		public void endCell(int i, int j) {
			endI = i;
			endJ = j;
		}
		
		// cập nhật chi phí đường đi và node ba mẹ của node hiện tại
		public void updateCostIfNeeded(Cell current, Cell t, int cost) {
			if(t == null || closeList[t.i][t.j]) // close là danh sách đã được duyệt thì ko cần duyệt nữa
				return;
			
			int tFinalCost = t.heuristicCost + cost; // tính giá trị đường đi từ current tới t
			
			boolean isOpen = openList.contains(t); // kiếm tra t đã tồn tại trong danh sách hay chưa
			
			
			//  sau đó lấy so sánh với f(u) hiện tại của t nếu nhỏ hơn hoặc chưa có trong openList thì đổi giá trị  và parrent
			if(!isOpen || cost < t.GCost) { // nếu đường đi của node hiện tại bé hơn hoặc chưa có trong open thì cập nhật
				t.finalCost = tFinalCost; 
				t.GCost = cost;
				t.parent = current;				
			}
			if(!isOpen) {
				openList.add(t);
			}
		}
		
		public void process() {
			//chúng ta thêm vị trí bắt đầu để mở danh sách
			openList.add(grid[startI][startJ]);
			Cell current;
			
			while(true) {
			current = openList.poll();
			
			if(current == null)
				break;
			
			closeList[current.i][current.j] = true;
			
			if(current.equals(grid[endI][endJ]))
				return;
			
			Cell t;
			 // kiểm tra nhưng node xung quanh và cập nhật lại chi phí
			if(current.i - 1 >= 0) {
				t = grid[current.i -1][current.j];
				updateCostIfNeeded(current, t, current.GCost + straightCost);
				
				if(current.j - 1 >= 0) {
					t = grid[current.i - 1][current.j - 1];
					updateCostIfNeeded(current, t, current.GCost + diagonalCost);
				}
				
				if(current.j + 1 < grid[0].length) {
					t = grid[current.i - 1][current.j + 1];
					updateCostIfNeeded(current, t, current.GCost + diagonalCost);
				}			
			}
			
			if(current.j - 1 >= 0) {
				t = grid[current.i][current.j - 1];
				updateCostIfNeeded(current, t, current.GCost + straightCost);
			}
			
			if(current.j + 1 < grid[0].length) {
				t = grid[current.i][current.j + 1];
				updateCostIfNeeded(current, t, current.GCost + straightCost);
			}
			
			if(current.i + 1 < grid.length) {
				t = grid[current.i + 1][current.j];
				updateCostIfNeeded(current, t, current.GCost + straightCost);
				
				if(current.j - 1 >= 0) {
					t = grid[current.i + 1][current.j - 1];
					updateCostIfNeeded(current, t, current.GCost + diagonalCost);
				}
				
				if(current.j + 1 < grid[0].length) {
					t = grid[current.i + 1][current.j + 1];
					updateCostIfNeeded(current, t, current.GCost + diagonalCost);
				}
			}
			
			
			}
		}
		
		public void display() {
			System.out.println("Grid: ");
			
			for(int i = 0; i < grid.length; i++) {
				for(int j = 0; j < grid[i].length; j++) {
					if(i == startI && j == startJ)
						System.out.print("SO  ");  // source cell
					else if(i == endI && j == endJ) // destination cell
						System.out.print("DE  ");
					else if(grid[i][j] != null)
						System.out.printf("%-3d ", 0);
					else
						System.out.print("BL  "); // block cell
				}
				System.out.println();
			}
			System.out.println();
		}
		
		public void displayScores() {
			System.out.println("\nScores for cells: ");
			
			for(int i =0; i < grid.length ; i++) {
				for (int j = 0; j < grid[i].length; j++) {
					
					if(grid[i][j] != null) 
						System.out.printf("%-3d ", grid[i][j].finalCost);
						else
							System.out.print("BL  ");				
				}
				System.out.println();
			}
			System.out.println();
		}
		
		public void displaySolution() {
			System.out.println("display Solution:");
			if(closeList[endI][endJ]) {
				// we track back the path
				System.out.print("path: ");
				Cell current = grid[endI][endJ];
				System.out.print(current);
				grid[current.i][current.j].solution = true;
				
				while(current.parent != null) {
					System.out.print(" -> " + current.parent);
					grid[current.parent.i][current.parent.j].solution = true;
					current = current.parent;
				}
				
				System.out.println("\n");
				
				for(int i =0; i < grid.length; i++) {
					for(int j = 0; j < grid[i].length; j++) {
						if(i == startI && j == startJ)
							System.out.print("SO  ");  // source cell
						else if(i == endI && j == endJ) // destination cell
							System.out.print("DE  ");
						else if(grid[i][j] != null)
							System.out.printf("%-3s ", grid[i][j].solution ? "X" : "0");
						else
							System.out.print("BL  "); // block cell
					}
					System.out.println();
				}
				System.out.println();
			}else
				System.out.println("no possible path");
		}
		
		
		public static void main(String[] args) {
			
			int[][] listBlock = new int[][] { {2,2} , {3,1} , {2,1} ,{0,4} , {2,3}};
			int[] startPoint = new int[] {0,0};
			int[] endPoint = new int[] {3,2};
			int[] lengthGrid = new int[] {5,5};
			AStar aStar = new AStar(lengthGrid[0], lengthGrid[1], startPoint[0], startPoint[1], endPoint[0], endPoint[1], listBlock);
			aStar.display();
			aStar.process(); // apply A* Algorithm 
			aStar.displayScores(); // display scores on grid
			aStar.displaySolution(); // display solution path
		}

}
