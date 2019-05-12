public class Table {
	double [] [] table ;
	int N ;
	public Table (int M, int H, int N) { //בנאי
		this.N = N;
		table = new double [(H+2)*(M+1)] [8]; 
		int index=0; // number of row
		for (int i=0 ;i <= M ; i++){ //מכניס לטבלה את מס' השעות שנשארו כשהגענו לתחנה 
			int hour =-1; 
			for (int j=0; j< H+2 ; j++){
				table [index][0] = i;
				table [index] [1] = hour;
				hour++; //מוסיף שעה
				index++; //יורד שורה
			}
		}
		double inf = Double.NEGATIVE_INFINITY; //מאתחל את התוחלות בטבלה להיות -אינסוף , כך נדע שיש אופציות לא רלוונטיות ולא נבחר בהן כאופטימליות.
		for (int i=0;i<table.length;i++)
			for (int j=2;j<=5;j++)
				table[i] [j] =inf;
	}
	public void print() { //שיטה שמדפיסה את הטבלה
		for(int i=0;i<table.length;i++)
		{
			System.out.println();
			for (int j=0;j<table[i].length;j++)
				System.out.print("[ "+ table[i][j] +"] " );
		}
	}
	public void addArgu(int i,int j,double val)
	{//שיטה שמקבלת מיקום וערך ומבציבה במקום המתאים בטבלה
		val = Math.round(val*100)/100.000;
		table[i][j]=val;
	}
	public double getVal(int i,int j)
	{  //שיטה שמחזירה את הערך של המשבצת בטבלה
		double t = table[i][j] ;
		return t;
	}
	public int getNumOfRows() { //שיטה שמחזירה את מס' השורות בטבלה
		return table.length;
	}
	public int getN () { //שיטה שמחזירה את השלב- מס' התחנה של הטבלה
		return N;
	}
	public void printLine(int i)
	{//שיטה שמדפיסה את השורה במקום הI
		for (int j=0;j<table[i].length-1;j++)
			System.out.print(table[i][j]+"             ");	
		System.out.println(table[i][7]-1+"             ");
	}

}

