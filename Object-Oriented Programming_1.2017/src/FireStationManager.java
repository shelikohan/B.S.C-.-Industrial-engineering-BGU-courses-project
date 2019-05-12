import java.util.ArrayList;
public class FireStationManager extends Thread{ //���� ����� ��� ��� ���� ���� EMPLOY �����
	private static int numOf5levelEvent;
	private static int eventHandeled=0;
	private int callsExpected;
	private ArrayList <Employ> employees;
	public FireStationManager(int callsExpected,ArrayList<Employ>employees){
		this.employees = employees;
		this.callsExpected = callsExpected;
}
public void run() {
	waitDayToEnd(); //��� ����� ����
	notifyThatDayIsOver();//������ ���� , ����� �������
	setStationWorkersSalary(); //����� �� �������� ����
	System.out.println("The day is over"); //����� ����� ����
	printSalaries(); //����� �� �� �� ��������
	printEvents(); //����� ������� ������
}
private synchronized void waitDayToEnd() {
	while (eventHandeled<callsExpected) {
		try {
			
			wait ();
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}}

}
private void printEvents() {
	System.out.println("Total Events Handled:"+callsExpected);
	System.out.println("Total serious Events:"+numOf5levelEvent);
	System.out.println("Good Job Everybody");
	
}
private void printSalaries() {
	double totalSalary =0;
	for (int i=0; i< employees.size(); i++) {
		if (employees.get(i) instanceof EmployWithSalaryAndName)
			totalSalary+=((EmployWithSalaryAndName) employees.get(i)).getSalary();
	}
	System.out.println("Total salaries: " +totalSalary);
}
private void setStationWorkersSalary() {
	for (int i=0; i< employees.size(); i++)
		if (employees.get(i) instanceof StationWorker)
		((EmployWithSalaryAndName) employees.get(i)).addSalary(100);
}
private void notifyThatDayIsOver() {
	for (int i=0; i<employees.size();i++) {
		if(employees.get(i) instanceof dayIsOver)
				((dayIsOver) employees.get(i)).dayIsOver();
	}
}
public synchronized void notifyEventEnded(int level){ //�� ����� ������� ������� ����� ����� ������
	eventHandeled++;
	if (level ==5) numOf5levelEvent++;
	if (eventHandeled == callsExpected){
	notifyAll();
	}
}
}
