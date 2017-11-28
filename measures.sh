###
###  This script contains example on how to calculate
### sensitivity, specificity, PCCC and CSMF accuracy from
### the output of the algorithms generated using R
###
for algo in "naivebayes" "tariff" "interva" "insilico"
do
echo $algo
java -jar  oaanbc.jar  "m" "/home/shary/sharywork/ryerson/experiment_data/finaldata/dataset_dirich_matlab/output_$algo" "10"  "15" 

#java -jar  oaanbc.jar  "m" "/home/shary/sharywork/ryerson/experiment_data/finaldata/dataset_dirich_agincourt/output_$algo" "100"  "16" 

done
