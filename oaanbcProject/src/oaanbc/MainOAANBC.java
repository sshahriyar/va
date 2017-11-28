package oaanbc;

import java.io.File;
/**
 * Main entry point of the application
 * @author <p> Syed Shariyar Murtaza </p>
 *
 */
public class MainOAANBC {

	public static void main (String []args) throws Exception{

	        //BufferedWriter outputBuildTime=null;
	      //  long startTime=0;
	      //  OneAgainstAll m= new OneAgainstAll();
	        	//String inputFolder="/home/shary/sharywork/ryerson/newer_experiment_data/arff/with_id/output_R";
	       //---  String inputFolder="/home/shary/sharywork/ryerson/experiment_data/finaldata/comparison_dirich_mds/output_naivebayes";
	        // String inputFolder="/home/shary/sharywork/ryerson/experiment_data/comparison_agincourt/output_naivebayes";
	      //---   String finalOutputFile="result_"+inputFolder.substring(inputFolder.lastIndexOf(File.separator)+1,inputFolder.length())+".txt";

	        	//args=new String[]{"/home/shary/sharywork/ryerson/experiment_data/finaldata/Agincourt_nom.arff", "10", "10", "16", "t"};
	        //   String outFolder="comparison_dirich_agincourt";
	           //String outFolder="comparison_agincourt";
	       // args=new String[]{"/home/shary/sharywork/ryerson/experiment_data/finaldata/Matlab_nom.arff", "10", "10", "15", "f"};
	      // String outFolder="comparison_dirich_matlab";
	       //String outFolder="comparison_matlab";
	          ;
	      //  String outFolder="comparison_dirich_mds";
	       // String outFolder="comparison_mds";
	        //args=new String[]{"/home/shary/sharywork/ryerson/newer_experiment_data/arff/with_id/PHMRC_IHME_India_Adult_12-69yrs_withid.arff"
	        	//	, "10", "10", "13", "f"};
	        //String outFolder="comparison_India_Adult_12-69yrs";
	//
	       // args=new String[]{"/home/shary/sharywork/ryerson/newer_experiment_data/arff/with_id/PHMRC_IHME_India_Child_28days-11yrs_withid.arff"
	        //		, "10", "10", "9", "f"};
	        //String outFolder="comparison_India_Child_28days-11yrs_withid";

	       // args=new String[]{"/home/shary/sharywork/ryerson/newer_experiment_data/arff/with_id/PHMRC_IHME_India_Neo_u28days_withid.arff"
	        //		, "10", "10", "2", "f"};
	       // String outFolder="comparison_India_Neo_u28days_withid";

	        //  args=new String[]{"f","/home/shary/sharywork/ryerson/experiment_data/finaldata/MDS_nom.arff","10", "15","dataset_dirich_mds", "t"};
	      //  args=new String[]{"m","/home/shary/sharywork/ryerson/experiment_data/finaldata/dataset_dirich_mds/output_naivebayes","10","15"};
	        //  args=new String[]{"o" ,"/home/shary/sharywork/ryerson/experiment_data/finaldata/dataset_dirich_mds" ,"10" ,"15", "uniqNo"};
	        String inputPath="";
	        Integer totalFolds=0;
	        Integer totalClassesInput=0;
	        Boolean isDirichletSampling=false;
	        String outputFolderName="";
	        String attributeNameToRemove="";
	        String inputPrefix="dataset_";
	        if (args[0].equals("m")){
	        	if (args.length != 4){
	        		  System.out.println(" For computation of measures: java -jar oaanbc.jar \"m\" \"path to input folder\" \"repetitions\" "
	  	            		+ "\"total classes excluding others\" ");
	        		  System.exit(-1);
	        	}
	        }
	        else if (args[0].equals("f")){
	        	if (args.length != 6){
	        		  System.out.println(" For generation of n fold files: java -jar oaanbc.jar \"f\" \"path to input folder\" \"folds\" "
	  	            		+ "\"total classes excluding others\" \"output folder name\" \"t for dirichlet sampling in test set and f for original classes ratio in the test set\"");
	        	       System.exit(-1);
	        	}
	        } else if (args[0].equals("o")){
	        	if (args.length != 5){
	        		  System.out.println(" For execution of OAA-NBC: java -jar oaanbc.jar \"o\" \"path to input folder\" \"folds\" "
	  	            		+ "\"total classes excluding others\" \"attribute name that contains IDs\" ");
	        	       System.exit(-1);
	        	}
	        }



	        try {
	        inputPath=args[1];
	        totalFolds=Integer.parseInt(args[2]);

	        totalClassesInput=Integer.parseInt(args[3]);
           if (args[0].equals("f")){
	          outputFolderName=args[4];
	          if (args[5].equalsIgnoreCase("t"))
	        	  isDirichletSampling=true;
           }
           if (args[0].equals("o")){
        	  attributeNameToRemove=args[4];
           }

	        }catch (NumberFormatException ex){
	            System.out.println ("Please only type integer numbers for repetitions and stratas");
	        }


	        if (args[0].equals("m")){
	        	   String finalOutputFile="result_"+inputPath.substring(inputPath.lastIndexOf(File.separator)+1,inputPath.length())+".txt";

	              ComputeMeasures.measurements(finalOutputFile, totalClassesInput, totalFolds, inputPath);
	        }
	        else if (args[0].equals("f")){
	        	  GenerateNFolds.generateFiles(inputPath, totalClassesInput, totalFolds, outputFolderName,isDirichletSampling,inputPrefix);
	        }else if (args[0].equals("o")){
	        	OAANBC oaaNbc=new OAANBC();
	        	oaaNbc.executeOAANBC(inputPath, totalFolds, totalClassesInput, attributeNameToRemove, inputPrefix);

	        }

	}
}
