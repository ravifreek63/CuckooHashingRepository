import java.util.ArrayList;
import java.util.Random;


public class CuckooGraph {
	public int cellOutDegree[]; // initialized to 0
	public int keyLocation[];   // initialized to -1
	public int bufferOutDegree[]; // initialized to 0
	public int primaryCells[][]; // done
	public int backupCells[][]; // done
	public int keysInCell[][]; // done
	public int keysInBuffer[][];
	public int bufferCellsByPage[][]; 
	public int primaryPageByKey[]; //done
	public int backUpPageByKey[]; //done
	public int primaryKeysCount = 0;
	public int backUpKeysCount = 0;
	public boolean cellPos[][];
	public boolean bufferPos[][];
	public boolean keyInBuffer[];
	public double expectedAccessCount;
	public int numberSteps = 0;
	
	
	public CuckooGraph (ExperimentParams params){
		this.cellOutDegree = new int[params.numberOfCells];
		this.bufferOutDegree = new int[params.numberOfBuffers];
		this.keyLocation = new int[params.numberKeys];
		// Initialize all the keyLocations to -1, keyLocation -1 means the key is not mapped
		for (int count = 0; count < params.numberKeys; count++){
			this.keyLocation[count] = -1;
		}
	}
	
	public void init (ExperimentParams params){
		this.primaryCells = new int[params.numberKeys][params.numberPrimaryCellsPerKey];
		this.backupCells = new int[params.numberKeys][params.numberBackupCellsPerKey];
		this.keysInCell = new int[params.numberOfCells][params.numberOfKeysPerCell];
		this.bufferCellsByPage = new int[params.numberOfPages][params.numberOfBuffersPerPage];
		int bufferCount = 0;
		for (int pageIndex = 0; pageIndex < params.numberOfPages; pageIndex++){
			for (int bufferIndex = 0; bufferIndex < params.numberOfBuffersPerPage; bufferIndex++){
				this.bufferCellsByPage[pageIndex][bufferIndex] = bufferCount;
				bufferCount++;
			}
		}
		this.primaryPageByKey = new int[params.numberKeys];
		this.backUpPageByKey = new int[params.numberKeys];		
		this.keysInBuffer = new int[params.numberOfBuffers][params.numberOfKeysPerBuffer];
		this.cellPos = new boolean [params.numberOfCells][params.numberOfKeysPerCell];
		this.bufferPos = new boolean [params.numberOfBuffers][params.numberOfKeysPerBuffer];
		this.keyInBuffer = new boolean[params.numberKeys];
	}
	
	public void countKeys(ExperimentParams params){
		int keyLocationCell, keyLocationPage;
		for(int keyIndex = 0; keyIndex < params.numberKeys; keyIndex++){
			if (this.keyInBuffer[keyIndex]){
				this.primaryKeysCount++;
			}else{
				keyLocationCell = this.keyLocation[keyIndex];
			if (keyLocationCell != -1){
				keyLocationPage = keyLocationCell / params.pageSize;
				if (keyLocationPage == this.primaryPageByKey[keyIndex])
					this.primaryKeysCount++;
				else
					this.backUpKeysCount++;
			} 
			}
		} 
		this.expectedAccessCount = 1 + ((double)this.backUpKeysCount) / (params.numberKeys);
	} 
	
	public void createGraph(ExperimentParams params){
		int primaryPageIndex, backUpPageIndex, cellIndexPage, cellIndex;
		Random random = new Random ();
		for (int keyIndex = 0; keyIndex < params.numberKeys; keyIndex++){		
			/*   Assigning a primary page and 3 primary cells to each key. */	
			primaryPageIndex = random.nextInt(params.numberOfPages);
			this.primaryPageByKey[keyIndex] = primaryPageIndex;
			ArrayList<Integer> list = new ArrayList <Integer> ();
			for (int cellCount = 0; cellCount < params.numberPrimaryCellsPerKey; cellCount++){
				cellIndexPage = random.nextInt(params.pageSize);
				while (list.contains(cellIndexPage)){				
					cellIndexPage = random.nextInt(params.pageSize);
				}
				list.add(cellIndexPage);			
				cellIndex = cellIndexPage + primaryPageIndex * params.pageSize;
				this.primaryCells[keyIndex][cellCount] = cellIndex;
			}	
			/*   Assigning a backup page and a backup cell to each key. */
			backUpPageIndex = random.nextInt(params.numberOfPages);
			while (primaryPageIndex == backUpPageIndex)
				backUpPageIndex = random.nextInt(params.numberOfPages);
			list = new ArrayList <Integer> ();
			this.backUpPageByKey[keyIndex] = backUpPageIndex;
			for (int cellCount = 0; cellCount < params.numberBackupCellsPerKey; cellCount++){
				cellIndexPage = random.nextInt(params.pageSize);
				while (list.contains(cellIndexPage)){
					cellIndexPage = random.nextInt(params.pageSize);
				}
				list.add(cellIndexPage);			
				cellIndex = cellIndexPage + backUpPageIndex * params.pageSize;
				this.backupCells[keyIndex][cellCount] = cellIndex;
			}	
		}
	}
}
