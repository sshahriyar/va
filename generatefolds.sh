###
### Use this file as an example to generate training and test files ##  for n folds
### and to execute OAA-NBC on the generated data. 
###
 echo "*** Generating files for 10 folds"
 ## parameters:
# 1. "f" for files
# 2. "dataset/mds_nom.arff" input file
# 3. "10" Number of folds
# 4. "15" total classes in the dataset (Excluding others which is used by OAA-NBC only)
# 5. "f" for false and "t" for true foe dirichlet distribution based test set



java -jar  lib/oaanbc.jar  "f" "dataset/regular/matlab_nom.arff" "10"  "15" "mypath/dataset_matlab"  "f"



echo "****************Generate files uisng 10 folds but generate the test set using Dirichlet distribution ***************"


java -jar  lib/oaanbc.jar  "f" "dataset/regular/matlab_nom.arff" "10"  "15" "mypath/dataset_dirich_matlab"  "t"

