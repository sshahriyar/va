
package oaanbc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * This class is provides the fundamental data mining functions using Weka's API
 *
 * @author <p>
 *         Syed Shariyar Murtaza
 *         </p>
 *
 */

public class MyWeka2 {

	private Instances data;

	private ConfusionTable confusionTable = null;

	/**
	 *
	 * @return
	 */
	public ConfusionTable getConfusionTable() {
		return this.confusionTable;
	}

	/**
	 * Performs stratified sampling on the data according to given number of
	 * partitions(folds or stratas)
	 *
	 * @param arffFile
	 * @param attributeNameToRemove
	 * @param totalFolds
	 * @return
	 * @throws Exception
	 */
	public Instances stratifyData(String arffFile,
			String attributeNameToRemove, int totalFolds) throws Exception {
		Random rand = new Random(1);
		Instances randData = loadDataSets(arffFile, attributeNameToRemove)[0];

		randData.randomize(rand);
		if (randData.classAttribute().isNominal())
			randData.stratify(totalFolds);
		else
			throw new Exception("Nominal class not found");
		return randData;
	}

	/**
	 * Creates training set and test set for the specific fold from a given data
	 * set.
	 *
	 * @param randData
	 * @param totalFolds
	 * @param foldNumber
	 * @param sourceDir
	 * @throws Exception
	 */
	public void createDataForSpecificFold(Instances randData, int totalFolds,
			int foldNumber, String sourceDir) throws Exception {

		Instances train, test;

		train = randData.trainCV(totalFolds, foldNumber);
		test = randData.testCV(totalFolds, foldNumber);
		DataSink.write(sourceDir + File.separator + "trainset.arff", train);
		DataSink.write(sourceDir + File.separator + "testset.arff", test);

	}

	/**
	 * Resamples a given ARFF file to increase minority classes
	 * @param arffFile
	 * @throws Exception
	 */
	public void resampledDataSet(String arffFile) throws Exception{

		Instances tempData = loadDataSets(arffFile, "")[0];
			weka.filters.supervised.instance.Resample reSamp = new weka.filters.supervised.instance.Resample();
			reSamp.setInputFormat(tempData);
			String Fliteroptions = "-B 0.5 -Z 200";
			reSamp.setOptions(weka.core.Utils
					.splitOptions(Fliteroptions));
			reSamp.setRandomSeed((int) System.currentTimeMillis());
			tempData = weka.filters.supervised.instance.Resample
					.useFilter(tempData, reSamp);
		DataSink.write(arffFile, tempData);

	}

	/**
	 * Generates One Against All Files from a given training set file. The file
	 * must be in ARFF format
	 *
	 * @param pathName
	 * @param predictionFile
	 * @param rankFile
	 * @param databaseName
	 * @param strLengthForRanking
	 * @param strMultipleLengths
	 * @param freqOrConf
	 * @param tableName
	 * @param result
	 * @param resultFile
	 * @throws java.lang.Exception
	 */
	public void generate_1aginstall_files(String pathName,
			String folderNameForPairWiseFiles, boolean isResampling,
			String attributeNameToRemove) throws Exception {
		data = loadDataSets(pathName, attributeNameToRemove)[0];

		for (int numClasses = 0; numClasses < data.numClasses(); numClasses++) {

			Instances tempData = new Instances(data);

			String classVal = tempData.classAttribute().value(numClasses);

			String otherVal = "others";

			if (!classVal.equalsIgnoreCase(otherVal)) {
				System.out.println("Generating file for " + classVal);

				for (int numInstances = 0; numInstances < tempData
						.numInstances(); numInstances++) {

					if (!tempData.instance(numInstances)
							.stringValue(tempData.classIndex())
							.equalsIgnoreCase(classVal)) {

						tempData.instance(numInstances).setClassValue(otherVal);

					}

				}

				if (isResampling == true) {

					//weka.filters.supervised.instance.ClassBalancer  reSamp=new weka.filters.supervised.instance.ClassBalancer();
					weka.filters.supervised.instance.Resample reSamp = new weka.filters.supervised.instance.Resample();
					reSamp.setInputFormat(tempData);
					String Fliteroptions = "-B 0.5 -Z 200";
					reSamp.setOptions(weka.core.Utils
							.splitOptions(Fliteroptions));
					reSamp.setRandomSeed((int) System.currentTimeMillis());
					tempData = weka.filters.supervised.instance.Resample
							.useFilter(tempData, reSamp);
				}

				String fileName = folderNameForPairWiseFiles + File.separator
						+ classVal.replace(":", "_") + ".arff";
				DataSink.write(fileName, tempData);

			}

		}

	}

	/**
	 * Loads a dataset in ARFF format
	 *
	 * @param folderPath
	 * @param attributeNameToRemove
	 * @return a Data set of instnaces
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public Instances[] loadDataSets(String folderPath,
			String attributeNameToRemove) throws FileNotFoundException,
			IOException, Exception {

		File folders = new File(folderPath);
		File f[] = folders.listFiles();
		Instances[] dataSets = null;
		int fileCount;

		if (f != null) { // if it is a folder
			dataSets = new Instances[f.length];
			fileCount = f.length;
		} else // if it is not a folder
		{
			dataSets = new Instances[1];
			fileCount = 1;
			f = new File[1];
			f[0] = folders;

		}

		for (int fCnt = 0; fCnt < fileCount; fCnt++) {

			dataSets[fCnt] = new Instances(new BufferedReader(new FileReader(
					f[fCnt].getPath())));

			String[] options = new String[2];
			options[0] = "-R"; // "range"

			if (!attributeNameToRemove.isEmpty()
					&& dataSets[fCnt].attribute(attributeNameToRemove) != null) {

				options[1] = Integer.toString(dataSets[fCnt]
						.attribute("uniqNo").index() + 1);

				Remove remove = new Remove(); // new instance of filter
				remove.setOptions(options); // set options
				remove.setInputFormat(dataSets[fCnt]); // inform filter about
														// dataset AFTER setting
														// options
				dataSets[fCnt] = Filter.useFilter(dataSets[fCnt], remove);
			}

			dataSets[fCnt].setClassIndex(dataSets[fCnt].numAttributes() - 1);// *****
																				// Class
																				// Index
																				// --
																				// set
																				// it
																				// for
																				// test
																				// also

		}
		return dataSets;
	}

	/**
	 * Initializes the settings
	 *
	 * @param fileName
	 * @throws IOException
	 */

	public void initialize(String fileName) throws IOException {

		BufferedWriter outputStream = new BufferedWriter(new FileWriter(
				fileName, false));
		outputStream.close();

	}

	/**
	 * Creates multiple classifiers from the folder of one-against -all files.
	 * One classifier for each file.
	 *
	 * @param folderTrainData
	 * @param testFile
	 * @param totalInputClasses
	 * @param rankingFile
	 * @param attributeNameToRemove
	 * @throws Exception
	 */
	public void multipleClassifiers(String folderTrainData, String testFile,
			Double totalInputClasses, String rankingFile,

			String attributeNameToRemove) throws Exception {

		Classifier[] classifiers = null;
		System.out.println("Loading Classifiers");

		// output predictions on test set
		System.out.println("Loading Test Data");
		Instances test = loadDataSets(testFile, attributeNameToRemove)[0];
		// /////////////////////////////
		// Getting total classes and their names to initalizes confusion table
		Integer totalClasses = totalInputClasses.intValue(); // test.classAttribute().numValues();
																// // as they
																// are in the
																// test set
		Integer totalRecords = test.numInstances();
		Enumeration e = test.classAttribute().enumerateValues();
		String[] classes = new String[totalClasses];
		int cnt = 0;
		while (e.hasMoreElements()) {
			String val = e.nextElement().toString();
			if (!val.equals("others"))
				classes[cnt++] = val;
		}
		confusionTable = new ConfusionTable(totalClasses, classes, totalRecords);

		System.out.println("Loading data");
		Instances[] datasets = loadDataSets(folderTrainData,
				attributeNameToRemove);
		// Note: you're not allowed to violate Weka's underlying assumption,
		// that all classifiers got trained on the same data. Hence the
		// structure of the datasets must be exactly the same. The data
		// itself can differ though.
		for (int i = 1; i < datasets.length; i++) {
			if (!datasets[0].equalHeaders(datasets[i]))
				throw new IllegalStateException("Training sets not compatible!");
		}

		// train classifiers
		System.out.println("Building Classifier");
		classifiers = new Classifier[datasets.length];

		for (int i = 0; i < datasets.length; i++) {

			classifiers[i] = new weka.classifiers.bayes.NaiveBayesMultinomial();

			classifiers[i].buildClassifier(datasets[i]);

			//if (!modelFile.isEmpty())
			//	weka.core.SerializationHelper.write(modelFile, classifiers[i]);

		}

		// }

		System.out.println("Testing data");
		// ///////////////////////////////////////////////////////////// testing

		BufferedWriter outputStream = new BufferedWriter(new FileWriter(
				rankingFile, false));

		for (int i = 0; i < test.numInstances(); i++) {

			TreeMap<Double, String> sortedVals = new TreeMap<Double, String>();
			for (int clCnt = 0; clCnt < classifiers.length; clCnt++) {

				//double pred = classifiers[clCnt].classifyInstance(test.instance(i));
				double dist[] = classifiers[clCnt].distributionForInstance(test
						.instance(i));

				for (int u = 0; u < dist.length; u++)
					if (!test.classAttribute().value(u)
							.equalsIgnoreCase("others")
							&& dist[u] > 0) {
						String cls = test.classAttribute().value(u) + " ";

						String classVals = sortedVals.get(dist[u]);

						if (classVals == null) {
							sortedVals.put(dist[u], cls);

						} else {
							classVals = classVals + " , " + cls;
							sortedVals.put(dist[u], classVals);
						}

					}

			}

			// ////////////////////////////
			String correctClass = test.instance(i).stringValue(
					test.classIndex());

			printCompareRanks(sortedVals, correctClass, outputStream,
					totalInputClasses);

		}

		outputStream.flush();
		outputStream.close();

	}

	/**
	 * Calculates predictions' ranks
	 *
	 * @param sortValue
	 * @param correctClassVal
	 * @param outputStream
	 * @param totalClasses
	 * @param avgTracker
	 * @throws IOException
	 */
	public void printCompareRanks(TreeMap<Double, String> sortValue,
			String correctClassVal, BufferedWriter outputStream,
			double totalClasses) throws IOException, Exception {

		outputStream.newLine();

		String[] components = correctClassVal.split(":"); // if there are more
															// than one
															// compoonents/functions

		int rankCounter = 0;// Rank counter: To find out the ranking of real
							// faulty function
		Double worstCaseRank = 0.0;
		Double bestCaseRank = 0.0;
		boolean isTheRealCause = false;

		for (Map.Entry<Double, String> itEpRanks : sortValue.descendingMap()
				.entrySet()) {

			rankCounter++;
			// Write rank and prediction to the file
			outputStream.write(itEpRanks.getKey().toString() + " = "
					+ itEpRanks.getValue() + " ; ");

			int fLength = 0;

			fLength = itEpRanks.getValue().split(",").length;

			isTheRealCause = false;

			String rankPrediction = itEpRanks.getValue();

			for (int j = 0; j < components.length; j++)
				// if there re multiple functions/components
				// compare each

				if (rankPrediction.contains(components[j] + " ")) {

					isTheRealCause = true;// break;

					break;
				}
			// If the real cause from predictions has been found then break out
			// of the loop
			if (isTheRealCause == true) {

				worstCaseRank += fLength;

				bestCaseRank++;
				confusionTable.updateCorrectPrediction(rankCounter,
						correctClassVal);

				break;
			} else {
				worstCaseRank += fLength;
				bestCaseRank += fLength;
				confusionTable.updateWrongPrediction(rankCounter,
						correctClassVal, rankPrediction);
			}

		}

		// if the real cause is not found in the list
		if (isTheRealCause == false) {
			worstCaseRank = totalClasses;
			bestCaseRank = totalClasses;
			// confusionTable.updateWrongPrediction(rankCounter,
			// correctClassVal, rankPrediction);
		}

		// Output prediction
		outputStream.newLine();
		outputStream.write(" Rank  " + rankCounter + " : Worst Case Rank:  "
				+ (worstCaseRank) + "Best Cae Rank: " + (bestCaseRank)
				+ " Actual Cause:" + correctClassVal);
		outputStream.newLine();
		outputStream.flush();

	}

}