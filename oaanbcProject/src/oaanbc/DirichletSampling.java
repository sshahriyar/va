package oaanbc;

import cc.mallet.types.Dirichlet;

import java.util.Random;

import weka.core.Attribute;
import weka.core.Instances;

import weka.core.converters.ConverterUtils;

/**
 * Class to sample Weka's data set using Dirichlet Distribution
 *
 * @author s
 *         <p>
 *         Syed Shariyar Murtaza
 *         </p>
 */
public class DirichletSampling {

	/**
	 * Returns the sampled file by using Dirichlet
	 *
	 * @param arffFileName
	 *            Input file of a data set in arff format
	 * @param attributeNameToRemove
	 *            Attribute Name of the ID that should be removed
	 * @return
	 */

	public String sampleDataByDirichlet(String arffFileName,
			String attributeNameToRemove) throws Exception {

		MyWeka2 weka = new MyWeka2();

		Instances data = weka.loadDataSets(arffFileName, attributeNameToRemove)[0];

		if (!data.classAttribute().isNominal())
			throw new Exception("No class found");

		int numOfClasses = data.numClasses() - 1;// subtract -1 for others
													// classes
		int numOfRecsToGenerate = data.numInstances();

		// Dirichlet sampling
		// Randomly select alpha parameter for each of the classes
		Random r = new Random();
		double[] alpha = new double[numOfClasses];
		// double p=r.nextDouble();
		for (int alp = 0; alp < alpha.length; alp++)
			alpha[alp] = r.nextDouble();

		Dirichlet d = new Dirichlet(alpha);

		// returns samples for classes (dimensions), equivalent to nmofRecs in
		// quantity
		int[] totalSamples = d.drawObservation(numOfRecsToGenerate);

		System.out.println("\nSample Size");
		for (int i = 0; i < totalSamples.length; i++) {
			if (totalSamples[i] == 0)
				totalSamples[i] = 1;
			System.out.print("Class " + i + ":" + totalSamples[i] + " ");
		}
		System.out.println();
		//
		// sampling from dataset
		//
		return sampleDataset(totalSamples, data, arffFileName);

	}

	/***
	 * Creates the data set based on a given distribution of classes in int[]
	 * array
	 *
	 * @param totalSamples
	 *            Distribution array
	 * @param data
	 *            Weka Instances (data set)
	 * @param arffFileName
	 * @return
	 * @throws Exception
	 */

	private String sampleDataset(int[] totalSamples, Instances data,
			String arffFileName) throws Exception {
		//
		// sampling from dataset
		//
		Instances finalData = new Instances(data, 0, 0);

		weka.core.Attribute at = new Attribute("uniqNo");
		finalData.deleteAttributeAt(0);
		finalData.insertAttributeAt(at, 0);

		Integer startIdx = 0;
		Integer instanceUniqNo = 0;
		for (int j = 0; j < totalSamples.length; j++) {
			int classJthIns = totalSamples[j];

			String previousClass = "";
			if (startIdx < data.numInstances())
				previousClass = data.instance(startIdx).stringValue(
						data.classIndex());
			//System.out.println("previous class:  " + previousClass);
			int idx;
			String classVal = "";
			for (idx = startIdx; idx < data.numInstances(); idx++) {

				classVal = data.instance(idx).stringValue(data.classIndex());

				// if random instances are more than the instances, copy them
				// again
				if (!classVal.equalsIgnoreCase(previousClass)
						&& (classJthIns > 0)) {
					idx = startIdx;
				} else if (!classVal.equalsIgnoreCase(previousClass)
						&& (classJthIns <= 0)) {

					break;
				} else if (classJthIns > 0 && idx == (data.numInstances() - 1)) {
					idx = startIdx;
				}

				if (classJthIns > 0) {
					instanceUniqNo++;

					data.instance(idx).setValue(0, instanceUniqNo);// assign a
																	// unique
																	// index
																	// number
					finalData.add(data.instance(idx));
					classJthIns--;
				}

			}
			//System.out.println(classJthIns + " --next class: " + classVal);
			startIdx = idx;

		}// end of sampling

		data.delete();
		data = null;

		String fileName = arffFileName;
		ConverterUtils.DataSink.write(fileName, finalData);

		return fileName;

	}

}
