#תקלות חו"ג ובקרת איכות 1 

# install.packages("simmer", repos = "http://cran.us.r-project.org")
# install.packages("parallel", repos = "http://cran.us.r-project.org")
# install.packages("e1071", repos = "http://cran.us.r-project.org")
# install.packages("sqldf", repos = "http://cran.us.r-project.org")
# library(e1071)
# library(sqldf)
# library(simmer)
# library(parallel)

##-------------------------------------------------1.  functions--------------------------------------------------------------------------------------------------------------------------------------

addService <- function  (path,sname,timeDist){
   updatedPath <- seize(path, sname)%>%
      timeout(timeDist) %>%
      release(sname)
   return(updatedPath)
}


target_magashit  <-  function () { #define the target of the magashit - inventory or argaz (יעוד המגשית)
   I = get_attribute(mamaTov_factory, key = "Inventory" ,global=TRUE)
   if (I <= 100) { return (i_path = 1) # go to inventory path
   } else {return (i_path = 0) } # continue to be packed
}


check_kziza <- function(quality_kziza) { #quality check for kziza (בדיקת איכות של הקציצה)
   if (quality_kziza == 0) { #reallity - good
      quality_kziza_result = rdiscrete(1,c(0.9,0.1), c(0,1)) #identify mistake if good עובד איי מזהה קציצה תקינה בטעות כפגומה בהסתברות 0.1
   }else { #reallity - defect
      quality_kziza_result = rdiscrete(1,c(0.8,0.2), c(1,0)) #identify mistake if damaged עובד איי מזהה קציצה פגומה בטעות כתקינה בהסתברות 0.2
   }
   return (quality_kziza_result)
}


check_activation <-  function() { #check if there is a break or an error in the simmer in order to synchronize activation. (עשיית אקטיבייט בהתאם למצב התקלה וההפסקה)
   breaks =get_attribute(mamaTov_factory,"in break", global = TRUE)
   error = get_attribute(mamaTov_factory,"error station", global = TRUE)
   if (breaks == 1 || error == 1) {
     return (activated = 0)
   } else {return (activated = 1)}
}


choose_magash_to_check  <- function() { #a random function that determin the magash who went checking.(בוחר רנדומלית מגשית אחת מתוך 10 לבדיקת איכות)
   counter =  get_attribute(mamaTov_factory, key = "count_magashit" ,global=TRUE)
   if (counter == 1) {
      return (1)
   }else {
      return (0)
   }}

##-------------------------------------------------2.  parameters--------------------------------------------------------------------------------------------------------------------------------------

simulationTime <-  16*60*60
initial_inventory <- 200 # initilized 200 kzizot
initial_error <-  0
initial_break <-  0
init_quality_sum <-  0
initial_counter <- 0
initial_approve <- 1 
initial_outcome <-  7*50*16 #global salaries
initial_income <-  0
##------------------------------------------------3.  init_simu_and_resources--------------------------------------------------------------------------------------------------------------------------

mamaTov_factory <-simmer("mamaTov_factory")   %>%
   add_resource("station1" , capacity = 1  , queue_size = Inf) %>%   
   add_resource("station2" , capacity = 1  , queue_size = Inf) %>%  
   add_resource("station3" , capacity = 1  , queue_size = Inf) %>% 
   add_resource("station4" , capacity = 1  , queue_size = Inf) %>%  
   add_resource("station5" , capacity = 1  , queue_size = Inf) %>% 
   add_resource("conveyor" , capacity = 1  , queue_size = Inf) %>% 
   add_resource("worker A" , capacity = 1  , queue_size = Inf) %>% 
   add_resource("worker B" , capacity = 1  , queue_size = Inf) %>% 
   add_resource("lock quality check",capacity = 1 , queue_size = Inf) 


##----------------------------------------------4.  mama tov trajectories------------------------------------------------------------------------------------------------------------------------------

#init
init_global<- trajectory("init")%>% #(אתחול תכונות ראשוניות למלאי ,תקלות והפסקות)
  set_attribute(key="Inventory",value=initial_inventory,global=TRUE) %>%
  set_attribute(key = "error station", value = initial_error, global = TRUE) %>% #default 0 - no error
  set_attribute(key = "in break", value = initial_break, global = TRUE) %>% #default 0 - no break %>%
  set_attribute(key = "outcome", value = initial_outcome, global = TRUE) %>%
   set_attribute(key = "income", value = initial_income, global = TRUE) %>%
  set_capacity ("station1",1) %>%
  set_capacity ("station2",1) %>%
  set_capacity ("station3",1) %>%
  set_capacity ("station4",1) %>%
  set_capacity ("station5",1) %>%
  set_capacity("lock quality check",1)  
  


add_to_stock <-  trajectory("add_to_stock") %>% #update stock (הוספת קציצות למלאי כאשר מגשית אחת מסתיימת)
   seize("worker B") %>% #הלוך
   timeout(function() 60*2) %>%
   set_attribute(key="Inventory",value=function(){ get_attribute(mamaTov_factory,key="Inventory",global=TRUE) +100 } ,global=TRUE) %>%
   timeout(function() 60*2) %>%
   release("worker B") 


remove_defect <- trajectory("remove_defects") %>% #replace defect kziza 
   addService("worker A" , function () rnorm(1,30,10) )  %>%
   set_attribute(key="Inventory",value=function(){ get_attribute(mamaTov_factory,key="Inventory",global=TRUE) -1 } ,global=TRUE) %>%
   set_attribute(key="quality", value=0, global = FALSE) %>% #0=good , 1=defect quality
   set_attribute(key="quality result", value=0, global = FALSE)  #0=good , 1=defect quality



change_defects_to_good <-trajectory("change_defects_to_good") %>% #this traj determin if a kziza is defect or not
   separate() %>%separate() %>%  #kziza
   branch(option = function() get_attribute(mamaTov_factory, "quality result") , continue= c(TRUE), remove_defect ) %>%
   batch(n= 5 , permanent = FALSE)%>% #back to tavnit
   batch(n=20,permanent = FALSE)#back to magashit



check_kziza_path <- trajectory("check_kziza_path") %>% #check kziza main traj
   timeout(function() runif (1,10,30)) %>%
   set_attribute(key = "outcome", value = function() get_attribute(mamaTov_factory,"outcome",global=TRUE)+0.75, global = TRUE) %>% #pay for test
   set_attribute(key ="quality result", value = function () check_kziza(get_attribute(mamaTov_factory,key = "quality")), global = FALSE)  %>% #result according to quality checker.
   set_attribute(key="quality sum",value=function(){ get_attribute(mamaTov_factory,key="quality sum",global=TRUE) +get_attribute(mamaTov_factory,key = "quality result", global = FALSE) } , global = TRUE) %>% #sum of all defects in this argaz
   set_attribute("argaz_approved", function() ifelse(get_attribute(mamaTov_factory, "quality sum",global = TRUE )<23, 1, 0), global = TRUE)   #define if argaz is approved(1) or not (0). important to update it every kziza in order to not check after 20 defects




not_approved_finish <-  trajectory("not_approved_finish") %>% #ארגז שנדחה מסיים את המסלול
   leave(prob = 1)


approved_finish <- trajectory("approved_finish")%>% #כשארגז שאושר מסיים את המסלול
   addService("worker A", function () runif(1,3,6)) %>%
   separate() %>% separate() %>%separate()%>% #kziza
   set_attribute("outcome", function() ifelse(get_attribute(mamaTov_factory, "quality",global = FALSE )==1 , get_attribute(mamaTov_factory, "outcome", global = TRUE)+10, get_attribute(mamaTov_factory, "outcome", global = TRUE)), global = TRUE) %>%
   batch(n=1000,permanent = FALSE) %>%
   set_attribute(key = "income", value = function() get_attribute(mamaTov_factory,"income",global=TRUE)+200*20, global = TRUE)  #selling profit - 200 tavniot

activation <- trajectory("activation") %>% #activate generators
   activate("kziza")%>%
   activate("defects AD")%>%
   activate("defects EJ")


dis_activation <- trajectory("dis_activation") %>% #dis_activation generators
   deactivate("kziza")%>%  #while on break/error - no kzizas in stations
   deactivate("defects AD")%>%  #while on break/error - no defects in stations
   deactivate("defects EJ") 


break_while_error <-  trajectory("break_while_error") %>%
  timeout(function() 20*60) %>%
  set_attribute(key = "in break", value = 0, global = TRUE) %>% #finish break flag
  branch(option = function() check_activation(),continue = c(TRUE), activation) #check simmer status - if fits - activate generators 


take_a_break <- trajectory("take_a_break") %>% #go to break
  set_attribute(key = "in break", value = 1, global = TRUE) %>% #start break flag
  branch(option = function() get_attribute(mamaTov_factory,"error station",global= TRUE), continue = c(FALSE), break_while_error ) %>%
   join(dis_activation) %>%
   timeout(function() 8) %>% #wait 8 sec until last kziza arrive
   seize ("station1") %>%
   seize("station2") %>%
   seize("station3") %>%
   seize("station4") %>%
   seize("station5") %>%
   timeout(function() 20*60)      %>%
   release("station1") %>%
   release("station2") %>%
   release("station3") %>%
   release("station4") %>%
   release("station5") %>%
   set_attribute(key = "in break", value = 0, global = TRUE) %>% #finish break flag
   branch(option = function() check_activation(),continue = c(TRUE), activation) #check simmer status - if fits - activate generators 

maintenance <- trajectory("maintenance") %>%
   branch(option = function() check_activation()+1,continue = c(FALSE,TRUE), out1 =trajectory()%>%leave(prob = 1), out2 = trajectory()%>%timeout(0)) %>%
   set_attribute(key = "error station", value = 1, global = TRUE) %>%
   join(dis_activation) %>%
   simmer::select(resources = function() paste0("station",get_attribute(mamaTov_factory,"defect",global=FALSE)))%>%  # תופס את המשאב בהתאם למכונה התקולה
   set_capacity_selected(0) %>%
   set_attribute("outcome", function() ifelse((now(mamaTov_factory)<11*60*60) & (now(mamaTov_factory) >2*60*60) , get_attribute(mamaTov_factory,"outcome",global=TRUE)+500, get_attribute(mamaTov_factory,"outcome",global=TRUE)+1000) , global = TRUE) %>%#order in work time
   timeout(function() runif(1,20*60,30*60 )) %>% #until maintanance man arrives
   timeout(function() rexp(1,1/(20*60) )) %>% #
   timeout(function() runif(1,10*60,30*60)) %>%
   set_attribute(key = "error station", value = 0, global = TRUE) %>%
   set_capacity_selected(1) %>%
   branch(option = function() check_activation(),continue = c(TRUE), activation) 
  

maintenance_AD <-  trajectory("maintenance_AD") %>% #תקלה במכונות A-D
   set_attribute(key="defect",value= function() rdiscrete(1,c(0.5,0.5), c(1,2)) ,global=FALSE) %>% #station that is defects
   join(maintenance) 
   

maintenance_EJ <-  trajectory("maintenance_EJ") %>% #תקלה במכונות E-J
   set_attribute(key="defect",value= function() rdiscrete(1,c(0.5,1/6,1/3), c(3,4,5)) ,global=FALSE) %>% #station that is defects
   join(maintenance)

check_magash_in_argaz <- trajectory("check_magash_in_argaz") %>% # בדיקת תקינות הקציצות בתוך המגשית שנבחרה מתוך הארגז
   separate() %>%separate() %>% #kziza
   seize("worker A" ) %>% #if argaz was not dissaproved yet - keep checking. seize in order to check kzizot one by one
   branch(option = function() get_attribute(mamaTov_factory, "argaz_approved", global = TRUE) , continue= c(TRUE),check_kziza_path) %>%
   release("worker A") %>%
   batch(n= 5 , permanent = FALSE) %>% #tavnit
   batch(n= 20 , permanent = FALSE) %>% #magashit 
   branch(option = function() get_attribute(mamaTov_factory, "argaz_approved", global = TRUE) , continue= c(TRUE), change_defects_to_good) #if was approved - replace all defects 



quality_check <-  trajectory("quality_check") %>%
   seize("lock quality check" ) %>%
   set_capacity("lock quality check",0) %>%
   release("lock quality check") %>%
   set_attribute(key="quality sum",init_quality_sum,global=TRUE)%>% # init quality_sum
   set_attribute("argaz_approved",initial_approve ,global=TRUE) %>% #init decision if argaz is regected or not
   set_attribute("count_magashit",initial_counter ,global=TRUE)%>%  # init counter for magashit random picking for quality check
   separate()  %>% # magashit 
   set_attribute("count_magashit",value = function(){get_attribute(mamaTov_factory,key="count_magashit",global=TRUE)+1 },global=TRUE) %>%
   branch(option=function() choose_magash_to_check()  ,continue= c(TRUE),check_magash_in_argaz)%>%
   batch(n= 10 ,permanent = FALSE )%>% #return to argaz
   set_attribute(key="argaz approved record",value= function() get_attribute(mamaTov_factory,"argaz_approved" ,global=TRUE),global = FALSE) %>% #station that is defects
   set_capacity("lock quality check",1) 
  

   
add_service_stations <-  trajectory("add_service_stations")%>%
   renege_in(600)  %>%
   addService("station1",function() rnorm(1, 6,sqrt(2))) %>% 
   addService("station2",function() rnorm(1, 7,sqrt(0.0725))) %>% 
   addService("station3",function() rnorm(1, 8,sqrt(2.735))) %>% 
   addService("station4",function() rnorm(1, 5,0.5)) %>% 
   addService("station5",function() rnorm(1, 5,sqrt(0.41))) %>%
   renege_abort() 


production_line<- trajectory("production_line")%>% #kziza traj
   set_attribute(key = "outcome", value = function() get_attribute(mamaTov_factory,"outcome",global=TRUE)+1, global = TRUE) %>% #עלות חומר גלם
   set_attribute(key="quality", value=function() rdiscrete(1,c(0.95,0.05),c(0,1)), global = FALSE)  %>% #real quality- 0=good , 1=defect
   join (add_service_stations) %>%
   batch(n=5, permanent = FALSE) %>% #Tavnit -waiting on fridge until 5 kzizot arrive
   addService("conveyor", function () 0.5*60 ) %>% 
   timeout(function() 9.5*60) %>%
   batch (n = 20 , permanent = FALSE) %>% #wait for 20 Tavnit until batch to magashit
   addService("worker B", function() rnorm (1, 2, 0.01)) %>% #prepare magashit
   branch(option = function() target_magashit() , continue= c(FALSE), add_to_stock) %>%
   batch (n = 10 , permanent = FALSE) %>%  #magshit to argaz
   join (quality_check) %>%
   branch(option = function() {get_attribute(mamaTov_factory, "argaz_approved",global = TRUE)+1 } , continue= c(FALSE,TRUE), out1=not_approved_finish  ,out2=approved_finish ) 



##----------------------------------------------5.  All Generators-------------------------------------------------------------------------------------------------------------------------------------

mamaTov_factory%>%
   add_generator("init", init_global, at(0),mon=2)%>% #only once
   add_generator("kziza", production_line, distribution = function() 8, mon=2 ) %>%
   add_generator("break", take_a_break, at(c(4*60*60,12*60*60)), mon=2) %>%
   add_generator("defects AD",maintenance_AD , function() rnorm(1, 163.7368*60 ,5.950731*60), mon=2 ) %>%
   add_generator("defects EJ", maintenance_EJ, function() rexp(1, (0.006772104/60)*(1/1.2)), mon=2 )



#-----------------------------------------------analysis_existing_situation-----------------------------------------------------------------------------------------------------------------------

 n <- 271
alternative1<-mclapply(1:n, function(i) {
   set.seed(((i+99)^2)*3-7)
   reset(mamaTov_factory)%>%run(until=simulationTime)%>%
     wrap()
 })

 arrivalsData <- get_mon_arrivals(alternative1)
 attributesData <- get_mon_attributes(alternative1)
 service_data <-  get_mon_arrivals(alternative1, per_resource = TRUE)
 resourceData <-  get_mon_resources(alternative1)


 
 #------------------------------------------------------------average damaged kzizas in argaz -only accepted------------------------------------------------------------------------------------------
 
 quality_table <- sqldf( #table of quality for each kziza in the end of the day
    "select max(time), name, value as quality, replication
    from attributesData
    where key = 'quality'
    group by name, replication "
 )
 
 kziza_in_argaz <- sqldf( #table that if kziza was in an argaz that was approved
    "select time,name, replication
    from attributesData
    where key='argaz approved record' and value = 1
    group by name, replication, value"
 )
 
 
 defects_in_argaz <-  sqldf ( #time represents an argaz(since all recieved the attrivute argaz approved record at once)
    " select time, kziza_in_argaz.replication as replication , sum(quality_table.quality) as 'total defects'
    from kziza_in_argaz left join quality_table on kziza_in_argaz.name = quality_table.name and kziza_in_argaz.replication = quality_table.replication
    group by time, kziza_in_argaz.replication
    " )
 
 daily_average_defects <-  sqldf ( #daily avarage for  replication that was created in approved argazim.
    " select replication, avg(defects_in_argaz.'total defects') as 'average defects'
    from defects_in_argaz
    group by replication
    ")
 
 
 
 defects_avg <-  mean(daily_average_defects$`average defects`)
 defects_sd <-  sd(daily_average_defects$`average defects`)
 
 
 #--------------------------------------------------workers in 5 stations utility-------------------------------------------------------------------------------
 
 workers_utility <- sqldf ( "
                            select replication, (sum(activity_time)+2*20*60*5)/ (5*16*60*60) as utility 
                            from service_data
                            where (resource like 'station%')  and name not like 'break%'
                            group by replication ")
 
 utility_avg <- mean(workers_utility$utility)
 utility_sd <- sd(workers_utility$utility)
 
 #-------------------------------------------average profit in day-------------------------------------
 incomes  <- sqldf ("
                    select max(value) as income , replication
                    from attributesData
                    where key = 'income'
                    group by replication
                    "
 )
 
 
 outcomes  <- sqldf ("
                     select max(value) as outcome , replication
                     from attributesData
                     where key = 'outcome'
                     group by replication
                     "
 )
 profit_per_day <-  sqldf(  "
                            select  outcomes.replication, income-outcome as profit
                            from incomes join outcomes on incomes.replication = outcomes.replication
                            group by outcomes.replication
                            "
 )
 
 profit_avg <- mean(profit_per_day$profit)
 profit_sd <- sd(profit_per_day$profit)
 