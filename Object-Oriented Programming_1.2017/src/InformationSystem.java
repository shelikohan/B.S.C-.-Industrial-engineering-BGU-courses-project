import java.util.*;
public class InformationSystem { //מערכת המידע
	private HashMap<String, ArrayList <FireEvent>> informationSystem;	//מפתח לפי מרחק
	private static DataBase dataBase = createDateBase();
	private static boolean dayIsOver =false;
	private Object o;
	public InformationSystem (){//בנאי למערכת מידע
		informationSystem= new HashMap<String, ArrayList <FireEvent>>();
	}
	public synchronized void addValueAndKey(String key, ArrayList <FireEvent> events){ //תגדיר מפתח
		informationSystem.put(key, events);
	}
	public synchronized void insert(FireEvent event){//תכניס אירוע למערכת מידע
		double Distance = event.getDistance();
		if( Distance<=10){// אם המרחק קטן שווה ל10 - תכניס למפתח -0-10
			ArrayList <FireEvent> temp =informationSystem.get("0-10");
			temp.add(event);
			insertToDataBase(event);//תכניס למערכת מידע
		}
		if(Distance > 10  &&  Distance <=20){
			ArrayList <FireEvent> temp =informationSystem.get("11-20");
			temp.add(event);
			insertToDataBase(event);
		}		
		if(Distance > 20){ 
			ArrayList <FireEvent> temp =informationSystem.get("20+");
			temp.add(event);
			insertToDataBase(event);
		}
		notifyAll();
	}
	public synchronized FireEvent extract(){//תוציא אירוע ממערכת המידע
		while (IsEmpty() && dayIsOver==false){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		FireEvent temp = null;
		ArrayList <FireEvent> shortDistance =informationSystem.get("0-10");
		ArrayList <FireEvent> mediumDistance =informationSystem.get("11-20");
		ArrayList <FireEvent> longDistance =informationSystem.get("20+");
		if(shortDistance.size()>0){//אם יש אירועים במרחק בין 10-0
			temp = shortDistance.get(0);
			shortDistance.remove(temp);
			return temp; //מחזיר את האירוע הראשון ברשימה מבין האירועים במרחק 0-10
		}
		if (dayIsOver ==true) return null;
		if(mediumDistance.size()>0){//אם יש אירועים במרחק בין 10-20
			temp = mediumDistance.get(0);
			mediumDistance.remove(temp);
			return temp;
		}
		if (longDistance.size()>0){//אם יש אירועים במרחק גדול מ20
			temp = longDistance.get(0);
			longDistance.remove(temp);
			return temp;
		}
		else return null; //סוף היום
	}
	public synchronized boolean IsEmpty(){// אם המערכת המידע ריקה
		ArrayList <FireEvent> shortDistance =informationSystem.get("0-10");
		ArrayList <FireEvent> mediumDistance =informationSystem.get("11-20");
		ArrayList <FireEvent> longDistance =informationSystem.get("20+");
		if(shortDistance.size()==0 && mediumDistance.size()==0 && longDistance.size()==0 )
			return true;
		else
			return false;
	}
	private synchronized void insertToDataBase(FireEvent event){//מכניס למערכת מידע
			dataBase.insertToTable("FireEvents", event);
	}
	private static DataBase createDateBase(){// יוצר מערכת מידע
		dataBase = new DataBase();
		dataBase.createTable("FireEvents",true);
		return dataBase;
}
	public synchronized void dayIsOver() {
		dayIsOver = true;
		notifyAll();
	}

}