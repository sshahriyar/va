/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package oaanbc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class implements the One-against-ALL- Naive Bayes Classifier (OAA-NBC).
 * The implementation details are present in the research paper.
 *
 * @author <p>
 *         Syed Shariyar Murtaza
 *         </p>
 */
public class OAANBC {

	public void executeOAANBC(String inputFolder, Integer totalFolds,
			Integer totalClassesInput, String attributeNameToRemove,
			String inputPrefix) {
		// if re-sampling of imbalance classes is needed in the generated
		// on-against all files, make this true
		Boolean isResampling = false;

		String finalOutputFile = "OAANBC.txt";

		MyWeka2 objWeka = null;

		try {

			String[] train_Test_Arff = { "", "" };
			String progDir = inputFolder.substring(0,
					inputFolder.lastIndexOf(File.separator));
			objWeka = new MyWeka2();

			String sourceDir = progDir + File.separator + "pairwise_oaanbc";
			String finalOutputDir = progDir + File.separator + "output_oaanbc";
			finalOutputFile = progDir + File.separator + finalOutputFile;

			File outFolder = new File(finalOutputDir);
			if (!outFolder.isDirectory())
				outFolder.mkdirs();

			Double[] sensitivity = new Double[totalClassesInput];// totalclassess
																	// is equal
																	// to total
																	// ranks
			Double[] specificity = new Double[totalClassesInput];
			Double[] PCCC = new Double[totalClassesInput];
			Double[] csmfAccuracy = new Double[totalClassesInput];
			Map<String, Double> sortedClassesForRank1 = new TreeMap<>();

			Arrays.fill(sensitivity, 0.0);
			Arrays.fill(specificity, 0.0);
			Arrays.fill(PCCC, 0.0);
			Arrays.fill(csmfAccuracy, 0.0);

			for (int foldCount = 0; foldCount < totalFolds; foldCount++) {

				File f = new File(sourceDir);

				recursiveDelete(f);

				int count = foldCount + 1;
				String trainFile = inputFolder + File.separator + inputPrefix
						+ count + File.separator + "trainset.arff";
				String testFile = inputFolder + File.separator + inputPrefix
						+ count + File.separator + "testset.arff";

				// clean up

				File cleanupFiles = new File(sourceDir + File.separator
						+ "*.arff");
				cleanupFiles.delete();
				// / cleanup ends

				train_Test_Arff[1] = testFile;
				train_Test_Arff[0] = trainFile;

				String predictionFile = finalOutputDir + File.separator + count
						+ ".txt";
				buildOneAgainstAllModels(train_Test_Arff, "oaanbc_" + count,
						totalClassesInput, objWeka, isResampling, sourceDir,
						attributeNameToRemove, predictionFile);

				ConfusionTable ct = objWeka.getConfusionTable();
				ct.calculateMeasures();
				// ct.print();
				java.util.Map<String, Double> sortedClassByPerc = ct
						.calculateMeasuresPerClass();
				// measures per Rank in each array (i.e., rank = total Classes)
				Double[] lSensitivity = ct.getSensitivity();
				Double[] lSpecificity = ct.getSpecificity();
				Double[] lPCCC = ct.getPCCC();
				Double[] lCsmfAccuracy = ct.getCsmfAccuracy();

				for (int j = 0; j < totalClassesInput; j++) {// saving
																// sensitivitiies
																// sum for
																// multiple
																// repetitions
					sensitivity[j] += lSensitivity[j];
					specificity[j] += lSpecificity[j];
					PCCC[j] += lPCCC[j];
					csmfAccuracy[j] += lCsmfAccuracy[j];

				}

				// code for classes and their percentages in rank 1
				for (Map.Entry<String, Double> it : sortedClassByPerc
						.entrySet()) {
					String key = it.getKey();
					if (!sortedClassesForRank1.containsKey(key))
						// taking care of new classes
						// could be found in different iterations
						sortedClassesForRank1.put(key, it.getValue());
					else {
						Double val = sortedClassesForRank1.get(key);
						val += it.getValue();
						sortedClassesForRank1.put(key, val);

					}
				}

			}// end of repetitions

			BufferedWriter outFile = new BufferedWriter(new FileWriter(
					finalOutputFile));
			String text = "OAANBC Results";

			System.out
					.println("*****************************************************************");
			System.out
					.println("*****************************Final Output************************");
			System.out.println(text);
			System.out
					.println("*****************************************************************");
			System.out.println("Total given folds of the datasets "
					+ totalFolds);

			outFile.write("*****************************************************************");
			outFile.newLine();
			outFile.write("*****************************Final Output************************");
			outFile.newLine();
			outFile.write(text);
			outFile.newLine();
			outFile.write("*****************************************************************");
			outFile.newLine();
			outFile.write("Total repetitions " + totalFolds);
			outFile.newLine();

			for (int rank = 0; rank < totalClassesInput; rank++) {
				sensitivity[rank] = sensitivity[rank] / totalFolds;
				specificity[rank] = specificity[rank] / totalFolds;
				PCCC[rank] = PCCC[rank] / totalFolds;
				csmfAccuracy[rank] = csmfAccuracy[rank] / totalFolds;

				System.out.println("Rank " + (rank + 1) + ": sensitivity= "
						+ sensitivity[rank] + ", specificity= "
						+ specificity[rank] + ", PCCC= " + PCCC[rank]
						+ ", csmf accuracy= " + csmfAccuracy[rank]);

				outFile.write("Rank " + (rank + 1) + ": sensitivity= "
						+ sensitivity[rank] + ", specificity= "
						+ specificity[rank] + ", PCCC= " + PCCC[rank]
						+ ", csmf accuracy= " + csmfAccuracy[rank]);
				outFile.newLine();
			}

			// Code for classes and their percentages in rank 1
			System.out.println();
			System.out.println();
			outFile.newLine();
			outFile.newLine();

			System.out
					.println("Top classes for Rank 1 sorted by their sensitivities are:");
			outFile.write("Top classes in Rank 1 sorted by their sensitivities are:");
			outFile.newLine();

			java.util.SortedSet<Map.Entry<String, Double>> sortedSetByVal = new java.util.TreeSet<>(
					new ValueComparator());

			for (Map.Entry<String, Double> it : sortedClassesForRank1
					.entrySet()) {
				String key = it.getKey();
				Double val = it.getValue();
				val = (val / totalFolds) * 100;
				sortedClassesForRank1.put(key, val);

			}
			sortedSetByVal.addAll(sortedClassesForRank1.entrySet());
			System.out.println(sortedSetByVal);
			outFile.write(sortedSetByVal.toString());
			outFile.newLine();

			outFile.flush();
			outFile.close();

		} catch (Exception ex) {
			// Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null,
			// ex);
			ex.printStackTrace();

		}

	}

	/**
	 * Builds OAA-NBC models
	 *
	 * @param arff_Files
	 * @param program
	 * @param totalInputClasses
	 * @param objWeka
	 * @param isResampling
	 * @param sourceDir
	 * @param attributeNameToRemove
	 * @param predictionFile
	 * @throws Exception
	 */

	public void buildOneAgainstAllModels(String[] arff_Files, String program,
			Integer totalInputClasses, MyWeka2 objWeka, Boolean isResampling,
			String sourceDir, String attributeNameToRemove,
			String predictionFile) throws Exception {

		Double totalClasses = Double.parseDouble(totalInputClasses.toString());
		objWeka.initialize(predictionFile);

		String training = arff_Files[0];

		String testFile = arff_Files[1];
		objWeka.generate_1aginstall_files(training, sourceDir + File.separator
				+ "pairwise", isResampling, attributeNameToRemove);
		objWeka.multipleClassifiers(sourceDir + File.separator + "pairwise",
				testFile, totalClasses, predictionFile, attributeNameToRemove);

	}

	/**
	 * Recursive delete files.
	 */
	void recursiveDelete(File dirPath) throws Exception {
		System.gc();
		String[] ls = dirPath.list();

		if (ls != null)
			for (int idx = 0; idx < ls.length; idx++) {
				File file = new File(dirPath, ls[idx]);
				if (file.isDirectory())
					recursiveDelete(file);
				// System.out.println ("deleting..."+file.getName());
				if (!file.delete())
					throw new FileNotFoundException("unable to delete" + file);
				file = null;
			}

	}

}