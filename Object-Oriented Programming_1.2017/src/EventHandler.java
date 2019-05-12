public class EventHandler extends  EmployWithSalaryAndName implements dayIsOver{
	private static Queue <FireEvent> eventsQ;
	private static final Object lockEH = new Object();
	private int workTime ;
	private static InformationSystem informationSystem;
	private static boolean dayIsOver =false;
	public EventHandler (String name, InformationSystem info,Queue <FireEvent> eventsQ ) {
	this.name =name;
	workTime =3;
	EventHandler.informationSystem =info;
	EventHandler.eventsQ = eventsQ;	 
}
public void run(){
	while(!dayIsOver){ //כל עוד היום לא נגמר
		addEventToInfoSys();
		}
	
	if (dayIsOver){ //אם היום נגמר
		eventsQ.endDay(); //תעיר את הסרדים שישנים על התור
		dayIsOver =false; //תתכונן ליום הבא בו היום לא נגמר
	}
}
private void addEventToInfoSys() { //מוסיף אירוע לתור
	synchronized(lockEH)
	{
		FireEvent e = eventsQ.extract(); //מוציא אירוע מתור האירועים
		if(e!=null) {
			System.out.println("Notice - New Emergency "+e.getID());
			setDistance (e);
			addSalary (workTime);
			informationSystem.insert(e); // מכניס אותו למערכת המידע
		}
	}
	try {
		Thread.sleep(workTime*1000); //זמן עבודה
	} catch (InterruptedException err) {
		err.printStackTrace();
	}
}
private static void setDistance (FireEvent event) { //מגדיר אצל אירוע את המרחק
	int d = getDistance(event.getAddress());
	event.setDistance(d);
}
private static int getDistance (String Address) { //מחשב את המרחק
	int wordsNum = CountWords (Address) ;
	int distance =0;
	char firstL = Address.charAt(0);
	distance += addByWordNum (wordsNum);
	distance += addByFirstChar(firstL);
	return distance;	
}
private static int addByWordNum(int wordsNum) { //מוסיף למרחק לפי מספר מילים
	int max=0;
	int min =0;
	if (wordsNum==1) {
		max=5;
		min=1;
		int ranNumber = min + (int)(Math.random() * ((max - min) + 1));
		return ranNumber;
	}
	if (wordsNum ==2) {
		max=7;
		min=3;
		int ranNumber = min + (int)(Math.random() * ((max - min) + 1));
		return ranNumber;
	}
	if (wordsNum>=3) {
		max=5;
		min=1;
		int ranNumber = min + (int)(Math.random() * ((max - min) + 1));
		return ranNumber;
		}
	else return 0;
}
private static int addByFirstChar(char c) { //מוסיף למרחק לפי אות ראשונה
	int max =0;
	int min=0;
	if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
		max=20;
		min=8;
		int ranNumber = min + (int)(Math.random() * ((max - min) + 1));
		return ranNumber;
	}
	if (c >= '0' && c <= '9'){
		int num = Character.getNumericValue(c);
		return num;	
}
	else return 0;
}
private static int CountWords (String str) { //סופר מילים
	int count =0;
	for(int e = 0; e < str.length(); e++){
        if(str.charAt(e) != ' '){
            count++;
            while(str.charAt(e) != ' ' && e < str.length()-1){
                e++;
            }
}}
	return count;
	}
public void dayIsOver() { //כך המנהל מודיע שהיום נגמר
	dayIsOver =true;
	informationSystem.dayIsOver();
	
}
		}