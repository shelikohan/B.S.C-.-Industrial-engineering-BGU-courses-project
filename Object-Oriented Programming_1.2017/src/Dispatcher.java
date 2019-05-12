public class Dispatcher extends EmployWithSalaryAndName{
	private static Queue<Call> callsQ; //תור שמכיל את כל השיחות.
	private static Queue <FireEvent> eventsQ;
	private static int totalCalls; //מספר השיחות שנשאר עוד לטפל בהן
private static Object lock = new Object();
private static int handeledCalls =0; //  מספר השיחות שטיפלנו בהן
public Dispatcher (String name, int totalCalls, Queue <FireEvent> eventsQ, Queue<Call> callsQ) {
	this.name = name; 
	Dispatcher.totalCalls = totalCalls;
	Dispatcher.eventsQ = eventsQ;
	Dispatcher.callsQ =callsQ;
}
public void run(){
	while( handeledCalls < totalCalls){  //כל עוד לא נגמר היום
		AnswerACall();	
	}
	if (handeledCalls == totalCalls) { //להעיר את כל הסרדים, נגמר היום
		callsQ.endDay();
	}
}
private void AnswerACall() {
	Call firstC;
	synchronized(lock) {
	firstC = callsQ.extract();//חילוץ שיחה ראשונה מתור תור שיחות
	if(firstC == null) return; // when the day finish, the queue is null with calls.
	handeledCalls++;
	}
	addSalary(firstC.getTime()*3 +0.5); //מוסיף משכורת למוקדן
	int random = 1000 + (int)(Math.random() * ((1000) + 1));
	try {
		Thread.sleep((long) (firstC.getTime()*1000+random));
		synchronized(firstC.getKey()){
		firstC.handledTheCall(); // tell the call that he handled her.
		firstC.getKey().notifyAll(); //מעיר את השיחה
		}
		
	 }catch (InterruptedException e) {
		e.printStackTrace();
	}
	eventsQ.add(new FireEvent(firstC.getAddress(),firstC.getSeverity() ,firstC.getArrival(),firstC.getArea() )); //מוסיף אירוע לרשימת אירועים 
}
}
