
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

public class MathExam {
	static Scanner input = new Scanner(System.in);
	static String filename ="out.txt";	
	static File file = new File(filename),parent = null;
	static OutputStream out = null;
	static String[] str = {"+","-","x","÷"},arg= {" "," "};
	static List<String> Calculation_Problem = new ArrayList<String>();
	static int cal_number1 = 0, cal_number2 = 0,symbol = 0, sum = 0, remainder = 0, 
			number = -1, grade = -1, 
			calculation_num = 0 ,ran_symbol_num = 0,ran_left_parenthesis = 0;			
	static Random ranNum = new Random(),ranStr = new Random(),ran_symbolnum= new Random(),ran_yes_no = new Random();
	static String word = null,check_message = null,cal_pro = null;
	static Pattern pattern = Pattern.compile("[0-9]*");
	public static void main(String[] args) {
			File_Initialization(file);//文档初始化生成
			if(args[0].equals("-n") && args[2].equals("-grade"))
			{	arg[0] = args[1];arg[1] = args[3];}
			else if(args[0].equals("-grade") && args[2].equals("-n"))
			{	arg[0] = args[3];arg[1] = args[1];}
			else
				System.out.print("输入有误！！！");
			Input_Message();//输入信息检测
			//生成合格的题目
			for (int i = 1; i <= number; i++) 
			{
				Iteration(i);//生成合适的加减乘除符号
				File_Write_Problem(i);//将题目写入文档
			}
			if(number!=0)
				File_Write_Answer();//将答案写入文档
	}
	
	/**
		小学三年级四则混合运算的要求如下：
		运算符在2～4个
		可以加括号
		减法运算的结果不能有负数--还需要在逆波兰计算时仍有判断
		除法运算除数不能为0，不能有余数
		数字在0-1000以内，含端点
		当然，为一年级、二年级出题的功能还是要保留
		
		经过查询，三年级混合运算结果还不能是小数
	 * 
	 * **/
	private static void Iteration(int i) {
		cal_number1 = ranNum.nextInt(1001);
		cal_number2 = ranNum.nextInt(1001);
		symbol = ranStr.nextInt(4);
		if(grade==1 && symbol<=1)
		{
			if(str[symbol].equals("+") && cal_number1 + cal_number2 <= 100)
				sum = cal_number1 + cal_number2;
			else if(str[symbol].equals("-") && cal_number1 - cal_number2 >= 0)
				sum = cal_number1 - cal_number2;
			else
				Iteration(i);
			word = "("+Integer.toString(i)+") "+Integer.toString(cal_number1)+" "+str[symbol]+" "+Integer.toString(cal_number2);
			return;
		}	
		else if(grade==2)
		{	
			if(str[symbol].equals("+") && cal_number1 + cal_number2 <= 100)
				sum = cal_number1 + cal_number2;
			else if(str[symbol].equals("-")&& cal_number1 - cal_number2 >= 0)
				sum = cal_number1 - cal_number2;
			else if(str[symbol].equals("x") && cal_number1 * cal_number2 <= 100)
				sum = cal_number1 * cal_number2;
			else if(str[symbol].equals("÷") && cal_number2 != 0)
			{	
				sum = cal_number1 / cal_number2;
				remainder = cal_number1 % cal_number2;
			}
			else
				Iteration(i);
			word = "("+Integer.toString(i)+") "+Integer.toString(cal_number1)+" "+str[symbol]+" "+Integer.toString(cal_number2);
			return;
		}
		else if(grade==3)
		{			
			ran_symbol_num = ran_symbolnum.nextInt(3)+2;
			word = Integer.toString(cal_number1);
			int k = -1;
			for(int j=1;j<=ran_symbol_num;j++)
			{
				cal_number2 = cal_number1;
				cal_number1 = ranNum.nextInt(1001);
				symbol = ranStr.nextInt(4);
				ran_left_parenthesis = ran_yes_no.nextInt(2);
				if(str[symbol].equals("+") && cal_number2 + cal_number1 > 1000
				|| str[symbol].equals("-") && cal_number2 - cal_number1 < 0
				|| str[symbol].equals("x") && cal_number2 * cal_number1 > 1000
				|| str[symbol].equals("÷") && j!=1 && cal_number1==0)
				{	
					j--;
					continue;
				}
				else if(ran_left_parenthesis % 2 == 1 && j <= ran_symbol_num-2)
				{	
					if(j == 1)
					{
						word = "(" + word;
						symbol = ranStr.nextInt(2);
						cal_number1 = ranNum.nextInt(1001);
						word = word + str[symbol] + Integer.toString(cal_number1)+")";
						symbol = ranStr.nextInt(2) + 2;
						cal_number1 = ranNum.nextInt(1001);
						word = word+str[symbol]+Integer.toString(cal_number1);
						j++;
					}
					else if(j <= ran_symbol_num-2)
					{
						word = word + str[symbol] + "("+Integer.toString(cal_number1);
						if(symbol == 0 || symbol == 1)
							symbol = ranStr.nextInt(2);
						else
							symbol = ranStr.nextInt(4);
						cal_number1 = ranNum.nextInt(1001);
						word = word + str[symbol] + Integer.toString(cal_number1)+")";
					}
					continue;
				}
				word = word+str[symbol]+Integer.toString(cal_number1);
			}
			Calculation calculation = new Calculation(word);
			calculation.infix_expression();
			calculation.cal();
			if(calculation.suffix_expression_summation())
			{				
				word = "("+i+") "+calculation.getword();
				sum = calculation.getSum();
				return;
			}
			else
				Iteration(i);
		}
		else
			Iteration(i);
	}

	private static void Input_Message() {
		int j=1;
		check_message = arg[0];//题数
		if(!pattern.matcher(check_message).matches())
		{	
			System.out.print("输入的题数不合法！请重新输入题数：");
			check_message = input.nextLine();
		}
		else
		{
			number = Integer.parseInt(check_message);
			if(number < 0)
				System.out.print("输入的题数不合法！请重新输入题数：");
		}
		j++;
		while(number<0)
		{
			if(j!=1)
				check_message = input.nextLine();
			if(!pattern.matcher(check_message).matches())
			{	
				System.out.print("输入的题数不合法！请重新输入题数：");
				continue;
			}
			else
			{
				number = Integer.parseInt(check_message);
				if(number < 0)
					System.out.print("输入的题数不合法！请重新输入题数：");	
			}	
		}
		j=1;
		check_message =arg[1];//年级
		if(!pattern.matcher(check_message).matches())
		{	
			System.out.print("输入的年级不合法！请重新输入年级：");
			check_message = input.nextLine();
		}	
		else
		{
			grade = Integer.parseInt(check_message);
			if(grade<1 || grade>3)
				System.out.print("输入的年级不合法!请重新输入年级(1或2或3)：");			
		}
		j++;
		while(grade<1 || grade>3)
		{
			if(j!=1)
				check_message = input.nextLine();
			if(!pattern.matcher(check_message).matches())
			{
				System.out.print("输入的年级不合法!请重新输入年级(1或2或3)：");
				continue;
			}
			else
			{
				grade = Integer.parseInt(check_message);
				if(grade<1 || grade>3)
					System.out.print("输入的年级不合法!请重新输入年级(1或2或3)：");
			}
			
		}
	}
	
	private static void File_Write_Answer() {
		try {
			out.write("\r\n".getBytes());
			for (int i = 0; i < calculation_num; i++)
			{
				out.write(Calculation_Problem.get(i).getBytes());
			}
		} 
		catch (IOException e) {
			System.out.println("程序在写入计算题答案时异常："+e.getMessage());
		}
	}

	private static void File_Write_Problem(int i) {
		try {
			
			out.write((word+"\r\n").getBytes());
			if(remainder!=0)
			{
				Calculation_Problem.add(word+" = "+Integer.toString(sum)+"..."+Integer.toString(remainder)+"\r\n");
				calculation_num++;
				remainder = 0;
			}
			else
			{	
				Calculation_Problem.add(word+" = "+Integer.toString(sum)+"\r\n");
				calculation_num++;
			}
		} 
		catch (IOException e) 
		{
			System.out.println("程序在out.write()时抛出异常"+e.getMessage());
		}
	}

	private static void File_Initialization(File file) {
		if(!file.exists()) 
		{		parent = file.getParentFile();
				if(parent !=null && !parent.exists())
				{	parent.mkdir();//创建目录
					System.out.println("创建目录："+parent);
				}
				try {
					file.createNewFile();
				} catch (IOException e) {
					System.out.println("程序在file.createNewFile()时抛出异常"+e.getMessage());
				}
				System.out.println("创建新文件："+file.getAbsolutePath());
		}//获取绝对路径
		try {
			out = new FileOutputStream(file);
		} 
		catch (FileNotFoundException e) {
			System.out.println("程序在new FileOutputStream(file)时抛出异常"+e.getMessage());
		}
	}

}
