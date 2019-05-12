abstract class EmployWithSalaryAndName extends Employ{ //מחלקה שיורשים אותה עובדים עם שם ומשכורת
protected double salary;
protected String name;
public double getSalary() { 
	return salary;
}
protected void addSalary (double num) {
	salary+=num;
}
}
