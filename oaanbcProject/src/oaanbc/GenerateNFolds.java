package oaanbc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * This class generates N folds of data sets for a given data set. The given
 * data set must be in ARFF format. All new data sets contains a pair of
 * training and test set and are place in different folders.
 *
 * @author <p>
 *         Syed Shariyar Murtaza
 *         </p>
 *
 */
public class GenerateNFolds {

	/**
	 * Generate stratified training and test sets for a given arff file based on
	 * given number of folds(stratas). In summary, this function generates N
	 * pairs of training and tes sets where N being the fold.
	 *
	 * @param inputFile
	 *            Input file
	 * @param totalClassesInput
	 *            Total number of classes in the input file
	 * @param numOfStratas
	 *            Number of stratas
	 * @param outFolder
	 *            Output folder
	 * @param isDirichletSampling
	 *            If Dirichlet distribution based sampling is needed in the test
	 *            set, set this parameter to true.
	 * @param inputPrefix
	 *            Common name that will prefix the folder names for folds
	 * @throws Exception
	 */

	public static void generateFiles(String inputFile,
			Integer totalClassesInput, Integer numOfStratas, String outFolder,
			Boolean isDirichletSampling, String inputPrefix) throws Exception {

		MyWeka2 wekaUtility = null;
		String attributeNameToRemove = "";

		String progDir = inputFile.substring(0,
				inputFile.lastIndexOf(File.separator));
		wekaUtility = new MyWeka2();

		Double[] sensitivity = new Double[totalClassesInput];// total classes is
																// equal to
																// total ranks
		Double[] specificity = new Double[totalClassesInput];
		Double[] PCCC = new Double[totalClassesInput];
		Double[] csmfAccuracy = new Double[totalClassesInput];

		Arrays.fill(sensitivity, 0.0);
		Arrays.fill(specificity, 0.0);
		Arrays.fill(PCCC, 0.0);
		Arrays.fill(csmfAccuracy, 0.0);

		String sourceDir = progDir + File.separator + outFolder;
		File f = new File(sourceDir);
		recursiveDelete(f);

		for (int fold = 0; fold < numOfStratas; fold++) {
			int cnt = fold + 1;

			String finalDir = sourceDir + File.separator + inputPrefix + cnt;

			int testFold = fold;

			weka.core.Instances randData = wekaUtility.stratifyData(inputFile,
					attributeNameToRemove, numOfStratas);
			wekaUtility.createDataForSpecificFold(randData, numOfStratas,
					testFold, finalDir);

			String testFile = finalDir + File.separator + "testset.arff";

			if (isDirichletSampling == true) {

				DirichletSampling dSample = new DirichletSampling();



				testFile = dSample.sampleDataByDirichlet(testFile,
						attributeNameToRemove);
				System.out.println("Dirichlet sampling performed.");
			}
			System.out.println("Test file location: " + testFile);

		}

	}

	/**
	 * Recursively deletes a folder
	 *
	 * @param dirPath
	 * @throws Exception
	 */
	private static void recursiveDelete(File dirPath) throws Exception {
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
