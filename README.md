# va
Verbal Autopsy Experiments for OAA-NBC

To execute the experiments follow these steps (note all the experiments have been successfully tested on a Linux machine):

1. Generate n-folds Cross Validation files (training and test set pair for each fold) for the given dataset. The dataset should be in ARFF format. This step can be skipped too for the datasets already avaialble in the repository for 10 folds Cross Validation.
   File To Execute: generatefolds.sh
2. Execute R code on the generated data from step 1 (i.e., on 10 different folders, see R code for the details.)
   File to Execute: openva_execute.r (use R to execute it)
3. Perform measurements for sensitivity, specificity, PCCC and CSMF accuracy from the data generate by R.
   File to Execute: measures.sh
4. Build OAA-NBC models on the data of Step 1 and get all the measures of sensitivity, specificity, PCCC and CSMF accuracy.
   File to Execute: ooanbc.sh
   
  
Directories in the repository
oaanbcProject: All the source code of OAA-NBC in Java along with the code for generation of data for n-folds and the code for measurements of output or R code.
R-code: R code using OpenVA package and weka (input) files to execute OAA-NBC,  InterVA-4, Tariff, InSilicoVA and NBC 
lib: this folder contains the compiled jar file of the OAA-NBC source code. This file is used in above scripts.

   
   
