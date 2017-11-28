##################################################
##  Author: Syed Shariyar Murtaza
##  Description: This R script laods OpenVA Package and executed its algorithm on the given train and test ARFF files.
## Each aglorithm generates predictions for the ARFF file.
##################################################

#install.packages('openVA')
#Sys.setenv(JAVA_HOME='/usr/lib/jvm/jdk1.7.0_40/')
#library(rJava)
library(openVA)
library(foreign)

loadFile<-function(file){
dataset=read.arff(file)
dataset[]<-lapply(dataset,as.character)
return (dataset)
}

formatData<-function(data){
  
   data[data=='1']<-'Y'
    data$Cause[data$Cause=='Y']<-'1'
    data[data=='0']<-''
    return (data)  
}


moveCausePosition<-function(df){
  col_idx <- grep("Cause", names(df))
  df <- df[, c(1,col_idx, (2:(ncol(df)-1)))]
}


fitInSilicoVA<-function(train,test){

  
fit <- codeVA(data = test, data.type = "customize", model = "InSilicoVA",
               data.train = train, causes.train = 'Cause',
               Nsim=1000, auto.length = FALSE)
prob<-getIndivProb(fit)
return (prob)
}


fitInterVA<-function(train,test){

  fit <- codeVA(data = test, data.type = "customize", model = "InterVA",
                 data.train = train, causes.train = 'Cause',
                 version = "4.03") # HIV = "h", Malaria = "l")
  prob<-getIndivProb(fit)
  return (prob)
}


fitTariff<-function(train,test){

  fit <- codeVA(data = test, data.type = "customize", model = "Tariff",
                 data.train = train, causes.train = 'Cause',
                 nboot.sig = 100)
  

  prob<-getIndivProb(fit)
  return (prob)
}

fitNaiveBayes<-function(train,test){

  fit <- codeVA(data = test, data.type = "customize", model = "NBC",
                 data.train = train, causes.train = 'Cause',causes.test='Cause', known.nbc = TRUE)
  

  return (fit$pred)
}
