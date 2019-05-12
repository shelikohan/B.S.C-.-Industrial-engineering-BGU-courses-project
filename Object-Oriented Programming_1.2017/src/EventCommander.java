public class EventCommander extends Employ implements dayIsOver{
	private static int trucks; //מס' המשאיות שלא נמצאות באירועים
	private static int planes; //מספר המטוסים שלא נמצאים באירועים
	private static BoundedQueue<ReadyEvent> readyEvQ ; //תור האירועים הממתינים לשילוח
	private boolean dayIsOver = false; //משתנה בוליאני שעוזר לקבוע אם נגמר היום
	private int workTime =0; //קלט מהגוי
	private static final Object lockEC = new Object();
	private static FireStationManager manager;
	public EventCommander(FireStationManager manager,int trucks, int planes, BoundedQueue<ReadyEvent> readyEvQ)
	{
		EventCommander.trucks = trucks;
		EventCommander.planes = planes;
		EventCommander.readyEvQ = readyEvQ;
		EventCommander.manager = manager;
	}
	public void run(){
		while(!dayIsOver){ //כל עוד לא נגמר היום
			ReadyEvent r;
			synchronized(lockEC) {
				r =readyEvQ.extract();
			}
			if(r!=null){ //היום נגמר
				getVehicles(r);
				goToMission(r);
				returnToUnit(r);
				notifyTheManager(r);
			}
		}
		if (dayIsOver){
			notifyAll();
			readyEvQ.endDay();
			dayIsOver =false; //מוכן להתחיל יום חדש, אין צורך שכל הסרדים יכנסו לכאן.
		}
	}
	private synchronized static void notifyTheManager(ReadyEvent r) { //מודיע למפקד על סיום אירוע
		manager.notifyEventEnded(r.getSeverity());

	}
	private synchronized void returnToUnit(ReadyEvent r) {    //מחזיר את הרכבים ליחידה 
		trucks += r.getTruckOnEvent(trucks, r.getSeverity());
		planes += r.planesNeeded();
		System.out.println("trucks when return" +trucks);
		System.out.println("planes when return" +planes);
		notifyAll(); //מעיר את הסרדים שמחכים למשאיות
	}
	private void goToMission(ReadyEvent r) { //יוצא למשימה
		try {
			workTime = (r.getSeverity()*2 + r.getDistance())/ (trucks + planes); 
			Thread.sleep(workTime*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	private void getVehicles(ReadyEvent r) { //מגדיר כמה רכבים הוא צריך לאירוע
		int trucksForEvent =r.getTruckOnEvent(trucks, r.getSeverity());
		int planesForEvent = r.planesNeeded();
		while (trucksForEvent > trucks || planesForEvent > planes){ //אין מספיק רכבים
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		takeVehicles(trucksForEvent ,planesForEvent);
	}
	private synchronized void takeVehicles(int trucksE,int planesE) { //לוקח את הרכבים לאירוע
		if (trucksE < trucks || planesE < planes) { //יש מספיק רכבים
			trucks -=trucksE;
			planes -= planesE;
			System.out.println("trucks when left" +trucks);
			System.out.println("planes when left" +planes);
		}}
	public void dayIsOver() {
		dayIsOver = true;
	}
}