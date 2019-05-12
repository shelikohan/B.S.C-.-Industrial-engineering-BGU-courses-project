SELECT Email = C.Email, First_Name =C.First_Name, Last_Name=C.Last_Name, TotalBuy=SUM(P.Price*QIO.Quantity)
FROM dbo.CUSTOMERS as C join dbo.Credit_Cards as CC on C.Email=CC.Customer_ID
join  dbo.ORDERS as O on CC.Credit_Number=o.Credit_Number
join dbo.QuantitiesInOrders as QIO on O.Order_ID = QIO.Order_ID join dbo.Products as P on QIO.Catalog_Number=P.Catalog_Number
WHERE (YEAR(GETDATE())-YEAR(O.Date))<=2
GROUP BY C.Email,C.First_Name, C.Last_Name
HAVING SUM(P.Price*QIO.Quantity)>=2000
ORDER BY 4 DESC

SELECT top 5 P.brand,SUM(P.Price) AS revenue
FROM dbo.ORDERS as O JOIN DBO.QuantitiesInOrders QID ON O.Order_ID=QID.Order_ID JOIN dbo.products as P ON QID.catalog_Number = P.catalog_Number
WHERE (YEAR(GETDATE())-YEAR(O.Date))<5
GROUP BY P.brand
HAVING SUM(P.Price)<5000
ORDER BY 2


SELECT P.Catalog_Number, P.Product_Name, revenue=QIN.Number_of_Sales*p.Price,
(QIN.Number_of_Sales*P.Price)/(
SELECT sum(Q.quantity* P.price)
FROM   dbo.QuantitiesInOrders AS Q JOIN dbo.Products AS p ON p.Catalog_Number = q.Catalog_Number)
AS Proportion
FROM dbo.ORDERS as O JOIN DBO.QuantitiesInOrders QID ON O.Order_ID=QID.Order_ID JOIN dbo.products as P ON QID.catalog_Number = P.catalog_Number JOIN
 (SELECT q.Catalog_Number, SUM(q.Quantity) AS Number_of_Sales
FROM dbo.QuantitiesInOrders AS q
GROUP BY  q.Catalog_Number ) AS QIN ON QIN.Catalog_Number = P.Catalog_Number
WHERE YEAR(getdate()) -YEAR(o.Date) <=5
GROUP BY P.Catalog_Number, P.Product_Name, QIN.Number_of_Sales*p.Price
ORDER BY Proportion DESC



select w.Warehouse_ID, [num of orders]=count(ord.Order_ID), revenue=sum(quan.Quantity*p.Price)
from Warehouses as w join QuantitiesInOrders as quan on w.Warehouse_ID=quan.WareHouse_ID join Orders as ord on ord.Order_ID=quan.Order_ID join Products as p on p.Catalog_Number=quan.Catalog_Number
where w.Warehouse_ID not  in (
select distinct qin.WareHouse_ID
from QuantitiesInOrders as qin join Orders as o on qin.Order_ID=o.Order_ID
where year(o.Date)=year(getDate())-1)
group by w.Warehouse_ID
order by 3	


ALTER TABLE dbo.ORDERS ADD Total_revenue FLOAT NULL

UPDATE dbo.Orders
SET ORDERS.Total_revenue=
      		(SELECT (SUM(p.Price*qio.quantity))
FROM dbo.ORDERS as O join dbo.QuantitiesInOrders as QIO on O.Order_ID=QIO.Order_ID join dbo.Products as P on P.Catalog_Number=QIO.Catalog_Number
	
	WHERE ORDERS.Order_ID=O.Order_ID) 



	
CREATE TABLE dbo.VIP_Customers (
Email varchar(30)               		NOT NULL  	   	PRIMARY KEY,
Rank            	 char(1)        	  NOT NULL,
CONSTRAINT ck_CheckMailVIP CHECK ([Email] LIKE '%_@__%.__%'),
)

 ALTER TABLE dbo.Customers ADD Num_of_Orders FLOAT NULL

UPDATE dbo.CUSTOMERS

SET CUSTOMERS.Num_of_Orders = (SELECT COUNT(O.Order_ID)
FROM dbo.CUSTOMERS as C join dbo.CREDIT_CARDS as CC on C.Email=CC.Customer_ID join dbo.ORDERS as O on CC.Credit_Number=O.Credit_Number
      		WHERE CUSTOMERS.Email=C.Email)
 
INSERT INTO dbo.VIP_Customers
      		SELECT Email,
                     	  Rank=(CASE
                                                      WHEN Num_of_Orders>=10 THEN 'A'
                                                      When Num_of_Orders>=5 AND num_of_orders<=9 THEN 'B'
                                                      ELSE 'C'
                                   	  END)
      		FROM dbo.CUSTOMERS
      		WHERE CUSTOMERS.Num_of_Orders>=3
 

 CREATE VIEW [Unfulfilled products desires] AS
SELECT top 10 wl.Catalog_Number,p.Product_Name , [num of products] = COUNT(*)
FROM dbo.WishLists wl LEFT join dbo.QuantitiesInOrders qio ON wl.Catalog_Number = qio.Catalog_Number JOIN dbo.Products p ON wl.Catalog_Number = p.Catalog_Number
WHERE qio.Order_ID IS NULL
GROUP BY wl.Catalog_Number,p.Product_Name
ORDER BY 3 desc
go



CREATE FUNCTION dbo.Order_Price (@Order_ID int)
RETURNS int
AS   	BEGIN
                   	DECLARE   	@Price        	int
                   	SELECT                  	@Price = sum(QIO.Quantity*P.price)
                   	from (dbo.QuantitiesInOrders QIO
                    	JOIN dbo.Products P ON  QIO.Catalog_Number=P.Catalog_Number)
                    	WHERE                  	qio.Order_ID = @Order_ID
                    	GROUP BY 	qio.Order_ID
                    	RETURN                 	@Price
                    	END

select  c.Customer_ID,[Total Amount]= sum(dbo.Order_Price(o.Order_Id))
                    	from dbo.Credit_Cards c JOIN orders o ON o.Credit_Number=c.Credit_Number
group by c.Customer_ID



CREATE FUNCTION dbo.Get_Products_List_per_Customer (@E_Mail varchar(30))
RETURNS  @Products_per_Customer
		TABLE ([E-Mail] varchar(30), [First-Name] varchar(15), [Last-Name] varchar(15) ,[Product-ID] varchar(50), [product name] varchar(100), [Category] varchar (20) )
AS
BEGIN
	INSERT @Products_per_Customer
		SELECT C.Email, C.First_Name, C.Last_Name, P.Catalog_Number, P.Product_Name, p.Category
		FROM QuantitiesInOrders qio join Products p on p.catalog_number = qio.catalog_number join orders o on o.Order_ID = qio.Order_ID join Credit_Cards cc on cc.Credit_Number = o.Credit_Number 
			join Customers c on c.Email = cc.Customer_ID
		WHERE C.Email=@E_Mail
RETURN
END

SELECT *
FROM dbo.Get_Products_List_per_Customer('abustin5e@jimdo.com')



--drop trigger updateNumberOfOrdersForCustomer
--alter table dbo.customers drop column num_of_orders

------Updating the correct amount of orders before the trigger------
alter table Customers add [Num_Of_Orders] int 


UPDATE dbo.Customers
SET Customers.[Num_Of_Orders]= 
	(SELECT COUNT(O.Order_ID)
	FROM dbo.Credit_Cards as CC join dbo.ORDERS as O on CC.Credit_Number=O.Credit_Number
	WHERE Customers.Email=CC.Customer_ID)

--------Creating the trigger--------

create trigger updateNumberOfOrdersForCustomer
ON dbo.orders
AFTER INSERT
AS
UPDATE dbo.Customers
SET [Num_Of_Orders]=[Num_Of_Orders]+(SELECT COUNT(A.Order_ID)
	 FROM INSERTED as A join Credit_Cards as CC on CC.Credit_Number=A.Credit_Number
						 WHERE Customers.Email=CC.Customer_ID)


INSERT INTO dbo.Orders ([Order_ID], [Date], [Credit_Number] , [City], [Street], [Number],[Total_revenue])
values ('1236','04-06-17',2010000000000000,'Aliquippa','Golf',270 ,100)



EXECUTE ON object.

drop PROCEDURE dbo.SP_UpdatePrice
CREATE PROCEDURE dbo.SP_UpdatePrice @productID bigint, @Action varchar(10), @Rate real
AS
        	IF (@Action='UP') BEGIN
                    	UPDATE dbo.Products
                    	SET Price=(1+@Rate)*products.Price
                    	WHERE products.Catalog_Number= @productID
        	END
        	ELSE IF (@Action='DOWN') BEGIN
                    	UPDATE dbo.Products
                    	SET Price=(1-@Rate)*products.Price
                    	WHERE products.Catalog_Number= @productID
        	END
 
GRANT EXECUTE ON object::dbo.SP_UpdatePrice to Manager
 
דוגמה-- לשימוש בפרוצדורה
EXECUTE dbo.SP_UpdatePrice '1','UP',0.2


הם זכאים למתנה או לא באמצעות cursor. 
--drop trigger update_gift
--alter table dbo.customers drop column num_of_orders
--alter table dbo.Customers add [num_of_orders] int 

------------------------------------------------------------------------------------------------------

update dbo.Customers     --update num_of_orders after inserted values
set num_of_orders=(select count(*)
 from dbo.Customers as c join dbo.Credit_Cards as cc on cc.Customer_ID=c.Email join Orders as o on o.Credit_Number=cc.Credit_Number
             		  where dbo.Customers.Email=c.Email
             		   group by c.Email)
update dbo.Customers
set gift='Gift'
where Num_of_Orders between 3  and 5 

update dbo.Customers
set gift='Free Wine'
where Num_of_Orders>=6

create trigger update_gift
on dbo.orders
for insert
as
declare @c_Email varchar(255)
declare @c_num_of_orders int
 
update dbo.Customers     --update num_of_orders after inserted values
set num_of_orders=(select count(*)
 from dbo.Customers as c join dbo.Credit_Cards as cc on cc.Customer_ID=c.Email join Orders as o on o.Credit_Number=cc.Credit_Number
            		  where dbo.Customers.Email=c.Email
           		   group by c.Email)
declare c_cursor cursor-- chaining the gift attribute according to the inserted values (if necessery)
for select email,num_of_orders
from inserted join dbo.Credit_Cards as cc on cc.Credit_Number=inserted.Credit_Number join dbo.Customers as c on c.Email=cc.Customer_ID
begin
open c_cursor
fetch next from c_cursor
into @c_email, @c_num_of_orders
while (@@FETCH_STATUS=0)
begin
if (@c_num_of_orders between 3 and 5)
begin
update dbo.Customers
set gift='Gift'
where dbo.Customers.Email=@c_Email
end
if (@c_num_of_orders>=6)
begin
update dbo.customers
set gift='Free Wine'
where dbo.Customers.Email=@c_Email
end
fetch next from c_cursor
into @c_email, @c_num_of_orders
end
close c_cursor
end





INSERT INTO dbo.Orders ([Order_ID], [Date], [Credit_Number] , [City], [Street], [Number],[Total_revenue])
values ('1236','04-06-17',2010000000000000,'Aliquippa','Golf',270 ,100)
INSERT INTO dbo.Orders ([Order_ID], [Date], [Credit_Number] , [City], [Street], [Number],[Total_revenue])
values ('1237','04-06-17',2010000000000000,'Aliquippa','Golf',270 ,100)
 
 ובסוף מציגה את טבלת המעקב אחר המוצרים הפחות רווחיים.
alter table dbo.products
add rank_category char(1) not null default 'A'
 
alter table dbo.products
add Date_Of_Update Datetime

 alter table dbo.products add num_of_sales int

update dbo.Products
set products.num_of_sales= (select count(o.Order_ID)
from dbo.Products as p join dbo.QuantitiesInOrders as qio on qio.Catalog_Number=p.Catalog_Number join dbo.Orders as o on o.Order_ID=qio.Order_ID
where dbo.Products.Catalog_Number=p.Catalog_Number)
drop function dbo.CategoryProportion

create function dbo.CategoryProportion(@category varchar(50))
returns bit
as begin
declare @result bit
select @result= (case when (cast (num_of_sales as float))/(cast((select sum (num_of_sales)
from dbo.Products) as float))>0.05 then 1 else 0 end)
from dbo.products as p
where p.category=@category
return @result
end
 
--drop function dbo.lastTimeBought
create function dbo.lastTimeBought(@category varchar(50))
returns bit
as begin
declare @result bit
select @result=(case when(DATEDIFF(dd,o.Date,getDate())<90) then 1 else 0 end)
from dbo.Products as p join QuantitiesInOrders as qio on p.catalog_number=qio.catalog_number join dbo.Orders o on o.order_ID=qio.order_ID
where p.category=@category
return @result
end
 
 
 --drop procedure dbo.SortingCategory
 create procedure dbo.SortingCategory(@category varchar(50))
 as
 if (dbo.lastTimeBought(@category)=1 and  dbo.CategoryProportion(@category)=1)
 begin
 
 update dbo.Products
 set rank_category='A'
 where dbo.Products.Category=@category
 
 update dbo.Products
 set date_of_update=getDate()
  where dbo.Products.Category=@category
 
  end
 
  else if (dbo.lastTimeBought(@category)=0 and  dbo.CategoryProportion(@category)=0)
  begin
  update dbo.Products
  set rank_category='C'
   where dbo.Products.Category=@category
 
   update dbo.Products
   set date_of_update=getDate()
	where dbo.Products.Category=@category
 
   	end
 
   	else
   	begin
   	update dbo.Products
   	set rank_category='B'
   	 where dbo.Products.Category=@category
 
   	 update dbo.Products
   	 set date_of_update=getDate()
   	  where dbo.Products.Category=@category
   	  end
 
   	  --drop view dbo.supervisioned_Category
   	  create view dbo.supervisioned_Category as
   	  select Category,date_of_update
   	  from dbo.Products
   	  where dbo.Products.rank_category='C'
 
   	--  drop procedure dbo.show_supervisioned_Category
 create procedure dbo.show_supervisioned_Category as
 select *
 from dbo.supervisioned_Category
 
 exec dbo.show_supervisioned_Category
 
-- drop procedure dbo.update_ranks_show_supervisioned_Category
 create procedure dbo.update_ranks_show_supervisioned_Category as
 
 execute dbo.SortingCategory 'Red Wine'
 execute dbo.SortingCategory 'White Wine'
 execute dbo.SortingCategory  'Vodkas'
 execute dbo.SortingCategory   'Brandy'
 execute dbo.SortingCategory 'Gins'
-- execute dbo.SortingCategory
 
 execute dbo.update_ranks_show_supervisioned_Category



 קוד ליצירת הדו"ח
CREATE FUNCTION dbo.Maxgender(@product BigInt, @Gender char) 
RETURNS int
AS 	BEGIN
		DECLARE @countGender int
		select @countGender= case when count (*) > 0 then count (*)
								else 0 end
		from QuantitiesInOrders qio right join products p on qio.Catalog_Number= p.Catalog_Number  left join 
		orders o on qio.Order_ID = o.Order_ID left join Credit_Cards cc on cc.Credit_Number= o.Credit_Number
		left join Customers c on c.Email = cc.Customer_ID 
where C.Gender=@gender and p.catalog_number =@product
		RETURN @countGender
		END
drop FUNCTION dbo.Maxgender
----------------------------------------------------------------------------------
select p.catalog_number, Revenue = sum(p.price* qio.Quantity),[num of customers]=p.num_of_sales,[distribution in warehouses],[items in stock],[items sold]=sum(qio.quantity) ,[Rank] =RANK() OVER   
    ( ORDER BY sum(p.price* qio.Quantity) desc),[main gender purchesers] = case when dbo.Maxgender(p.catalog_number, 'M') > dbo.Maxgender(p.catalog_number, 'F')  then 'M'
																			when  dbo.Maxgender(p.catalog_number, 'M') <  dbo.Maxgender(p.catalog_number, 'F') then 'F'
																			when  dbo.Maxgender(p.catalog_number, 'M') =  dbo.Maxgender(p.catalog_number, 'F') and  dbo.Maxgender(p.catalog_number, 'F')!=0 then 'both'
																			else 'None'
																			end
from QuantitiesInOrders qio right join products p on qio.Catalog_Number= p.Catalog_Number  left join 
	orders o on qio.Order_ID = o.Order_ID left join Credit_Cards cc on cc.Credit_Number= o.Credit_Number
	left join Customers c on c.Email = cc.Customer_ID join (select p.Catalog_Number,[distribution in warehouses]= count( i.WareHouse_ID)
from products as p left join inventory as i on p.Catalog_Number=i.Catalog_Number
group by p.Catalog_Number) as a on a.Catalog_Number =p.Catalog_Number join  (select p.Catalog_Number, [items in stock]= sum (I.Quantity_in_stock)
from dbo.Products p left join dbo.inventory i on i.Catalog_Number=p.Catalog_Number
group by p.Catalog_Number) as b on b.Catalog_Number=p.Catalog_Number
group by p.Catalog_Number,p.num_of_sales,[distribution in warehouses], [items in stock],case when dbo.Maxgender(p.catalog_number, 'M') > dbo.Maxgender(p.catalog_number, 'F')  then 'M'
																			when  dbo.Maxgender(p.catalog_number, 'M') <  dbo.Maxgender(p.catalog_number, 'F') then 'F'
																			when  dbo.Maxgender(p.catalog_number, 'M') =  dbo.Maxgender(p.catalog_number, 'F') and  dbo.Maxgender(p.catalog_number, 'F')!=0 then 'both'
																			else 'None'
																			end
go
CREATE FUNCTION dbo.Maxgender(@product BigInt, @Gender char) 
RETURNS int
AS 	BEGIN
		DECLARE @countGender int
		select @countGender= case when count (*) > 0 then count (*)
								else 0 end
		from QuantitiesInOrders qio right join products p on qio.Catalog_Number= p.Catalog_Number  left join 
		orders o on qio.Order_ID = o.Order_ID left join Credit_Cards cc on cc.Credit_Number= o.Credit_Number
		left join Customers c on c.Email = cc.Customer_ID 
where C.Gender=@gender and p.catalog_number =@product
		RETURN @countGender
		END
drop FUNCTION dbo.Maxgender
----------------------------------------------------------------------------------
select p.catalog_number, Revenue = sum(p.price* qio.Quantity),[num of customers]=p.num_of_sales,[distribution in warehouses],[items in stock],[items sold]=sum(qio.quantity) ,[Rank] =RANK() OVER   
    ( ORDER BY sum(p.price* qio.Quantity) desc),[main gender purchesers] = case when dbo.Maxgender(p.catalog_number, 'M') > dbo.Maxgender(p.catalog_number, 'F')  then 'M'
																			when  dbo.Maxgender(p.catalog_number, 'M') <  dbo.Maxgender(p.catalog_number, 'F') then 'F'
																			when  dbo.Maxgender(p.catalog_number, 'M') =  dbo.Maxgender(p.catalog_number, 'F') and  dbo.Maxgender(p.catalog_number, 'F')!=0 then 'both'
																			else 'None'
																			end
from QuantitiesInOrders qio right join products p on qio.Catalog_Number= p.Catalog_Number  left join 
	orders o on qio.Order_ID = o.Order_ID left join Credit_Cards cc on cc.Credit_Number= o.Credit_Number
	left join Customers c on c.Email = cc.Customer_ID join (select p.Catalog_Number,[distribution in warehouses]= count( i.WareHouse_ID)
from products as p left join inventory as i on p.Catalog_Number=i.Catalog_Number
group by p.Catalog_Number) as a on a.Catalog_Number =p.Catalog_Number join  (select p.Catalog_Number, [items in stock]= sum (I.Quantity_in_stock)
from dbo.Products p left join dbo.inventory i on i.Catalog_Number=p.Catalog_Number
group by p.Catalog_Number) as b on b.Catalog_Number=p.Catalog_Number
group by p.Catalog_Number,p.num_of_sales,[distribution in warehouses], [items in stock],case when dbo.Maxgender(p.catalog_number, 'M') > dbo.Maxgender(p.catalog_number, 'F')  then 'M'
																			when  dbo.Maxgender(p.catalog_number, 'M') <  dbo.Maxgender(p.catalog_number, 'F') then 'F'
																			when  dbo.Maxgender(p.catalog_number, 'M') =  dbo.Maxgender(p.catalog_number, 'F') and  dbo.Maxgender(p.catalog_number, 'F')!=0 then 'both'
																			else 'None'
							end
go




