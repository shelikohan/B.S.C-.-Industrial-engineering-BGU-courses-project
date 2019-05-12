public class StationWorker extends EmployWithSalaryAndName implements dayIsOver {//עובד תחנה עםם שם ומשכורת ומודיעים לו על סוף היום
	private static InformationSystem infoSys ;
	private static BoundedQueue <ReadyEvent> readyEvQ;
	private static boolean dayIsOver =false;
	private double workTime ;
	private static final Object lockSW= new Object();
public StationWorker (double workTime,String name, InformationSystem infoSys, BoundedQueue <ReadyEvent> readyEvQ){
	this.name = name;
	this.salary = 100; //משכורת בסיסית של עובד תחנה ליום
	this.workTime = workTime;
	StationWorker.infoSys = infoSys;
	StationWorker.readyEvQ = readyEvQ;
}
public void run() {
	while(!dayIsOver){ //כל עוד לא נגמר היום
		if(handleEvent())
		{
			workTime();
		}
}	
	if (dayIsOver) { //נגמר היום
		infoSys.dayIsOver(); 
		
	}
}
private boolean handleEvent() { //
	synchronized (lockSW) {
		FireEvent e = infoSys.extract();//מחלץ אירוע ממערכת המידע
		if(e==null)
		{
			return false;
		}
			Available trucksNum = getNumOfTrucks(e);
			int planesNum = getNumOfPlanes(e);
			addSalary(3); //מוסיף 3 למשכורת
			ReadyEvent rv = new ReadyEvent(e.getAddress(), e.getSeverity(),e.getDistance() ,trucksNum, planesNum);
			readyEvQ.add(rv); //מוסיף לתור האירועים המוכנים לשילוח
		//}
	}
	return true;
}
private void workTime() { //זמן עבודה
	try {
		Thread.sleep((long) (1000*workTime));
	} catch (InterruptedException e1) {
		e1.printStackTrace();
	}
}
	private int getNumOfPlanes(FireEvent e) { //תשיג את מס' המטוסים
	int state = e.getSeverity();
	if (state ==3) return 1;
	if (state ==4) return 2;
	if (state ==5) return 3;
	else return 0;
}
	private Available getNumOfTrucks(FireEvent e) { //תשיג את מס' המשאיות
		int state = e.getSeverity();
		Available a;
		if (state ==1) return a= new Available(0,2);
		if (state ==2) return a= new Available(0,2);
		if (state ==3) return a= new Available(1,2);
		if (state ==4) return a= new Available(2,2);
		else return a = new Available(3,2);
}
	public void dayIsOver() {
		dayIsOver = true;
		
	}
}
