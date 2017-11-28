package oaanbc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the Confusion Table for multiple ranks of predictions.
 * It measures cumulative sensitivity, specificity, PCCC and CSMF Accuracy for
 * all the ranks; where total ranks = total classes.
 *
 * @author <p>
 *         Syed Shariyar Murtaza
 *         </p>
 */
public class ConfusionTable {
	private static enum Metrics {
		TP, FP, FN, TN
	};

	private HashMap<Integer, HashMap<String, HashMap<Metrics, Integer>>> confusionTable;
	private Integer totalRecords;
	private Integer totalClasses;
	private Double[] sensitivity;
	private Double[] specificity;
	private Double[] PCCC;
	private Double[] csmfAccuracy;

	public Double[] getSensitivity() {
		return sensitivity;
	}

	public Double[] getSpecificity() {
		return specificity;
	}

	public Double[] getPCCC() {
		return PCCC;
	}

	public Double[] getCsmfAccuracy() {
		return csmfAccuracy;
	}

	/**
	 *
	 * @param totalClasses
	 * @param uniqueClasses
	 */
	ConfusionTable(Integer totalClasses, String[] uniqueClasses, Integer records) {
		System.out.println("All Classes: " + Arrays.toString(uniqueClasses));
		confusionTable = new HashMap<>();
		Integer ranks = totalClasses;// -1;
		this.totalClasses = totalClasses;
		for (int i = 0; i < ranks; i++) {

			HashMap<String, HashMap<Metrics, Integer>> rank = new HashMap<>();

			for (int k = 0; k < uniqueClasses.length; k++) {
				if (!uniqueClasses[k].equalsIgnoreCase("others")) {
					HashMap<Metrics, Integer> theClass = new HashMap<>();

					theClass.put(Metrics.TP, 0);
					theClass.put(Metrics.FP, 0);
					theClass.put(Metrics.TN, 0);
					theClass.put(Metrics.FN, 0);

					rank.put(uniqueClasses[k], theClass);
				}
			}// end for unqiue classes

			confusionTable.put(i, rank);
		} // end for rank
		totalRecords = records;

		sensitivity = new Double[ranks];// totalclassess is equal to total ranks
		specificity = new Double[ranks];
		PCCC = new Double[ranks];
		csmfAccuracy = new Double[ranks];

	}

	/**
	 *
	 * @param rank
	 * @param correctClass
	 */
	public void updateCorrectPrediction(Integer rank, String correctClass) {
		correctClass = correctClass.trim();
		rank = rank - 1;
		HashMap<String, HashMap<Metrics, Integer>> theRank = confusionTable
				.get(rank);
		HashMap<Metrics, Integer> theClass = theRank.get(correctClass);

		Integer tp = theClass.get(Metrics.TP);
		theClass.put(Metrics.TP, (tp + 1));
		// theRank.put(correctClass, theClass);

		for (Map.Entry<String, HashMap<Metrics, Integer>> classes : theRank
				.entrySet()) {
			String key = classes.getKey();
			if (!key.equalsIgnoreCase(correctClass)) {
				HashMap<Metrics, Integer> metric = classes.getValue();
				Integer tn = metric.get(Metrics.TN);
				metric.put(Metrics.TN, (tn + 1));
			}

		}

	}

	/**
	 *
	 * @param rank
	 * @param correctClass
	 * @param predictedClass
	 */
	public void updateWrongPrediction(Integer rank, String correctClass,
			String predictedClass) {

		correctClass = correctClass.trim();
		predictedClass = predictedClass.trim();
		rank = rank - 1;
		// System.out.println("rank corr pred "+ rank + " "+correctClass+
		// " "+predictedClass);
		if (predictedClass.contains(","))
			predictedClass = predictedClass.split(",")[0].trim();

		HashMap<String, HashMap<Metrics, Integer>> theRank = confusionTable
				.get(rank);

		// Updat FN for the correct class
		HashMap<Metrics, Integer> theClass = theRank.get(correctClass);
		Integer fn = theClass.get(Metrics.FN);
		theClass.put(Metrics.FN, (fn + 1));

		// Updat FP for the predicted class
		theClass = theRank.get(predictedClass);
		Integer fp = theClass.get(Metrics.FP);
		theClass.put(Metrics.FP, (fp + 1));
		// theRank.put(correctClass, theClass);

		// Update TN for all other classes
		for (Map.Entry<String, HashMap<Metrics, Integer>> classes : theRank
				.entrySet()) {
			String key = classes.getKey();
			if (!key.equalsIgnoreCase(correctClass)
					&& !key.equalsIgnoreCase(predictedClass)) {
				HashMap<Metrics, Integer> metric = classes.getValue();
				Integer tn = metric.get(Metrics.TN);
				metric.put(Metrics.TN, (tn + 1));
			}

		}

	}

	/**
    *
    */
	public void calculateMeasures() {
		System.out.println();
		Double overallSensDenom = 0.0;
		Double overallSensNom = 0.0;
		Double overallSpecDenom = 0.0;
		Double overallSpecNom = 0.0;

		Integer[] allPositivePredForClass = new Integer[totalClasses];
		Integer[] allActualPositives = new Integer[totalClasses];
		Arrays.fill(allActualPositives, 0);
		Arrays.fill(allPositivePredForClass, 0);

		// Double overallCsmfMin=0.0;
		// Double overallCsmfNumerator=0.0;
		// Double overallCsmfAccu=0.0;
		for (Map.Entry<Integer, HashMap<String, HashMap<Metrics, Integer>>> ranks : confusionTable
				.entrySet()) {
			// first get the ranks
			Integer tn = 0;
			Integer fp = 0;
			Integer tp = 0;
			Integer fn = 0;
			System.out.println("rank " + (ranks.getKey() + 1));

			HashMap<String, HashMap<Metrics, Integer>> theRank = ranks
					.getValue();

			// second get the classess
			Double csmfMin = 0.0;
			Double csmfNumerator = 0.0;
			int classCount = 0;
			for (Map.Entry<String, HashMap<Metrics, Integer>> classes : theRank
					.entrySet()) {
				//String key = classes.getKey();

				// third get the metrics
				HashMap<Metrics, Integer> metric = classes.getValue();

				tn = tn + metric.get(Metrics.TN);
				fp = fp + metric.get(Metrics.FP);

				tp = tp + metric.get(Metrics.TP);
				fn = fn + metric.get(Metrics.FN);

				// // calcualting CSMF accuracy
				allPositivePredForClass[classCount] += metric.get(Metrics.TP);
				// Integer
				// positivePredForClass=metric.get(Metrics.TP)+metric.get(Metrics.FP);
				Integer positivePredForClass = allPositivePredForClass[classCount]
						+ metric.get(Metrics.FP);

				Double csmfPred = positivePredForClass.doubleValue()
						/ totalRecords.doubleValue();

				allActualPositives[classCount] += metric.get(Metrics.TP);
				// Integer
				// actualPositivesForClass=metric.get(Metrics.TP)+metric.get(Metrics.FN);
				Integer actualPositivesForClass = allActualPositives[classCount]
						+ metric.get(Metrics.FN);

				Double csmfCorrect = actualPositivesForClass.doubleValue()
						/ totalRecords.doubleValue();
				if (csmfMin == 0.0 || csmfCorrect < csmfMin) {
					csmfMin = csmfCorrect;
				}
				classCount++;

				csmfNumerator = csmfNumerator
						+ Math.abs(csmfCorrect - csmfPred);

			}
			Double csmfDenominator = 2 * (1 - csmfMin);
			Double csmfAccuracy = 1 - (csmfNumerator / csmfDenominator);

			overallSpecNom = overallSpecNom + tn.doubleValue();
			overallSpecDenom = overallSpecNom + fp.doubleValue();

			overallSensNom = overallSensNom + tp.doubleValue();
			overallSensDenom = overallSensNom + fn.doubleValue();

			Double specificityOverall = overallSpecNom / (overallSpecDenom);
			Double sensitivityOverall = overallSensNom / (overallSensDenom);

			// Calculating PCCC=(TP-(k/N))/(1-(k/N))
			// k = top 1 , 2 ,..n and N= 15/classes
			int rankNum = ranks.getKey();
			double KoverN = (double) (rankNum + 1) / (double) totalClasses;
			double OneMinusKoverN = 1 - KoverN;
			double PCCC = (sensitivityOverall - KoverN) / OneMinusKoverN;

			this.sensitivity[rankNum] = sensitivityOverall;
			this.specificity[rankNum] = specificityOverall;
			this.PCCC[rankNum] = PCCC;
			this.csmfAccuracy[rankNum] = csmfAccuracy;

			System.out.println(" sensitivity: " + sensitivityOverall
					+ " speficity: " + specificityOverall + " PCCC: " + PCCC
					+ " csmf accuracy: " + csmfAccuracy);

		}

	}

	/**
	 * Print the confusion table for each rank
	 */
	public void print() {

		System.out.println();
		for (Map.Entry<Integer, HashMap<String, HashMap<Metrics, Integer>>> ranks : confusionTable
				.entrySet()) {
			// first get the ranks
			System.out.println("rank " + (ranks.getKey() + 1));
			HashMap<String, HashMap<Metrics, Integer>> theRank = ranks
					.getValue();

			// second get the classess
			for (Map.Entry<String, HashMap<Metrics, Integer>> classes : theRank
					.entrySet()) {
				String key = classes.getKey();

				// third get the metrics
				HashMap<Metrics, Integer> metric = classes.getValue();

				System.out.println(" class:" + key + " TN:"
						+ metric.get(Metrics.TN) + " TP "
						+ metric.get(Metrics.TP) + " FP:"
						+ metric.get(Metrics.FP) + " FN:"
						+ metric.get(Metrics.FN));

			}
		}

	}

	/**
	 * Classes sorted by their sensitibity (kinda accuarcy here) per rank
	 *
	 * @return
	 */
	public Map<String, Double> calculateMeasuresPerClass() {

		System.out.println();
		java.util.Map<String, Double> sortedClasses = new java.util.TreeMap<String, Double>();

		for (Map.Entry<Integer, HashMap<String, HashMap<Metrics, Integer>>> ranks : confusionTable
				.entrySet()) {
			// first get the ranks
			System.out.println("For rank " + (ranks.getKey() + 1));
			HashMap<String, HashMap<Metrics, Integer>> theRank = ranks
					.getValue();

			// / code for sorters by value
			// java.util.SortedSet<Map.Entry<String, Double>> sortedSet= new
			// java.util.TreeSet<>(new ValueComparator());
			// java.util.Map<String,Double> sortedClasses=new
			// java.util.TreeMap<String,Double>();

			// second get the classess
			for (Map.Entry<String, HashMap<Metrics, Integer>> classes : theRank
					.entrySet()) {
				String key = classes.getKey();

				// third get the metrics
				HashMap<Metrics, Integer> metric = classes.getValue();

				Double sensitivityForClass = ((double) metric.get(Metrics.TP))
						/ ((double) (metric.get(Metrics.TP) + metric
								.get(Metrics.FN)));
				if (!sensitivityForClass.isNaN())
					sortedClasses.put(key, sensitivityForClass);

				// System.out.println(" class:"+key);//+" TN:"+metric.get(Metrics.TN)+
				// " TP "+
				// metric.get(Metrics.TP)+ " FP:"+metric.get(Metrics.FP)+
				// " FN:"+metric.get(Metrics.FN));

			}

			// /sortedSet.addAll(sortedClasses.entrySet());

			// System.out.println(sortedSet);
			break; // just do it only for rank 1\

		}
		return sortedClasses;
	}

	/**
	 * A Simple Unit test
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		String[] a = { "1", "2", "3", "4" };
		ConfusionTable t = new ConfusionTable(4, a, 6);
		t.updateCorrectPrediction(1, "1");
		t.updateCorrectPrediction(1, "2");
		t.updateCorrectPrediction(1, "3");
		t.updateCorrectPrediction(1, "1");
		t.updateWrongPrediction(1, "1", "4");
		t.updateWrongPrediction(1, "1", "2");
		t.print();

		t.calculateMeasuresPerClass();
		// t.calculateMeasures();
	}
}
