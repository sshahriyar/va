########################################
##
## Author: Syed Shariyar Murtaza
## Description: Generates the predictions of VA algorithms on different folds of ARFF data sets. An output prediction
## file is generated separately for the data sets of each fold.
##
#########################################
### Parameters to change

inputPath<-"/home/shary/sharywork/ryerson/experiment_data/finaldata/dataset_dirich_agincourt/"
totalFolds<-10

### Main Code
source ('/home/shary/sharywork/ryerson/experiments/openva.r')

modelList<-c("naivebayes","tariff","interva","insilico")
inputPrefix<-"dataset_"
inputFolder<-paste(inputPath,inputPrefix,sep="")

for (modelIdx in 1:4){
            theModel<-modelList[modelIdx]
            
            outputFolder<-paste(inputPath,"output_",theModel,"/",sep="")
            dir.create(outputFolder)
            
            
            for (cntInput in 1:totalFolds){
              
                trainFile<-paste(inputFolder,cntInput,"/trainset.arff",sep="")
                testFile<-paste(inputFolder,cntInput,"/testset.arff",sep="")
                outFile<-paste(outputFolder,cntInput,".txt",sep="")
                    
                train<-loadFile(trainFile)
                test<-loadFile(testFile)
                train<-formatData(train)
                test<-formatData(test)
                train<-moveCausePosition(train)
                test<-moveCausePosition(test)
             
                rownames(test)<-test[,1]
                rankings<-data.frame()
                #head(rownames(test))
                if (theModel==modelList[1]) #naive bayes
                  rankings<-fitNaiveBayes(train,test)
                else if (theModel==modelList[2])  #tariff 
                  rankings<-fitTariff(train,test)
                else if (theModel==modelList[3])   #interva
                  rankings<-fitInterVA(train,test)
                else if (theModel==modelList[4]) #insilico   
                  rankings<-fitInSilicoVA(train,test)
               # 
               # 
               rowIds<-c()
               colIds<-c()
               if (theModel==modelList[1]){ #naive bayes
                 rowIds<-rankings[,1]
                 rownames(rankings)<-rowIds
                 colIds<-colnames(rankings)
                 colIds<-colIds[2:length(colIds)]
               }else{
                rowIds<-rownames(rankings)
                colIds<-colnames(rankings)
               }
                correctCnt=0
                count=1
                dfSize=length(rowIds)*length(colIds)+1
                output<-data.frame(rank=numeric(dfSize),pred=character(dfSize),
                                   correct=character(dfSize),iscorrect=logical(dfSize),  
                                   stringsAsFactors=FALSE)
                
                for (rowId in rowIds){
                  #print(rankings[rowId,])
                  sortCols<-c()
                  if (theModel==modelList[1]){ #naive bayes
                    cols<-rankings[rowId,2:length(colIds)]
                    sortCols<-unlist(cols,use.names=FALSE)
                    
                  }else if(theModel==modelList[2]){#tariff
                    sorted<-sort(rankings[rowId,],decreasing=FALSE)
                    sortCols<-names(sorted)
                  } else { # for interva and inslicio 
                    sorted<-sort(rankings[rowId,],decreasing=TRUE)
                    sortCols<-names(sorted)
                  }
                  #cat(sprintf("\"%s\"  \"%s\" \"%s\"\n", rowId, sortCols[1],  test[rowId,"Cause"]))
                  #print(count)
                  for (j in 1:length(sortCols)) {
                      #print(sortCols[j])
                      #print (test[rowId,"Cause"])
                    if (sortCols[j]==test[rowId,"Cause"]){
                      output$rank[count]<-j
                      output$pred[count]<-sortCols[j]
                      output$correct[count]<-test[rowId,"Cause"]
                      output$iscorrect[count]<-TRUE
                      count=count+1
                      break
                     #   correctCnt=correctCnt+1
                    }else{
                      output$rank[count]<-j
                      output$pred[count]<-sortCols[j]
                      output$correct[count]<-test[rowId,"Cause"]
                      output$iscorrect[count]<-FALSE
                      count=count+1
                    }
                  }
                  
                  #break
                }
                write.table(output,file=outFile,sep=",",row.names=FALSE)
                
            
            }
}
