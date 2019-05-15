# connection to Tableau and r
install.packages("Rserve")
library(Rserve) 
Rserve()
install.packages("dbConnect")
library(dbconnect)
install.packages("RMySQL")

install.packages("dbGetQuery")
library(dbGetQuery)

install.packages("RMySQL")
library(RMySQL)

install.packages('rpart')
library(rpart)
install.packages('tree')
library(tree)
install.packages('party')
library(party)
install.packages("rpart.plot")
library("rpart.plot")
install.packages("sqldf")
library(sqldf)

#-------------------------------------------------------------------------------------------
drv <- dbDriver("MySQL")

dbforR= dbConnect(drv,user='root',password='root',dbname='bi_8_dw',host='127.0.0.1')

data <- dbSendQuery(dbforR, "SELECT * FROM bi_8_dw.dw_rolap_display
                              where year =2018 ")
rolap_display <- dbFetch(data, n=-1)

rolap_display<-subset(rolap_display, (!is.na(rolap_display[,21]))) 

rolap_display <- rolap_display[,c(15,16,22,24,26)]

rolap_display$User_Register_date <-  strtoi(substr(rolap_display$User_Register_date, 1, 4))



for (i in c(1,2)){
  rolap_display[,i] = factor(rolap_display[,i])
}



for (i in (1:nrow(rolap_display))){
  if (rolap_display[i,5]=='Fashion\r'||rolap_display[i,5]=='Shoes\r'||rolap_display[i,5]=='Makeup & Beauty\r'|| rolap_display[i,5]=='Tops\r'|| rolap_display[i,5]=='Bottoms\r')
  {rolap_display[i,5]='Fashion'}}

for (i in (1:nrow(rolap_display))){
  if (rolap_display[i,5]=='Accessories\r'||rolap_display[i,5]=='Watches\r'||rolap_display[i,5]=='Jewerly\r'||rolap_display[i,5]=='Wallets & Bags\r')
  {rolap_display[i,5]='Accessories'}}

for (i in (1:nrow(rolap_display))){
  if (rolap_display[i,5]=='Home decor\r'||rolap_display[i,5]=='Baby & Kids\r'||rolap_display[i,5]=='Phone upgrade\r'||rolap_display[i,5]=='Gadgets\r'||rolap_display[i,5]=='Hobbies\r'||rolap_display[i,5]=='Pets\r')  
  {rolap_display[i,5]='Leisure'}}


rolap_display[,5]=factor(rolap_display[,5])

?rpart
tree   <- rpart(category ~ ., data = rolap_display,cp=0.01115) # create the tree


rpart.plot(tree, box.palette="RdBu", shadow.col="gray", nn=TRUE)


#------------------------------------------------------------------------------#
drv <- dbDriver("MySQL")

dbforR= dbConnect(drv,user='root',password='root',dbname='bi_8_dw',host='127.0.0.1')
data2 <- dbSendQuery(dbforR, "SELECT *
                            FROM bi_8_dw.dw_rolap_company_2018")

data2 <- dbFetch(data2, n=-1)


data2[, 5][is.na(data2[, 5])] <- 0
data2[, 1][is.na(data2[, 1])] <- mean(data2[, 1],na.rm = T)


data3 <- dbSendQuery(dbforR, "SELECT * 
                            FROM bi_8_dw.dw_rolap_company_2017")
data3 <- dbFetch(data3, n=-1)



data3[, 5][is.na(data3[, 5])] <- 0
data3[, 1][is.na(data3[, 1])] <- mean(data3[, 1],na.rm = T)


regression_data = cbind(data2,data3[,6])
regression_data = regression_data[,-2]
names(regression_data)[6]<-"y"

model  = lm(y~.,data = regression_data)
data3 = data3[,-2]

predicition_2019 = data.frame(predict(model,data3))

predicition_2019 = cbind(predicition_2019,data2[,2])

names(predicition_2019)[2]<-"company_name"

write.csv(predicition_2019,file = "S:/bi/predicition_2019.csv")




