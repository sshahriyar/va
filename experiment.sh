###
### Use this file as an example to generate training and test files ##  for n folds
### and to execute OAA-NBC on the generated data. 
###
 echo "*** Generating files for n folds"

#java -jar  oaanbc.jar  "f" "/home/shary/sharywork/ryerson/experiment_data/finaldata/matlab_nom.arff" "10"  "15" "dataset_dirich_matlab"  "t"

echo "**************** data generated ***************"

echo "******** Building oaanbc models*********" 
#java -jar oaanbc.jar "o" "/home/shary/sharywork/ryerson/experiment_data/finaldata/dataset_dirich_matlab/" "10" "15" "uniqNo"

echo "********************OAA NBC Finished Execution **********"

 


echo "*** enerating one against all files"
mainDir="/home/shary/sharywork/ryerson/experiment_data/finaldata"

mainOutputFolder="$mainDir/dataset_dirich_agincourt"
mkdir $mainOutputFolder
count=1
for var in {1..10}
do
folderName="dataset_dirich_agincourt_$var"

java -jar  oaanbc.jar  "f" "$mainDir/agincourt_nom.arff" "10"  "16" "$folderName"  "t"


	for i in {1..10}
	do
  
   		mv "$mainDir/$folderName/dataset_$i" "$mainOutputFolder/dataset_$count"
   		let count++
	done
rm -rf $folderName
done
echo "**************** data generated ***************"

echo "******** Building oaanbc models*********" 
java -jar oaanbc.jar "o" "/home/shary/sharywork/ryerson/experiment_data/finaldata/dataset_dirich_agincourt2/" "100" "16" "uniqNo"

echo "********************OAA NBC Finished Execution **********"

