public class ReadyEvent extends Event{ 
	private Available numOfTrucks;
	private int truckOnEvent; //משאיות שבפועל נשלחו למשימה
	private int numOfPlanes=0;
public ReadyEvent (String Address, int state, int distance,Available numOfTrucks,int numOfPlanes){
	this.Address = Address ;
	this.State = state;
	this.distance = distance;
	this.numOfTrucks = numOfTrucks;
	this.numOfPlanes = numOfPlanes;
	this.truckOnEvent=0;
}
public Available getAvailable (){ //שיטה שמחזירה את מספר המשאיות 
	return numOfTrucks;
}
public int planesNeeded(){
	return numOfPlanes;
}
public int getTruckOnEvent(int freeTrucks, int state) {
	if (truckOnEvent!=0) return truckOnEvent; //במקרה שכבר חזרנו מהאירוע, אסור לשנות את מס' המשאיות
	else {
	int truckOnEvent =numOfTrucks.TrucksNeeded(freeTrucks, state);
	return truckOnEvent;
}}
}