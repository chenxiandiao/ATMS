package util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import model.TableData;

public class Expression {

	private static  List<String>tokenList = null;
	private static  Stack<String>chStack = null;
	
	public static String toPostfix(String expstr){
		tokenList = new ArrayList<String>();
		chStack = new Stack<String>();
		String postfix = "";
		int i = 0;
		int len = expstr.length();
		while(i<len)
		{
			char ch = expstr.charAt(i);
			String token = "";
			switch(ch)
			{
				case '+':
				case '-':
					while(chStack.isEmpty()&&chStack.firstElement()!="(")
					{
						postfix +=chStack.pop();
						postfix +=" ";
					}
					chStack.push(""+ch);
					i++;break;
				case '*':
				case '/':
					while(chStack.isEmpty()&&(chStack.firstElement()=="*"
					||chStack.firstElement()=="/"))
					{
						postfix +=chStack.pop();
						postfix +=" ";
					}
					chStack.push(""+ch);
					i++; break;
				case '(':
					chStack.push(""+ch);
					i++;break;
				case ')':
					String out = chStack.pop();
					while(out!=null&&out!="(")
					{
						postfix += out;
						postfix += " ";
						out = chStack.pop();
					}
					i++;
					break;
				default:
					 while(ch!='+'&&ch!='-'&&ch!='*'&&ch!='/'&&ch!='('&&ch!=')')
					 {
						 token +=ch;
						 postfix +=ch;
						 i++;
						 if(i<len)
						 {
							 ch = expstr.charAt(i);
						 }
						 else
						 {
							 break;
						 }
					 }
					 tokenList.add(token);
					 postfix +=" ";
			}
		}
		int tmp = 0;
		while(!chStack.isEmpty())
		{
			if(tmp!=0)
			{
				postfix +=" ";
			}
			tmp++;
			postfix +=chStack.pop();
		}
		return postfix;
	}
	
	public static String formulaValue(List<TableData>tableDataList,String postfix)
	{
		try
		{
			if(postfix==null||postfix.length()==0)
			{
				return "0";
			}
			chStack = new Stack<String>();
			String[]token = postfix.split(" ");
			int i = 0;
			double result = 0;
			int len = token.length;
			while(i<len)
			{
				String ch = token[i];
				if(ch!="+"&&ch!="-"&&ch!="*"&&ch!="/")
				{
					chStack.push(ch);
					i++;
				}
				else
				{
					double y = 0;
					double x = 0;
					String yTmp = chStack.pop();
					String xTmp = chStack.pop();
					String tmp = "";
					if(yTmp.indexOf("C")!=-1)
					{
						tmp = tableDataList.get(Integer.parseInt(yTmp.substring(1))).getValue();
						if(tmp==null)
						{
							return "0";
						}
						if(tmp.indexOf("¥")!=-1)
						{
							tmp = tmp.substring(1);
						}
						y =  Double.parseDouble(tmp);
					}
					else
					{
						y =  Double.parseDouble(yTmp);
					}
					if(xTmp.indexOf("C")!=-1)
					{
						tmp = tableDataList.get(Integer.parseInt(yTmp.substring(1))).getValue();
						if(tmp==null)
						{
							return "0";
						}
						if(tmp.indexOf("¥")!=-1)
						{
							tmp = tmp.substring(1);
						}
						x =  Double.parseDouble(tmp);
					}
					else 
					{
						x =  Double.parseDouble(xTmp);
					}
					switch(ch)
					{
						case "+":result = x + y;break;
						case "-":result = x - y;break;
						case "*":result = x * y;break;
						case "/":result = x / y;break;
					}
					chStack.push(String.valueOf(result));
					i++;
				}
			}
		}catch(Exception e)
		{
			System.out.println("error");
		}
		DecimalFormat df = new DecimalFormat("#.00");
		String result_value = String.valueOf(df.format(Double.parseDouble(chStack.pop())));
		return result_value;
	}
	
	
}
