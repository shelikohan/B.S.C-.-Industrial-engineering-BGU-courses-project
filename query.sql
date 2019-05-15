GRANT ALL PRIVILEGES ON * . * TO 'root'@'localhost';
DROP DATABASE IF EXISTS BI_8_OLTP;
DROP DATABASE IF EXISTS BI_8_MIR;
DROP DATABASE IF EXISTS BI_8_STG;
DROP DATABASE IF EXISTS BI_8_DW;
CREATE DATABASE BI_8_OLTP;
CREATE DATABASE BI_8_MIR;
CREATE DATABASE BI_8_STG;
CREATE DATABASE BI_8_DW;
set foreign_key_checks=0;



DROP TABLE IF EXISTS BI_8_OLTP.Users;
CREATE TABLE BI_8_OLTP.Users (
 User_id            integer not null Primary Key ,
 User_Email			Varchar(50)       NOT NULL  ,
 First_Name			Varchar(20)       NOT NULL,
 Last_Name 			Varchar(20)       NOT NULL,
 Address_Country	Varchar(30)       NOT NULL,
 Address_City		Varchar(30)       NOT NULL,
 Address_Street		Varchar(30)       NOT NULL,
 Address_number		Varchar(40)       NOT NULL,
 Phone				Varchar(30)				,
 User_Password		Varchar(30)			NOT NULL,
 Gender				Varchar(1)			NOT NULL,
 Register_date		Date				NOT NULL,
 birthday			Date				NOT NULL,
 check (User_Email LIKE '%_@_%_.__%'),
 check (length(User_Password)>=6),
 check (Gender in ('M' ,'F'))
);
LOAD DATA LOCAL INFILE 'S:\\bi\\csvs\\Users.csv' INTO TABLE BI_8_OLTP.Users FIELDS TERMINATED BY ',' IGNORE 1 LINES;


DROP TABLE IF EXISTS BI_8_OLTP.Categories;
CREATE TABLE BI_8_OLTP.Categories(
category_id integer not null primary key,
category Varchar(30) NOT NULL

);
LOAD DATA LOCAL INFILE 'S:\\bi\\csvs\\Categories.csv' INTO TABLE BI_8_OLTP.Categories FIELDS TERMINATED BY ',' IGNORE 1 LINES;

DROP TABLE IF EXISTS BI_8_OLTP.Searches;
create table BI_8_OLTP.Searches (
 User_id            integer not null  ,
 search_date		date		NOT NULL,	
 search_time		time		NOT NULL,
 category_id		integer NOT NULL,
 Constraint PK_Searches Primary Key (User_id,search_date,search_time),
 Foreign Key (User_id) References BI_8_OLTP.Users (User_id),
 Foreign Key (category_id) References BI_8_OLTP.Categories (category_id)
)
ENGINE=INNODB; 
LOAD DATA LOCAL INFILE 'S:\\bi\\csvs\\Searches.csv' INTO TABLE BI_8_OLTP.Searches FIELDS TERMINATED BY ',' IGNORE 1 LINES;


DROP TABLE IF EXISTS BI_8_OLTP.Companies;
create table BI_8_OLTP.Companies (
Company_id integer NOT NULL primary key,
Company_Email		Varchar(50)	NOT NULL,
Company_Name				Varchar(50)	NOT NULL,
Address_Country	Varchar(30)	NOT NULL,
Address_City	Varchar(30)	NOT NULL,
Register_date		date		NOT NULL,
check (Company_Email LIKE '%_@_%_.__%')
);
LOAD DATA LOCAL INFILE 'S:\\bi\\csvs\\Companies.csv' INTO TABLE BI_8_OLTP.Companies FIELDS TERMINATED BY ',' IGNORE 1 LINES;

DROP TABLE IF EXISTS BI_8_OLTP.Advertisements;
create table BI_8_OLTP.Advertisements(
Ad_ID	Integer NOT NULL primary key,
content	Varchar(100) NOT NULL,
Company_id integer NOT NULL,
check (Ad_ID>0),
constraint fk_company Foreign Key (Company_id) References BI_8_OLTP.Companies (Company_id)
);
LOAD DATA LOCAL INFILE 'S:\\bi\\csvs\\Advertisements.csv' INTO TABLE BI_8_OLTP.Advertisements FIELDS TERMINATED BY ',' IGNORE 1 LINES;

DROP TABLE IF EXISTS BI_8_OLTP.Advertisements_Displays;
create table BI_8_OLTP.Advertisements_Displays (
Ad_ID	Integer NOT NULL,
Display_date		date		NOT NULL,	
Display_time		time		NOT NULL,
Internal			bool			NOT NULL,
Constraint PK_Ad_Displays Primary Key (Ad_ID,Display_date,Display_time)
);
LOAD DATA LOCAL INFILE 'S:\\bi\\csvs\\Advertisement_Displays.csv' INTO TABLE BI_8_OLTP.Advertisements_Displays FIELDS TERMINATED BY ',' IGNORE 1 LINES;

DROP TABLE IF EXISTS BI_8_OLTP.Advertisements_Clicks;
create table  BI_8_OLTP.Advertisements_Clicks (
User_id		integer NOT NULL,
Click_start_date		date		NOT NULL,	
click_start_time		time		NOT NULL,
Click_end_date		date		NOT NULL,
click_end_time		time		NOT NULL,
Ad_ID					Integer		NOT NULL,
Display_date			date		NOT NULL,	
Display_time			time		NOT NULL,
Constraint PK_Ad_Clicks Primary Key (User_id,Click_start_date,click_start_time),
constraint fk_user2 Foreign Key (User_id) References BI_8_OLTP.Users (User_id),
constraint fk_display Foreign Key (Ad_ID,Display_date,Display_time) References BI_8_OLTP.Advertisements_Displays (Ad_ID,Display_date,Display_time)
);
LOAD DATA LOCAL INFILE 'S:\\bi\\csvs\\Advertisement_Clicks.csv' INTO TABLE BI_8_OLTP.Advertisements_Clicks FIELDS TERMINATED BY ',' IGNORE 1 LINES;

DROP TABLE IF EXISTS BI_8_OLTP.Advertisements_Catagories;
create table BI_8_OLTP.Advertisements_Catagories (
Ad_ID		Integer NOT NULL,
category_id	integer NOT NULL,
constraint PK_Ad_Catagories Primary Key (Ad_ID, category_id),
constraint fk_ad Foreign Key (Ad_ID) References BI_8_OLTP.Advertisements (Ad_ID),
constraint fk_catergory2 Foreign Key (category_id) References BI_8_OLTP.Categories (category_id)
);
LOAD DATA LOCAL INFILE 'S:\\bi\\csvs\\Advertisements_Catagories.csv' INTO TABLE BI_8_OLTP.Advertisements_Catagories FIELDS TERMINATED BY ',' IGNORE 1 LINES;

DROP TABLE IF EXISTS BI_8_OLTP.Packages;
create table BI_8_OLTP.Packages (
Package_id integer	NOT NULL Primary Key,
Package_Type Varchar(20)	NOT NULL ,
Monthly_cost decimal(25,2)		NOT NULL,
check (Monthly_cost>=0)
);
LOAD DATA LOCAL INFILE 'S:\\bi\\csvs\\package.csv' INTO TABLE BI_8_OLTP.Packages FIELDS TERMINATED BY ',' IGNORE 1 LINES;

DROP TABLE IF EXISTS BI_8_OLTP.Companies_Packages;
create table BI_8_OLTP.Companies_Packages (
Company_id		integer	NOT NULL,
start_date		    date		NOT NULL,
Package_id integer	NOT NULL,
constraint PK_Ad_dis_search2 Primary Key (Company_id,start_date),
constraint fk_company2 Foreign Key (Company_id) References BI_8_OLTP.Companies (Company_id),
constraint fk_packages Foreign Key (Package_id) References BI_8_OLTP.Packages (Package_id)
);
LOAD DATA LOCAL INFILE 'S:\\bi\\csvs\\company_package.csv' INTO TABLE BI_8_OLTP.Companies_Packages FIELDS TERMINATED BY ',' IGNORE 1 LINES;



DROP TABLE IF EXISTS BI_8_OLTP.Yearly_category_data;
create table BI_8_OLTP.Yearly_category_data (
Category							Varchar(30)	NOT NULL,
Year_of_data								Integer		NOT NULL,
number_of_companies					integer		NOT NULL,
Profit_from_purchas_by_men			decimal(15,2)		NOT NULL,
Profit_from_Purchas_by_18_to_25_age	decimal(15,2)		NOT NULL,
Profit_from_Purchas_by_26_to_40_age	decimal(15,2)		NOT NULL,
Profit_from_Purchas_by_41_to_65_age	decimal(15,2)		NOT NULL,
Profit_from_Purchas_by_up_to_66_age	decimal(15,2)		NOT NULL,
category_id integer not null,
check (Year_of_data>=1990),
check (Avarage_Profit_for_company>=0),
check (number_of_companies>=0),
check (Profit_from_purchas_by_men>=0),
check (Profit_from_Purchas_by_18_to_25_age>=0),
check (Profit_from_Purchas_by_26_to_40_age>=0),
check (Profit_from_Purchas_by_41_to_65_age>=0),
check (Profit_from_Purchas_by_up_to_66_age>=0)
);
LOAD DATA LOCAL INFILE 'S:\\bi\\csvs\\Yearly_Category_Data.csv' INTO TABLE BI_8_OLTP.Yearly_category_data FIELDS TERMINATED BY ',' IGNORE 1 LINES;

CREATE TABLE IF NOT EXISTS bi_8_dw.datedim  (
    date_id INT NOT NULL auto_increment,
    fulldate date,
    dayofmonth int,
    dayofyear int,
    dayofweek int,
    dayname varchar(10),
    monthnumber int,
    monthname varchar(10),
    year    int,
    quarter tinyint,
    PRIMARY KEY(date_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000;


DROP PROCEDURE IF EXISTS bi_8_dw.datedimbuild;
delimiter //
CREATE PROCEDURE bi_8_dw.datedimbuild (p_start_date DATE, p_end_date DATE)
BEGIN
    DECLARE v_full_date DATE;

    DELETE FROM datedim;

    SET v_full_date = p_start_date;
    WHILE v_full_date < p_end_date DO

        INSERT INTO datedim (
            fulldate ,
            dayofmonth ,
            dayofyear ,
            dayofweek ,
            dayname ,
            monthnumber,
            monthname,
            year,
            quarter
        ) VALUES (
            v_full_date,
            DAYOFMONTH(v_full_date),
            DAYOFYEAR(v_full_date),
            DAYOFWEEK(v_full_date),
            DAYNAME(v_full_date),
            MONTH(v_full_date),
            MONTHNAME(v_full_date),
            YEAR(v_full_date),
            QUARTER(v_full_date)
        );

        SET v_full_date = DATE_ADD(v_full_date, INTERVAL 1 DAY);
    END WHILE;
END//

call bi_8_dw.datedimbuild('2004-01-01','2020-12-31'); 

CREATE TABLE IF NOT EXISTS BI_8_DW.timedim  (

    time_id INT NOT NULL auto_increment,

    fulltime time,

    hour int,

    minute int,

    second int,

    ampm varchar(2),

    PRIMARY KEY(time_id)

) ENGINE=InnoDB AUTO_INCREMENT=1000;

 

DROP PROCEDURE IF EXISTS BI_8_DW.timedimbuild;

delimiter //

CREATE PROCEDURE bi_8_DW.timedimbuild ()

BEGIN

    DECLARE v_full_date DATETIME;

 

    DELETE FROM timedim;

 

    SET v_full_date = '2009-03-01 00:00:00';

    WHILE v_full_date <= '2009-03-01 23:59:59' DO

 

        INSERT INTO timedim (

            fulltime ,

            hour ,

            minute ,

            second ,

            ampm

        ) VALUES (

            TIME(v_full_date),

            HOUR(v_full_date),

            MINUTE(v_full_date),

            SECOND(v_full_date),

            DATE_FORMAT(v_full_date,'%p')

        );

 

        SET v_full_date = DATE_ADD(v_full_date, INTERVAL 1 SECOND);

    END WHILE;

END//

 

call bi_8_dw.timedimbuild();


