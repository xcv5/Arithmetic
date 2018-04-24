package encode;

/**
 * 
 * 数据流对象
 * 用于编码中的实际操作对象
 * 其组成为实际的数值取小数部分并转换为二进制格式
 * @author 徐嘉兴
 *
 */
public class DataStream {
    private String stream;
    
    public String getStream() {
    	return this.stream;
    }
    
    public void setStream(String stream) {
    	this.stream=stream;
    }
    
    /**
     * 
     * @return 镜像实例
     */
    public DataStream copyOf() {
    	DataStream copy=new DataStream();
    	copy.setStream(this.stream);
    	return copy;
    }
    
    /**
     * 自定义加法
     * @param st
     * @return 实例引用
     */
    public DataStream add(String st) {
    	int carry=0;
    	int len=st.length()-1;
    	int thislen=stream.length()-1;
    	int min=(len>thislen)?thislen:len;
    	StringBuilder res=new StringBuilder((len>thislen)?st:stream);
    	for(;min>=0;min--) {
    		int thischa=stream.charAt(min)=='0'?0:1;
    		int cha=st.charAt(min)=='0'?0:1;
    		if((cha+thischa+carry)%2==1)
    			res.setCharAt(min, '1');
    		else
    			res.setCharAt(min, '0');
    		if((cha+thischa+carry)>1)
    			carry=1;
    		else
    			carry=0;
    	}
    	if(carry==1) {
    		System.err.println("加法或乘法计算错误，数字溢出！！");
    	}
    	this.stream=res.toString();
    	return this;
    }
    
    /**
     * 自定义减法
     * @param st
     * @return 实例引用
     */
    public DataStream sub(String st) {
    	int bor=0;
    	int len=st.length()-1;
    	int thislen=stream.length()-1;
    	StringBuilder res=new StringBuilder(stream);
    	if(thislen<len) {
    		for(;thislen<len;thislen++)
    		    res.append('0');
    	}
    	for(;len>=0;len--) {
    		int thischa=res.charAt(len)=='0'?0:1;
    		int cha=st.charAt(len)=='0'?0:1;
    		int theSub=thischa-cha-bor;
    		if(theSub==0||theSub==-2)
    			res.setCharAt(len, '0');
    		else
    			res.setCharAt(len, '1');
    		if(theSub<0)
    			bor=1;
    		else
    			bor=0;
    	}
    	if(bor==1)
    		System.err.println("除法错误，减法结果为负！！");
    	this.stream=res.toString();
    	return this;
    }
    
    /**
     * 内部静态方法
     * 完成除法操作
     * @param res
     * @param st2
     */
    private static void sub(StringBuilder res,String st2) {
    	int bor=0;
    	for(int i=st2.length()-1;i>=0;i--) {
    		int cha=res.charAt(i+1)=='1'?1:0;
    		int cha2=st2.charAt(i)=='1'?1:0;
    		int theSub=cha-cha2-bor;
    		if(theSub==0||theSub==-2)
    			res.setCharAt(i+1, '0');
    		else
    			res.setCharAt(i+1, '1');
    		if(theSub<0)
    			bor=1;
    		else
    			bor=0;
    	}
    	if(bor==1)
    		res.setCharAt(0, '0');
    }
    
    /**
     * 自定义乘法
     * @param ds
     * @return 实例引用
     */
    public DataStream mul(String ds) {
   	 StringBuilder st=new StringBuilder(stream);
   	 StringBuilder zero=new StringBuilder();
   	 for(int i=0;i<stream.length();i++)
   		 zero.append('0');
   	 this.stream="";
   	 for(int i=0;i<ds.length();i++) {
   		 st.insert(0, '0');
   		 zero.append('0');
   		 if(ds.charAt(i)=='1')
   			 this.add(st.toString());
   		 else
   			 this.add(zero.toString());
   	 }
   	 return this;
   }
    
    /**
     * 内部方法
     * 完成乘法功能
     * @param str1
     * @param str2
     * @return
     */
    private static int comparaTo(String str1,String str2) {
    	if(str1.charAt(0)=='1')
    		return 1;
    	int l2=str2.length();
    	for(int i=0;i<l2;i++) {
    		if(str1.charAt(i+1)=='1'&&str2.charAt(i)=='0')
    			return 1;
    		else if(str1.charAt(i+1)=='0'&&str2.charAt(i)=='1')
    			return -1;
    	}
    	return 0;
    }
    
    /**
     * 比较函数
     * 比较两个字节流大小
     * @param range
     * @return 1：大于 0：等于 -1：小于
     */
    public int comparaTo(String range) {
    	String copy=stream;
    	int cLen=copy.length(),rLen=range.length();
    	if(cLen>rLen) {
    		while(cLen>rLen) {
    		    range+='0';
    		    rLen++;
    		}
    	}
    	else if(rLen>cLen) {
    		while(rLen>cLen){
    		    copy+='0';
    		    cLen++;
    		}
    	}
    	for(int i=0;i<rLen;i++) {
    		if(copy.charAt(i)=='1'&&range.charAt(i)=='0')
    			return 1;
    		else if(copy.charAt(i)=='0'&&range.charAt(i)=='1')
    			return -1;
    	}
    	return 0;
    }
    
    /**
     * 自定义除法
     * @param ds
     * @return 实例引用
     */
    public DataStream div(String ds) {
    	StringBuilder sb=new StringBuilder("0");
    	sb.append(stream);
    	StringBuilder ans=new StringBuilder();
    	int iter=0;
    	while(iter<64) {
    		if(comparaTo(sb.toString(),ds)>=0) {
    			ans.append('1');
    			sub(sb,ds);
    		}
    		else
    			ans.append('0');
    		sb.deleteCharAt(0);
    		sb.append('0');
    		iter++;
    	}
    	this.stream=ans.substring(1);
    	return this;
    }
    
    /**
     * 编码框压缩方法
     * 通过去精度来获取减小运算量的目的
     * @return 实例引用
     */
    public DataStream resetLow() {
    	if(stream.length()<=64)
    		return this;
    	StringBuilder sb=new StringBuilder(stream);
    	for(int i=63;i>=0;i--) {
    		if(sb.charAt(i)=='0') {
    			sb.setCharAt(i, '1');
    			break;
    		}
    		else {
    			sb.setCharAt(i, '0');
    		}
    	}
    	if(sb.length()>64) {
    		stream=sb.substring(0, 64).toString();
    	}
    	return this;
    }
    
    /**
     * 编码框压缩方法
     * @return 实例引用
     */
    public DataStream resetHigh() {
    	if(stream.length()<=64)
    		return this;
    	StringBuilder sb=new StringBuilder(stream);
    	for(int i=63;i>=0;i--) {
    		if(sb.charAt(i)=='1') {
    			sb.setCharAt(i, '0');
    			break;
    		}
    		else {
    			sb.setCharAt(i, '1');
    		}
    	}
    	if(sb.length()>64) {
    		stream=sb.substring(0, 64).toString();
    	}
    	return this;
    }
    
    /**
     * 去冗余方法
     * 通过输出既定压缩数据段
     * 减少重复运算
     * @param num
     * @return 当前冗余长度
     */
    public int del(String num) {
    	int i=0;
    	for(;i<stream.length();i++) {
    		if(stream.charAt(i)!=num.charAt(i)) 
    			break;
    	}
    	stream=stream.substring(i);
    	return i;
    }
    
    /**
     * 滑动窗输出方法
     * 输出已编码数据
     * @param len
     * @return 已编码数据
     */
    public String out(int len) {
    	String out=stream.substring(0,len);
    	stream=stream.substring(len);
    	return out;
    }
    
    /**
     * 滑动窗输入方法
     * @param str
     */
    public void in(String str) {
    	this.stream+=str;
    }
    
}
