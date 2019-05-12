import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FatmaUnit { //יחידת לוחמי האש
	private ArrayList <Employ> employees = new ArrayList <Employ>();
	private ArrayList <Call> callsDetailes = new ArrayList <Call>() ;
	private Queue <Call> readyCalls = new Queue <Call>();
	private Queue <FireEvent> eventsQ = new Queue <FireEvent>();
	private int totalCalls;
	private int trucks;
	private int planes;
	private InformationSystem informationSystem = createInformationSystem();
	private BoundedQueue <ReadyEvent> readyEvQ = new BoundedQueue<ReadyEvent> (15);
	private FireStationManager manager ;
	private int X;
	private int Y;
	private int Z;
	private double stationWorkTime;
	public FatmaUnit(int X, int Y, int Z, double stationWorkTime) { //מקבלת משתנים מהGUI
	this.X = X;
	this.Y = Y;
	this.Z = Z;
	this.stationWorkTime = stationWorkTime;
	}
	public void StartDay() { //פונקציה שמתחילה יום עבודה בתחנה
		trucks =50 +Y; //מספר משאיות בתתחנה
		planes =10 +Z; //מספר מטוסים בתחנה
		getCallsDetails("callsData"); //מקבלת בקלט את השיחות
		totalCalls = callsDetailes.size();
		addDispatchers(5);
		addEventHandelers(3);
		addStationWorkers(3);
		manager= new FireStationManager (totalCalls,employees);
		addEventCommander(5+X);
		startAllThreads(); 
	}
	private void startAllThreads() { //מפעיל את שיטת RUN של כל הסרדים
		for (int j=0; j<callsDetailes.size(); j++)
			callsDetailes.get(j).start();
		for (int i=0;i<employees.size() ; i++) 
			employees.get(i).start();
		manager.start();
	}
	private InformationSystem createInformationSystem(){// יוצר את מערכת המידע
		InformationSystem informationSystem = new InformationSystem();
		informationSystem.addValueAndKey("0-10" , new ArrayList<FireEvent>());
		informationSystem.addValueAndKey("11-20" , new ArrayList<FireEvent>());
		informationSystem.addValueAndKey("20+" , new ArrayList<FireEvent>());
		return informationSystem;
	}
	private void addStationWorkers (int numOfEmployees) {//מוסיף עובדי תחנה
		for(int i = 0; i<numOfEmployees;i++){
			employees.add(new StationWorker(stationWorkTime,"s"+i,informationSystem, readyEvQ ));
		}
	}
	private void addEventCommander(int numOfEmployees) { //מוסיף מפקדי משימות
		for(int i = 0; i<numOfEmployees;i++){
			employees.add(new EventCommander(manager,trucks,planes,readyEvQ));
	}}
	private void addDispatchers(int numOfEmployees) { //מוסיף מוקדנים
		for(int i = 0; i<numOfEmployees;i++){
			employees.add(new Dispatcher("d"+i, totalCalls, eventsQ,readyCalls ));
		}}
	private void addEventHandelers(int numOfEmployees){ //מוסיף אחראי אירועים
			for(int i = 0; i<numOfEmployees;i++){
				employees.add(new EventHandler("e"+i,informationSystem, eventsQ));
		}
		}	
	private void getCallsDetails(String filename){// מוסיף בקלט את פרטי השיחה
		BufferedReader in = null;
		String str;
		String[] callsData;
		try {
			in = new BufferedReader (new FileReader(filename+".txt"));
			str=in.readLine();

			while(in.ready()){
				str=in.readLine();
				callsData=str.split("\t");
				Call newCall = new Call((callsData[4]),Integer.parseInt( callsData[3]),Double.parseDouble(callsData[2]),Integer.parseInt(callsData[1]),Integer.parseInt(callsData[0]), readyCalls);
				callsDetailes.add(newCall);
			}
		}
		catch (IOException e) {
			System.out.println("Couldn't read file");
		}
	}
}