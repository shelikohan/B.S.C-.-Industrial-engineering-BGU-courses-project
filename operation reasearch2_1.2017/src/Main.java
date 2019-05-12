import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
public class Main {
	static int M=0;
	static int C =0;
	static int N =0;
	static int H =0;
	static double P =0;
	static double Q =0;
	static double O =0;
	static int[] X ;
	static int[] Y ;
	public static void main(String[] args) {
		readFromFile1();//����� �������� ����� �� �������	
		readFromFile2();//����� �������� ����� �� ������� �� x  �Y
		Table t=new Table(M, H, N+1); //����� ������� ����������, ����� ��� ����� ������� ���� �����.
		creatLastT(t); //������� ������ �� ����� �������
		ArrayList<Table> allT=new ArrayList<>(); //���� ����� ����� �� �� �������.
		allT.add(t);
		int n=N; //����� ������ �� ��' ������
		for (int i=0;i<N;i++)
		{
		allT.add(createRegularT(allT.get(allT.size()-1), n, i)); //������� ������ ������ ���' ������
		n--;
		}
		writeTablesToTxt(allT);
		System.out.println();
		System.out.println("Done");
	}
	private static void readFromFile2() {
		//����� X Y
		X =new int[N];
		Y = new int [N];
		File file = new File("transportation input.txt");
		try	
		{
			Scanner sc1 = new Scanner(file);
			int i=0;
			while(sc1.hasNext() && i<X.length)
			{
				if(sc1.hasNextInt())
				{
					X[i] = sc1.nextInt();
					i++;
				}
				else
					sc1.next();
			}
			i=0;
			while(sc1.hasNext() && i<Y.length)
			{
				if(sc1.hasNextInt())
				{
					Y[i] = sc1.nextInt();
					i++;
				}
				else
					sc1.next();
			}
			sc1.close();
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	private static void readFromFile1() {
		//��� ��� ������ ������� ������ ����
		File file = new File("variables input.txt");
		try	
		{
			Scanner sc = new Scanner(file);
			boolean flag=false;
			while(sc.hasNext()&&flag==false)
			{
				if(sc.hasNextInt())
				{
					M = sc.nextInt();
					flag=true;
				}
				else
					sc.next();
			}
			flag=false;
			while(sc.hasNext()&&flag==false)
			{
				if(sc.hasNextInt())
				{
					C = sc.nextInt();
					flag=true;
				}
				else
					sc.next();
			}
			flag=false;
			while(sc.hasNext()&&flag==false)
			{
				if(sc.hasNextInt())
				{
					N = sc.nextInt();
					flag=true;
				}
				else
					sc.next();
			}
			flag=false;
			while(sc.hasNext()&&flag==false)
			{
				if(sc.hasNextInt())
				{
					H = sc.nextInt();
					flag=true;
				}
				else
					sc.next();
			}
			flag=false;
			while(sc.hasNext()&&flag==false)
			{
				if(sc.hasNextDouble())
				{
					P = (double)sc.nextDouble();
					flag=true;
				}
				else
					sc.next();
			}
			flag=false;
			while(sc.hasNext()&&flag==false)
			{
				if(sc.hasNextDouble())
				{
					Q =sc.nextDouble();
					flag=true;
				}
				else
					sc.next();
			}
			flag=false;
			while(sc.hasNext()&&flag==false)
			{
				if(sc.hasNextDouble())
				{
					O = sc.nextDouble();
					flag=true;
				}
				else
					sc.next();
			}
			sc.close();
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}

	}
	public static Table createRegularT(Table oldt, int n, int j)
	{ //������� ������ ���� ����� ������ �� ����� ���� ������� ����
		Table newT = new Table (M, H, n);
		int x = X[N-j-1];
		int y = Y[N-j-1];
		for (int i=0 ; i<newT.getNumOfRows(); i++) { //���� ������ �� �� ������
			newT.addArgu(i, 2, trainVal(i, oldt, 2, x));
			newT.addArgu(i, 3, planeVal(i,oldt,3, y));
			if (ThereIsTeleport(i,newT)) { //���� ��� ������� �� ������ �������� ����� �������
			newT.addArgu(i, 4, teleportAndT(i, newT, oldt));
			newT.addArgu(i, 5, teleportAndP(i,newT, oldt));
			}
			double fmax =getfMax(i, newT); //������ �� f ��������.
			newT.addArgu(i, 6, fmax); 
			newT.addArgu(i, 7, getXmax(i, fmax, newT)); //  ���� �� ������ ���������� ��� ���
		
		}
		return newT; 
	}
	public static boolean ThereIsTeleport(int i,Table newT) { //������� ������ ��� ������ ���� ������ �������� ���� ������.
		int m= M;  //���� ��������
		double [] tempArr = new double [newT.getNumOfRows()]; // ���� ���� ����� �� ��' ��������� �������� ����� ����� �� ������
		for (int j=0; j<newT.getN() && m!=0; j++) { // ������ �� ����� ������ ������
			tempArr[j] = m; //��� ���� �� ��' �������� ������ ����� ���' �����
			m--; //����� ��� �� �� ������ ����� �� M �������� (��� �� �������) ,
					//����� 2 �� �� M �� M-1 ��������.. ��� ����.
			if ( newT.getVal(i, 0) ==  tempArr[j] ) return true; //�� ��' ��������� ����� ������� ������� ����� ������� - ���� ������ ���� ������
		}
		return false; //�� �� , ������ �� �� ������ �������� ���� ���� �� ���' �������� ����� ������ ���� �� ����� �� ������� ����� �������.
	}
	public static double getfMax (int i, Table newT) { //������� ������ �� ���� �������� ��� ���
		double maxF = newT.getVal(i, 2); //��� ���� ������ �� ���� ��������
		for (int j=3 ; j<=5 ;j++) { //����� ������ ����� �� �� �����
			if (maxF< newT.getVal(i, j)) {
				maxF = newT.getVal(i, j); 
			}
		}
		return maxF;
	}
	public static int getXmax(int i, double fmax, Table newT) { //����� �� ������ ���������� ��� ���
								// ����-2 ����-3 ������ �����-4 ������ �����-5
		int index =0;
		for (int j=2 ; j<=5 ;j++) {
			if (fmax == newT.getVal(i, j))  {
				index =j; //����� �� ������ ��������� ������� ������
			break;
			}
		}
		return index;
	}
	public static double trainVal(int i, Table oldT ,int j, int x)
	{//������� ������ �� ������ ���� �� �����
	double expectedVal=Q*oldT.getVal(setIndex(i,x+1,oldT,j), 6)+(1-Q)*oldT.getVal(setIndex(i,x,oldT,j), 6)-70;
		return 	expectedVal;
	}
	public static double planeVal(int i, Table oldT, int j, int y)
	{//������� ������ �� ������ ���� �� �����
	double expectedVal=O*oldT.getVal(setIndex(i,2*y,oldT,j), 6)+(1-O)*oldT.getVal(setIndex(i,y,oldT,j), 6)-120;
		return 	expectedVal;
	}
	public static int setIndex (int i, int val,Table oldT, int j) { //������� ������ �� ������� �� ���� ��������
		if ((oldT.getVal(i, 1) - val)< 0 ) return (int) i-i%(H+2) ; //�� ����� ���� �� ���� ����, ��� ����� �� ����� ������ �� 1-  ����� �� ���' ��������
		else return (i-val); 
	}
	public static double teleportAndT(int i, Table newT, Table oldT)
	{//������� ������ �� ������ ���� �� ����� �� ����� �������
	double costTeleport=Math.pow(1.1, M-newT.getVal(i, 0))*220;
	double expectedVal=(1-P)*(oldT.getVal(i-2-H, 6) )+P*(newT.getVal(i-2-H, 2))-costTeleport;
	return 	expectedVal;
	}
	public static double teleportAndP(int i, Table newT, Table oldT)
	{//������� ������ �� ������ ���� �� ����� �� ����� �������
	double costTeleport=Math.pow(1.1, M-newT.getVal(i, 0))*220;
	double expectedVal=(1-P)*(oldT.getVal(i-2-H, 6) )+P*(newT.getVal(i-2-H, 3))-costTeleport;
	return 	expectedVal;
	}
	public static void creatLastT (Table t) 
	{ //������� ������ �� ���� ��' N+1
		for(int j=2;j<=6;j++){
			for (int i = 0 ; i< t.getNumOfRows() ;i++) {
				if(t.getVal( i, 1) == -1) 
					t.addArgu(i,j, 0); //�� �� ����� ���� - �� ���� ����
				else t.addArgu(i, j, C); //�� ����� ����, ���� �� ����� C		
			}
		}
		for(int k=0;k<t.getNumOfRows(); k++){ // ����� �� X ���� ����� ���� ����� ������� (���� �� ��� ������� �� �� ���� �����) 
			t.addArgu(k, 7, 2); 
		}
	}
	public static void writeTablesToTxt (ArrayList <Table> allT) {
		String fileName = "out.txt";
		try {
			PrintWriter outputStream = new PrintWriter(fileName);
			outputStream.println("The f* is: "+allT.get(allT.size()-1).getVal(allT.get(allT.size()-1).getNumOfRows()-1,6));
			outputStream.println("table 1: represent station number 1");
			outputStream.println("teleports left   "+"hours left  "+"       1.train  "+"           2.plane "
			+"   3.teleporte and train  "+ "     4.teleporte and plane  "+ "    f*  "+"      best choice  ");
			Table t1 = allT.get(allT.size()-1);
			int i = t1.getNumOfRows()-1;
			for (int j=0;j<7;j++) 
				outputStream.print(t1.getVal(i, j)+"             ");	
			outputStream.println(t1.getVal(i,7)-1+"             ");
			Table t2 = allT.get(allT.size()-2);
			ArrayList<Double> tempList = new ArrayList<Double> ();//����� ������ �� ����� ���������� ����� 2
			outputStream.println("table 2: represent station number 2");
			outputStream.println("teleports left   "+"hours left  "+"       1.train  "+"           2.plane "
					+"   3.teleporte and train  "+ "     4.teleporte and plane  "+ "    f*  "+"      best choice  ");
			if (!tempList.contains(H)) tempList.add((double) H);
			if (!tempList.contains(H-X[0])&& (H-X[0]) > -1) tempList.add((double) (H-X[0]));
			else if (!tempList.contains(-1)) tempList.add((double) -1);
			if (!tempList.contains(H-X[0]-1)&& (H-X[0]-1) > -1 ) tempList.add((double) (H-X[0]-1));
			else if (!tempList.contains(-1)) tempList.add((double) -1);
			if (!tempList.contains(H-Y[0])&& (H-Y[0]) > -1) tempList.add((double) (H-Y[0]));
			else if (!tempList.contains(-1)) tempList.add((double) -1);
			if (!tempList.contains(H-2*Y[0])&& (H-2*Y[0]) > -1) tempList.add((double) (H-2*Y[0]));
			else if (!tempList.contains(-1)) tempList.add((double) -1);
			for (int j =0 ; j<(H+2)*2 ; j++) { //���� �� ������ ����������
				int numOfRow = (2+H)*(M+1)-2*(H+2)+j;
				if (tempList.contains(t2.getVal(numOfRow, 1))) { //�� �����  �� ���������, ����� ����
					for (int r=0;r<7;r++) {
						outputStream.print(t2.getVal(numOfRow, r)+"             ");	
					}
					outputStream.println(t2.getVal(numOfRow,7)-1+"             ");
					
					}
					
			}
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}

