import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

	
public class Experiment {
	private int numberOfCells = (int) Math.pow (10, 5);
	private int numberOfKeysPerCell = 1;
	private int numberOfPrimaryCellsPerKey = 3;
	private int numberOfBackUpCellsPerKey = 1;
	private double bias = 0.97;
	private int globalCounter = 300;
	private double loadFactor = 0.75;
	private int pageSize = (int) Math.pow (10, 3);
	private int numberBuffersPerPage = 10;
	
	public CuckooGraph onlineCuckooHashing (RandomWalkInsert randomWalkInsert, ExperimentParams eParams, 
			Experiment exp){
		CuckooGraph cuckooGraph = new CuckooGraph(eParams);
		cuckooGraph.init(eParams);
		cuckooGraph.createGraph(eParams);		
		int maxKeys = (int) (exp.loadFactor * exp.numberOfCells), keyIndex= 0;
		boolean keyInserted;
		for (keyIndex = 0; keyIndex < maxKeys; keyIndex++){					
			keyInserted = randomWalkInsert.randomWalkInsert (eParams, cuckooGraph, keyIndex);
			if (!keyInserted)
				break;			
		}
		cuckooGraph.countKeys(eParams);
		return cuckooGraph;
	}
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		int numberExperiments = 5;
		double primaryKeyFraction[][] = new double[2][numberExperiments]; 
		double keysInsertedFraction[][] = new double[2][numberExperiments];
		double expectedAccessCount[][] = new double[2][numberExperiments];
		double averageNumberSteps[][] = new double[2][numberExperiments];
		Experiment exp = new Experiment();
		PrintWriter pkfWriter = new PrintWriter("./results/primaryKeyFraction.csv", "UTF-8");
		PrintWriter keysInsertedWriter = new PrintWriter("./results/fractionKeysInsertedFraction.csv", "UTF-8");
		PrintWriter expAccessCountWriter = new PrintWriter("./results/expectedAccessCount.csv", "UTF-8");
		PrintWriter expNumberStepsWriter = new PrintWriter("./results/expectedStepsWriter.csv", "UTF-8");
		while (exp.loadFactor < 0.90){
		int maxKeys = (int)(exp.loadFactor * exp.numberOfCells * exp.numberOfKeysPerCell);
		ExperimentParams eParams = new ExperimentParams ();
		eParams.setParams(exp.numberOfCells, exp.numberOfKeysPerCell, 
				exp.numberOfPrimaryCellsPerKey, exp.numberOfBackUpCellsPerKey,
				exp.bias, exp.globalCounter, exp.loadFactor, exp.pageSize, exp.numberBuffersPerPage);
		for (int experimentIndex = 0; experimentIndex < numberExperiments; experimentIndex++){				
			RandomWalkInsert vanillaRandomWalkInsert = new VanillaRandomWalkInsert ();
			CuckooGraph cuckooGraphNormal = exp.onlineCuckooHashing (vanillaRandomWalkInsert, eParams, exp);
			primaryKeyFraction[0][experimentIndex] = ((double)cuckooGraphNormal.primaryKeysCount / maxKeys);
			keysInsertedFraction[0][experimentIndex] = ((double)(cuckooGraphNormal.primaryKeysCount + cuckooGraphNormal.backUpKeysCount)   / maxKeys);
			expectedAccessCount[0][experimentIndex] = cuckooGraphNormal.expectedAccessCount;
			averageNumberSteps[0][experimentIndex] = ((double)cuckooGraphNormal.numberSteps) / (cuckooGraphNormal.primaryKeysCount + cuckooGraphNormal.backUpKeysCount);
			eParams.pageSize =  exp.pageSize - eParams.numberOfBuffersPerPage;
			eParams.numberOfCells = exp.numberOfCells - eParams.numberOfPages; 
			RandomWalkInsert bufferedRandomWalkInsert = new BufferedRandomWalkInsert ();
			CuckooGraph cuckooGraphBuffered = exp.onlineCuckooHashing (bufferedRandomWalkInsert, eParams, exp);		
			primaryKeyFraction[1][experimentIndex] = ((double)cuckooGraphBuffered.primaryKeysCount / maxKeys);
			keysInsertedFraction[1][experimentIndex] = ((double)(cuckooGraphBuffered.primaryKeysCount + cuckooGraphBuffered.backUpKeysCount)   / maxKeys);
			expectedAccessCount[1][experimentIndex] = cuckooGraphBuffered.expectedAccessCount;
			averageNumberSteps[1][experimentIndex] = ((double)cuckooGraphBuffered.numberSteps) / (cuckooGraphBuffered.primaryKeysCount + cuckooGraphBuffered.backUpKeysCount) ;
		}
		pkfWriter.println(exp.loadFactor +","+ HelperMethods.getAverage(primaryKeyFraction[0]) + ","  + HelperMethods.getAverage(primaryKeyFraction[1]));
		keysInsertedWriter.println(exp.loadFactor +","+ HelperMethods.getAverage(keysInsertedFraction[0]) + ","  + HelperMethods.getAverage(keysInsertedFraction[1]));
		expAccessCountWriter.println(exp.loadFactor +","+ HelperMethods.getAverage(expectedAccessCount[0]) + ","  + HelperMethods.getAverage(expectedAccessCount[1]));
		expNumberStepsWriter.println(exp.loadFactor +","+ HelperMethods.getAverage(averageNumberSteps[0]) + ","  + HelperMethods.getAverage(averageNumberSteps[1]));
		exp.loadFactor += 0.01;
	}
		pkfWriter.close();
		keysInsertedWriter.close();
		expAccessCountWriter.close();
		expNumberStepsWriter.close();
	}
	
}
