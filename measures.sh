###
###  This script contains example on how to calculate
### sensitivity, specificity, PCCC and CSMF accuracy from
### the output of the algorithms generated using R
###

 ## parameters:
# 1. "m" for measurements
# 2. "dataset/outputfolde/generated/by/R/script" input file
# 3. "10" Number of folds
# 4. "15" total classes in the dataset (Excluding others which is used by OAA-NBC only)


#example 1
algo="interva"
numClasses="15"
java -jar  lib/oaanbc.jar  "m" "dataset/dirichlet/dataset_dirich_matlab_10_folds/output_$algo" "10"  $numClasses 


# Example 2


algo="tariff"
numClasses="15"
java -jar  lib/oaanbc.jar  "m" "dataset/regular/dataset_matlab_10_folds/output_$algo" "10"  $numClasses 



