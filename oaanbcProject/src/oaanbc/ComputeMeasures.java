

package oaanbc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;


/**
 * This class computes the measures, such as sensitivity, specificity, PCCC and CSMF accuracy, from a given file. This class is used on
 *  the output of the R implementation of the existing algorithms of VA analysis. It can be used on any other output too as long as the
 *  output is in the following  format:
 *  Rank, prediction, correct class, Correct prediction or not
 *  1,"7","6",FALSE
 *  2,"5","6",FALSE
 *	3,"11","6",FALSE
 *	4,"12","6",FALSE
 *	5,"8","6",FALSE
 *	6,"3","6",FALSE
  * 7,"6","6",TRUE
  *
  * In the above example, first column shows the rank of prediction by an algorithm. This algorithm made the correct prediction on the seventh
  * rank, when its predictions were sorted by some criteria (e.g., probability). First six were wrong predictions.
 *
 * @author <p> Syed Shariyar Murtaza </p>
 */
public class ComputeMeasures {

    private static ConfusionTable computeFromRGeneratedData(String file) throws IOException, Exception{

    	BufferedReader reader=new BufferedReader(new FileReader(file));
    	String line="";
    	Integer rowsInTestSet=0;
    	HashSet<String> uniqueClasses=new HashSet<>();
    	reader.readLine();//skip header
    	while ((line=reader.readLine())!=null){
    		 String[] columns=line.replaceAll("\"", "").split(",");
    		 //System.out.println(columns[2]);
    		 if (columns[2]!=null && !columns[2].isEmpty()){
    			 uniqueClasses.add(columns[2]); //add both pred and correct for any missing values
    			 uniqueClasses.add(columns[1]);
    		 }
    		 if (columns[0]!=null && Integer.parseInt(columns[0])==1)
    			 rowsInTestSet++;
    	}
    	reader.close();

    	String []classesinTestSet=new String[uniqueClasses.size()];
    	classesinTestSet=uniqueClasses.toArray(classesinTestSet);

        Integer totalClassesInTest=uniqueClasses.size();

        ConfusionTable ct= new ConfusionTable((totalClassesInTest),
       		     classesinTestSet, rowsInTestSet);


        reader=new BufferedReader(new FileReader(file));
    	line="";
    	reader.readLine();//skip header
    	while ((line=reader.readLine())!=null){
    		 String[] columns=line.replaceAll("\"", "").split(",");
    		 if (columns[3]!=null && columns[3].equalsIgnoreCase("TRUE")){
    			 ct.updateCorrectPrediction( Integer.parseInt(columns[0]), columns[2]);//rank and correct cass
    		 }else if (columns[2]!=null && !columns[2].isEmpty()){

    			 ct.updateWrongPrediction(Integer.parseInt(columns[0]), columns[2], columns[1]);
    		 }
    	}
    	reader.close();
    	return (ct);
    }


        /**
         *
         * @param inputFile
         * @param totalClassesInput
         * @param maxRepetitions
         * @param inputFolder
         */
   public static void measurements(String finalOutputFile, Integer totalClassesInput,
		     Integer maxRepetitions,String inputFolder){

       try{

       String progDir=inputFolder.substring(0,inputFolder.lastIndexOf(File.separator));

       finalOutputFile=progDir+File.separator+finalOutputFile;


       Double []sensitivity=new Double[totalClassesInput];//totalclassess is equal to total ranks
       Double []specificity=new Double[totalClassesInput];
       Double []PCCC=new Double[totalClassesInput];
       Double []csmfAccuracy=new Double[totalClassesInput];
       Map<String,Double> sortedClassesForRank1=new TreeMap<>();

       Arrays.fill(sensitivity,0.0);
       Arrays.fill(specificity,0.0);
       Arrays.fill(PCCC,0.0);
       Arrays.fill(csmfAccuracy,0.0);


       for (int repCount=0; repCount<maxRepetitions; repCount++){
                  int cnt=repCount+1;


                                 //get total classes from test, class names from test and then number of records
    	                      String file=inputFolder+"/"+cnt+".txt";
                              ConfusionTable ct=computeFromRGeneratedData(file);
                              ct.calculateMeasures();
                              //ct.print();
                              java.util.Map<String,Double> sortedClassByPerc=ct.calculateMeasuresPerClass();
                              //measures per Rank in each array (i.e., rank = total Classes)
                              Double []lSensitivity=ct.getSensitivity();
                              Double []lSpecificity=ct.getSpecificity();
                              Double []lPCCC=ct.getPCCC();
                              Double []lCsmfAccuracy=ct.getCsmfAccuracy();

                              for (int j=0; j<lSensitivity.length; j++){// saving sensitivities sum for multiple repetitions
                                  sensitivity[j]+=lSensitivity[j];
                                  specificity[j]+=lSpecificity[j];
                                  PCCC[j]+=lPCCC[j];
                                  csmfAccuracy[j]+=lCsmfAccuracy[j];

                              }

                              //code for classes and their percentages in rank 1
                              for (Map.Entry<String,Double> it:sortedClassByPerc.entrySet()){
                                  String key=it.getKey();
                                  if (!sortedClassesForRank1.containsKey(key))
                                        //taking care of new classes
                                        // could be found in different iterations
                                      sortedClassesForRank1.put(key,it.getValue());
                                  else{
                                     Double val=sortedClassesForRank1.get(key);
                                     val+=it.getValue();
                                     sortedClassesForRank1.put(key,val);

                                    }
                              }

       }// end of repetitions

       BufferedWriter outFile=new BufferedWriter(new FileWriter(finalOutputFile));
       String text="";
       text="According to Cross Validation On Original Distribution of Classes";

        System.out.println("*****************************************************************");
        System.out.println("*****************************Final Output************************");
        System.out.println(text);
        System.out.println("*****************************************************************");
        System.out.println("Total repetitions "+ maxRepetitions);

        outFile.write("*****************************************************************");
        outFile.newLine();
        outFile.write("*****************************Final Output************************");
        outFile.newLine();
        outFile.write(text);
        outFile.newLine();
        outFile.write("*****************************************************************");
        outFile.newLine();
        outFile.write("Total repetitions "+ maxRepetitions);
        outFile.newLine();

        for (int rank=0; rank<totalClassesInput;rank++){
            sensitivity[rank]=sensitivity[rank]/maxRepetitions;
            specificity[rank]=specificity[rank]/maxRepetitions;
            PCCC[rank]=PCCC[rank]/maxRepetitions;
            csmfAccuracy[rank]=csmfAccuracy[rank]/maxRepetitions;

            System.out.println("Rank "+ (rank+1) +": sensitivity= "+sensitivity[rank]
                      + ", specificity= "+specificity[rank]+ ", PCCC= "+PCCC[rank]+ ", csmf accuracy= "+ csmfAccuracy[rank] );

            outFile.write("Rank "+ (rank+1) +": sensitivity= "+sensitivity[rank]
                      + ", specificity= "+specificity[rank]+ ", PCCC= "+PCCC[rank]+ ", csmf accuracy= "+ csmfAccuracy[rank] );
            outFile.newLine();
       }

         //Code for classes and their percentages in rank 1
        System.out.println();
        System.out.println();
        outFile.newLine();
        outFile.newLine();

        System.out.println("Top classes for Rank 1 sorted by their sensitivities are:");
        outFile.write("Top classes in Rank 1 sorted by their sensitivities are:");
        outFile.newLine();

        java.util.SortedSet<Map.Entry<String, Double>> sortedSetByVal= new
                                     java.util.TreeSet<>(new ValueComparator());

         for (Map.Entry<String,Double> it:sortedClassesForRank1.entrySet()){
                    String key=it.getKey();
                    Double val= it.getValue();
                    val=(val/maxRepetitions)*100;
                    sortedClassesForRank1.put(key,val);

            }
         sortedSetByVal.addAll(sortedClassesForRank1.entrySet());
         System.out.println(sortedSetByVal);
         outFile.write(sortedSetByVal.toString());
         outFile.newLine();

         outFile.flush();
         outFile.close();


        } catch (Exception ex) {
           // Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();

        }
        finally {


        }
    }




}
