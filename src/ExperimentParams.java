
public class ExperimentParams {
	public int globalCounter;  //done
	public double bias;//done
	public int numberKeys;//done
	public int numberPrimaryCellsPerKey;//done
	public int numberBackupCellsPerKey;//done
	public int numberOfCells;//done
	public int numberOfKeysPerCell;//done
	public int numberOfPages;//done
	public int numberOfBuffersPerPage;//done
	public int numberOfKeysPerBuffer;//done
	public double loadFactor;//done
	public int pageSize;//done
	public int numberOfBuffers; //done 
	
	public void setParams (int numberOfCells, int numberKeysPerCell,
			int numberOfPrimaryCellsPerKey, int numberOfBackupCellsPerKey,
			double bias, int globalCounter, double loadFactor, int pageSize, int numberBuffersPerPage){
		this.numberOfCells = numberOfCells;
		this.numberOfKeysPerCell = this.numberOfKeysPerBuffer = numberKeysPerCell;
		this.numberPrimaryCellsPerKey = numberOfPrimaryCellsPerKey;
		this.numberBackupCellsPerKey = numberOfBackupCellsPerKey;
		this.bias = bias;
		this.globalCounter = globalCounter;
		this.loadFactor = loadFactor;
		this.numberKeys = (int) (loadFactor * this.numberOfCells * this.numberOfKeysPerCell);
		this.numberOfBuffersPerPage = numberBuffersPerPage;
		this.pageSize = pageSize;
		this.numberOfPages = this.numberOfCells / this.pageSize;
		this.numberOfBuffers = this.numberOfPages * this.numberOfBuffersPerPage;
	}
}
