public class FireEvent extends Event{ //אירוע אש
	private static int IDCount=0; //משתנה שעוזר להגדיר את הID של האירוע
	private int area;
	private double Arrival;
	private int ID;
	public FireEvent (String Address, int state ,double Arrival, int area  ){
		this.Address = Address ;
		IDCount+=1;
		this.ID = IDCount;
		this.State = state;
		this.Arrival = Arrival;
		this.area = area;
	}
	public void setDistance(int distance){//מגדיר את המרחק לאירוע

		this.distance = distance;
	}	
	public int getLocation() {
		return area;
	}
	public int getID() {
		return ID;
	}
}