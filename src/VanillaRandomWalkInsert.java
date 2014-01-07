import java.util.Random;

public class VanillaRandomWalkInsert implements RandomWalkInsert {
	
	public boolean randomWalkInsert (ExperimentParams params, CuckooGraph cuckooGraph, int key){
		int cellPos;
		boolean success = false;
		int globalCounter = params.globalCounter, cell, randomKey = -1, randomCell;
		Random random = new Random();
		while (!success && globalCounter >0){
			cell = findEmptyCell (cuckooGraph.primaryCells[key], params.numberPrimaryCellsPerKey, 
					params.numberOfKeysPerCell, cuckooGraph.cellOutDegree);
			success = (cell != -1);
			if (success){
				cuckooGraph.cellOutDegree[cell]++;
				cuckooGraph.keyLocation[key] = cell;
				for (cellPos = 0; cellPos < params.numberOfKeysPerCell; cellPos++)
					if (!(cuckooGraph.cellPos[cell][cellPos]))
						break;
				cuckooGraph.keysInCell[cell][cellPos] = key;
				cuckooGraph.cellPos[cell][cellPos] = true;
			} else {
				if (random.nextDouble() < params.bias){
					cellPos = random.nextInt(params.numberOfKeysPerCell);
					randomCell = cuckooGraph.primaryCells[key][random.nextInt(params.numberPrimaryCellsPerKey)];
					randomKey = cuckooGraph.keysInCell[randomCell][cellPos];					
					cuckooGraph.keyLocation[randomKey] = -1;
					cuckooGraph.keyLocation[key] = randomCell;
					cuckooGraph.keysInCell[randomCell][cellPos] = key;
					key = randomKey;
				} else {
					cell = findEmptyCell (cuckooGraph.backupCells[key], params.numberBackupCellsPerKey, 
							params.numberOfKeysPerCell, cuckooGraph.cellOutDegree);
					success = (cell != -1);
					if (cell != -1){
						cuckooGraph.keyLocation[key] = cell;
						cuckooGraph.cellOutDegree[cell]++;		
						for (cellPos = 0; cellPos < params.numberOfKeysPerCell; cellPos++)
							if (!(cuckooGraph.cellPos[cell][cellPos]))
								break;
						cuckooGraph.keysInCell[cell][cellPos] = key;
						cuckooGraph.cellPos[cell][cellPos] = true;
					} else {
						cellPos = random.nextInt(params.numberOfKeysPerCell);
						randomCell = cuckooGraph.backupCells[key][cellPos];
						randomKey = cuckooGraph.keysInCell[randomCell][random.nextInt(params.numberOfKeysPerCell)];
						cuckooGraph.keyLocation[randomKey] = -1;
						cuckooGraph.keyLocation[key] = randomCell;
						cuckooGraph.keysInCell[randomCell][cellPos] = key;
						key = randomKey;
					}
				}
			}
			globalCounter--;
			cuckooGraph.numberSteps++;
		}
		return success;
	}
	
	private int findEmptyCell (int Cells[], int numberCellsPerKey, 
			int outDegree, int outDegreeArray[]){
		int currentCell, currentCellIndex, emptyCell = -1;
		for (currentCellIndex = 0; currentCellIndex < numberCellsPerKey; currentCellIndex++){
			currentCell = Cells[currentCellIndex];
			if (outDegreeArray[currentCell] < outDegree){
				emptyCell = currentCell;
				break;
			} 
		}
		return emptyCell;
	}
}
