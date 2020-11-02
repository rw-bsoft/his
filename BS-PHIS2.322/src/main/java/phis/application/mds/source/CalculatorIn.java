package phis.application.mds.source;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class CalculatorIn {
	private Stack<Double> operandStack=new Stack<Double>();//操作数堆栈  
    private Stack<String> operatorStack=new Stack<String>();//操作符堆栈  
    private String expression;//算数表达式  
    private double result=0.0;//计算结果  
    private Map<String,Integer> priorityMap=new HashMap<String,Integer>();//用于存储操作符优先级的Map  
    //初始化优先级约定(可根据计算的复杂程度扩展)  
    public CalculatorIn()  
    {  
        priorityMap.put("+",0);  
        priorityMap.put("-",0);  
        priorityMap.put("*", 1);  
        priorityMap.put("/", 1);  
         
    }  
     
    public int getPriority(String op)//得到一个操作符的优先级  
    {  
        return priorityMap.get(op);  
    }  
     
    public boolean highPriority(String op)//判断操作符的优先级在堆栈里面是否最为高  
    {  
        int opPri=getPriority(op);//当前操作符的优先级  
        if(!operatorStack.empty())  
        {  
        for(String s:operatorStack)  
        {  
            int priority=getPriority(s);  
            if(opPri<priority)  
                return false;  
             
        }  
        }  
        return true;  
    }  
    //把表达式转化成逆波兰式  
    public String expToIpn()  
    {  
        int index=0;  
        int end=0;  
        String Ipn="";  
        for(int i=0;i<expression.length();i++)  
        {  
            String temps=String.valueOf(expression.charAt(i));  
            if(temps.matches("[0-9.]"))//检查是否是数字  
            {  
                end++;  
            }  
            else  
            {  
                String tempOperand=expression.substring(index,end);//得到操作数  
                Ipn+=tempOperand+",";  
                String tempOperator=expression.substring(end,++end);//得到操作符  
                if(tempOperator.equals("!"))//假如到表达式的最后将操作符 全部弹出  
                    {  
                    while(!operatorStack.empty())  
                    {  
                         Ipn+=operatorStack.pop()+",";  
                     }  
                    }  
                 else  
                 {  
                if(highPriority(tempOperator))//优先级高的压入操作符堆栈  
                    {  
                    operatorStack.push(tempOperator);  
                    }  
                 else  
                    {  
                     while(!operatorStack.empty())//  
                    {  
                         Ipn+=operatorStack.pop()+",";  
                     }  
                      
                     operatorStack.push(tempOperator);  
                    }  
                //System.out.println(tempOperand+","+tempOperator);  
                index=end;  
            }  
            }  
             
        }  
        return Ipn;  
     
    }  
    public double calculateIpn(String[] Ipn)//计算逆波兰式  
    {  
           
        for(int i=0;i<Ipn.length;i++)  
        {  
        //    System.out.println(Ipn[i]);  
            if(Ipn[i].matches("^[0-9]+.?[0-9]*$"))//正则表达式判断是数字  
            {  
                operandStack.push(Double.parseDouble(Ipn[i]));  
            }  
                else  
                {  
                    popOperand(Ipn[i]);  
                }  
        }  
        return result;  
         
    }  
    //遇到操作符时，弹出操作数，进行相应操作，并保村result  
    public void popOperand(String operator)  
    {  
        double d1=operandStack.pop();  
        double d2=operandStack.pop();  
        System.out.println(d1+operator+d2);  
        if(operator.equals("+"))  
            result=d2+d1;  
        if(operator.equals("-"))  
            result=d2-d1;  
        if(operator.equals("*"))  
            result=d2*d1;  
        if(operator.equals("/"))  
            result=d2/d1;  
//System.out.println(result);  
            operandStack.push(result);  
         
    }  
  
    @SuppressWarnings("rawtypes")
	public Stack getOperandStack() {  
        return operandStack;  
    }  
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void setOperandStack(Stack operandStack) {  
        this.operandStack = operandStack;  
    }  
    @SuppressWarnings("rawtypes")
	public Stack getOperatorStack() {  
        return operatorStack;  
    }  
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void setOperatorStack(Stack operatorStack) {  
        this.operatorStack = operatorStack;  
    }  
    public String getexpression_r() {  
        return expression;  
    }  
    public void setexpression_r(String expression) {  
        this.expression = expression;  
    }  
    public double getResult() {  
        return result;  
    }  
    public void setResult(double result) {  
        this.result = result;  
    }  
	/**
	 * @author caijy
	 * @createDate 2014-11-4
	 * @description 
	 * @updateInfo
	 * @param args
	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		CalculatorIn cal=new CalculatorIn();  
//        String exp="32+1!";  
//        cal.setexpression_r(exp);  
//        String[] Ipn=cal.expToIpn().split(",");  
//         for(int i=0;i<Ipn.length;i++)  
//        System.out.println(Ipn[i]);  
//        System.out.println(cal.calculateIpn(Ipn));  
//	}
	
	public double js(String exp){
		CalculatorIn cal=new CalculatorIn();  
		 cal.setexpression_r(exp+"!");  
		 String[] Ipn=cal.expToIpn().split(","); 
		 return cal.calculateIpn(Ipn);
	}
}
