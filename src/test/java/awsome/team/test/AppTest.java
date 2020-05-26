package awsome.team.test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class AppTest{
	
	private String rwtab[]= {"function", "if", "then", "while", "do", "endfunc"};//保留字改end-func为endfunc
	private int syn, sum;//ex3:syn符号编码
	//ex3:将各单词符合种别码统一
	private void myScaner(char[] prog, char[] token){
		char ch;
		int line, p, m; //sum数字真值，p位置号 //ex3:新增line变量记录程序行数
		
		//for(int n = 0;n < 8; n++) token[n] = null;
		p = 0;
		ch = prog[++p];
		
		//删除了 while(ch !="#")ch = prog[++p];

		if(ch >= 'a' && ch <= 'z'){
			m=0;
			while (ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9'){
				if (m<8) {
					token[m++] = ch;
					ch = prog[++p];
				  }  
			}
			token[m] = '\0'; //设置字符串结束符
			ch = prog[--p]; //ch回退到不满足小写字符的字符上
			syn = 10; //syn=10时，分词结果为小写的字符串(后面可以跟数字)且不为保留字
			for(int n = 0;n < 6; n++)
				if (rwtab[n].equals(token)){ //token和保留字相等时，syn设置编号为1到6，分词结果为保留字
					syn = n + 1; //数组下标从0开始，+1后与自然概念对齐
					break;
				}
		}else if(ch >= '0'&& ch <='9'){
			sum = 0;
			while (ch >= '0' && ch <= '9'){
				sum = sum * 10 + ch - '0'; //ch - ‘0’ 就是把ch从char转成int，整体效果就是把一溜数字字符串转成实际int
				ch = prog[++p];
			}
			ch = prog[--p]; //ch回退到不满足数字的字符上
			syn = 11; //syn为11时，分词结果为数字串
		}else 
			switch (ch){
				case'<': 
					m = 0; 
					token[m++]= ch; //从零开始存
					ch = prog[++p]; //ch为不满足数字的字符之后一位
					if(ch == '='){
						syn = 22; //syn=22，说明是在<=判断运算
						token[m+1] = ch;
					}else{
						syn = 20; //syn=20，<判断运算，前提是程序没出错
						ch = prog[--p];
					}
				break;
				case'>': 
					m = 0;
					token[m++] = ch;
					ch = prog[++p];
					if(ch == '='){
						syn = 24; //syn=24，说明是在>=判断运算
						token[m++] = ch;
					}else{
						syn = 23; //syn=23，说明是在>判断运算
						ch= prog[--p];
					}
				break;
				case'=':
					m = 0;
					token[m++] = ch;
					ch = prog[++p];
					if(ch == '='){
						syn = 25; //syn=25，说明是在==判断运算
						token[m++] = ch;
					}else{
						syn = 18; //syn=18，说明是在=赋值运算
						ch = prog[--p];
					}
				break;
				case'!': 
					m = 0;
					token[m++] = ch;
					ch = prog[++p];
					if(ch == '='){
						syn= 21; //这里!=状态码与<=冲突了，改成syn=21，说明是在!=判断运算
						token[m++] = ch;
					}else
						syn = -1; //syn=-1，说明格式错了
				break;
				case'/': 
					m = 0;
					token[m++] = ch;
					ch = prog[++p];
					if(ch == '*'){
						syn= 30; //syn=30, /*注释
						token[m++] = ch;
					}else
						if(ch == '/'){
						syn= 31; //syn=31, //注释
						token[m++] = ch;
					}else{
						syn = 16; 
						ch = prog[--p];//syn=16，除法运算
					}
				break; 
				case'*':
					m = 0;
					token[m++] = ch;
					ch = prog[++p];
					if(ch == '/'){
						syn= 32; //syn=32, */注释
						token[m++] = ch;
					}else{
						syn = 15;
						ch = prog[--p]; //syn=15，乘法运算
					}
				break;
				case'\n': 
					syn = 7; 
					token[0] = '0'; 
					break; //syn=7，换行符 
				case'+': syn = 13; token[0] = ch; break; //syn=13，加法运算
				case'-': syn = 14; token[0] = ch; break; //syn=14，减法运算
				case';': syn = 26; token[0] = ch; break; //syn=26，分号
				case'(': syn = 27; token[0] = ch; break; //syn=27，左括号
				case')': syn = 28; token[0] = ch; break; //syn=28，右括号
				case'#': syn = 0;  token[0] = ch; break; //syn=0，结束符
				case' ': syn = 8;  token[0] = ch; break; //syn=8，空格符
				case'	':syn = 9; token[0] = ch; break; //syn=9,制表符
				default: syn = -1;
			}
	}
	
	/**
     * 读取txt文件的内容
     * @param filePath
     */
    private String readTxtFile(String filePath){
    	String result = "";
        try {
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(new FileInputStream(file));
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
                    	result += lineTxt;
                    }
                    read.close();
        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return result;
     
    }
	
	public static void main(String[] args) {
		int p = 0;
		char[] token = new char [8]; //java数组的初始化方式
		AppTest at = new AppTest();
		//System.out.println("\n plz input your code :\n");
		
		//读取文件程序
		String temp = at.readTxtFile("/home/mig-chen/文档/编译原理/CKJ_ex3(20200516)/test2.txt");
		System.out.println(temp);
		char[] prog = temp.toCharArray();
		
		p = 0;
		do{
			at.myScaner(prog,token);
			switch(at.syn){ 
				case 11: System.out.println("\n ("+at.syn+","+at.sum+")") ;break;
				case -1: System.out.println("\n error") ;break;
				default: System.out.println("\n ("+at.syn+","+token+")");
			}
		}while(at.syn != 0);
	}
}
