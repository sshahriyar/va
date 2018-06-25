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

for algo in "naivebayes" "tariff" "interva" "insilico"
do
echo $algo
java -jar  lib/oaanbc.jar  "m" "dataset/dirichlet/dataset_dirich_matlab_10_folds/output_$algo" "10"  "15" 



done


for algo in "naivebayes" "tariff" "interva" "insilico"
do
echo $algo
java -jar  lib/oaanbc.jar  "m" "dataset/regular/dataset_matlab_10_folds/output_$algo" "10"  "15" 



done
