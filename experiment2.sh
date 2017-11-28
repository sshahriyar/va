###
### Use this file as an example to generate training and test files ##  for n folds
### and to execute OAA-NBC on the generated data. 
###
 echo "*** Generating files for n folds"

java -jar  oaanbc.jar  "f" "/home/shary/sharywork/ryerson/experiment_data/finaldata/mds_nom.arff" "10"  "15" "dataset_dirich_mds"  "t"
echo "**************** data generated ***************"

echo "******** Building oaanbc models*********" 
java -jar oaanbc.jar "o" "/home/shary/sharywork/ryerson/experiment_data/finaldata/dataset_dirich_mds/" "10" "15" "uniqNo"

echo "********************OAA NBC Finished Execution **********"

 
 echo "*** Generating files for n folds"

#java -jar  oaanbc.jar  "f" "/home/shary/sharywork/ryerson/experiment_data/finaldata/agincourt_nom.arff" "10"  "16" "dataset_dirich_agincourt"  "t"
echo "**************** data generated ***************"

echo "******** Building oaanbc models*********" 
#java -jar oaanbc.jar "o" "/home/shary/sharywork/ryerson/experiment_data/finaldata/dataset_dirich_agincourt/" "10" "16" "uniqNo"

echo "********************OAA NBC Finished Execution **********"

