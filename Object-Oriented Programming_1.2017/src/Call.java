public class Call extends Thread{ //שיחה המכילה פרטים על אירוע אש
private String Address; //כתובת האירוע
private boolean handled; //משתנה בוליאני שעוזר לקבוע האם השיחה טופלה או לאד
private int State; //חומרת האירוע
private int area; //אזור האירוע
private double Time; //משך השיחה
private int Arrival; //זמן עד שהשיחה מגיעה לתור
private static Queue <Call> readyCalls; //תור שיחות המוכנות לטיפול של המוקדנים
private Object key ;  //אובייקט שמסייע לסנכרן בין שיחות למוקדנים
public Call (String Address ,int Arrival ,double Time , int area ,int State, Queue <Call> readyCalls){ 
	this.Address =Address;
	this.Arrival =Arrival;
	this.Time =Time;
	this.area =area;
	this.State = State;
	Call.readyCalls = readyCalls; //תור השיחות, מגיע מהיחידה כמשותף לשיחות ולמוקדנים.
	key = new Object();
	handled = false;
}
public void run(){
	try {
		Thread.sleep(Arrival*1000); //השיחה מחכה לפני שנכנסת לתור
		readyCalls.add(this); //נכנסת לתור
		synchronized(key) { //מחכה שיסיימו לטפל בה
			while(!handled){ //אם המוקדן לא טיפל בשיחה, היא מחכה
				key.wait(); 
			}
		}	
	}
	catch(InterruptedException err) {
	}
}
public double getTime() {
	return Time;
}
public String getAddress() {
	return Address;
}
public int getSeverity() {
	return State;
}
public int getArea() {
	return area;
}
public double getArrival() {
	return Arrival;
}
public Object getKey() {
	return key;
}
public void handledTheCall(){
	handled = true;
}

}
