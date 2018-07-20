package encode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 压缩编码算法
 * 其核心思想在于不断的压缩区间，并将其表现在区间分割的high与low中
 * 根据熵编码的思想
 * 其能达到的区间长度为最长
 * 表示该区间的编码长度也越小
 * 达到压缩的目的
 * 
 * @author 徐嘉兴
 *
 */
public final class Encode {
    private String bufferStr;
    private Map<Character,Interval> charMap=new HashMap<>();
    
    /**
     * 统计源文件概率分布
     * @param filePath
     */
    private void count(String filePath) {
    	int num=0;
    	File file=new File(filePath);
    	if(!file.exists())
    		return;
    	DataInputStream br=null;
    	Map<Character,Integer> map=new HashMap<>();
    	int count=0;
    	try {
    		br=new DataInputStream(new FileInputStream(file));
    		while(br.available()>0) {
        		char c=(char)br.readByte();
        		if(map.containsKey(c)) {
        			int cou=map.get(c);
        			map.put(c, cou+1);
        		}
        		else
        			map.put(c, 1);
        		count++;
    		}
    	}catch(IOException ioe) {
    		System.err.println("IOERROR");
    	}finally {
    		if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    	}
    	//终止符设置
    	if(map.containsKey('$')) {
    		int cou=map.get('$');
    		count+=cou;
    		map.put('$', cou*2+1);
    	}
    	else 
    		map.put('$', 1);
    	count++;
    	if(map.containsKey('e')) {
    		int cou=map.get('e');
    		map.put('e', cou+1);
    	}
    	else
    		map.put('e', 1);
    	count++;
    	double left=0.0;
    	for(Entry<Character, Integer> en:map.entrySet()) {
    		num++;
    		char c=en.getKey();
    		double right=left+(double)en.getValue()/(double)count;
    		Interval in=new Interval(left, right);
    		charMap.put(c,in);
    		left=right;
    	}
    	System.out.println(num);
    }
    
    /**
     * 压缩算法
     * 
     * @param filePath 源文件路径
     * @param outFilePath 目的文件路径
     * @return
     */
    public boolean encode(String filePath,String outFilePath) {
    	int bytenum=0;
    	File file=new File(filePath);
    	File outFile=new File(outFilePath);
    	if(!file.exists())
    		return false;
    	count(filePath);
    	for(Entry<Character, Interval> en:charMap.entrySet()) {
    		if(en.getValue().getLeft().equals(en.getValue().getRight()))
    			System.out.println(en.getKey());
    		//System.out.println(en.getKey()+" "+en.getValue().getLeft()+" "+en.getValue().getRight());
    	}
    	DataInputStream dis=null;
    	DataOutputStream dos=null;
    	
    	String border;  //构造码本，保存各字符右边界
    	
    	DataStream low=new DataStream();
    	DataStream high=new DataStream();
    	DataStream range=null;
    	bufferStr="";   //输出缓存

    	try {
    		dis=new DataInputStream(new FileInputStream(file));
    		if(!outFile.exists())
    			outFile.createNewFile();
    		if(dis.available()<=0)
    			return true;
    		dos=new DataOutputStream(new FileOutputStream(outFile));
    		
    		//输出码本
    		for(Entry<Character, Interval> en:charMap.entrySet()) {
    			dos.writeChar(en.getKey());
    			border=en.getValue().getRight();
    			dos.writeByte(strToByte(border.substring(0, 8)));
    			dos.writeByte(strToByte(border.substring(8)));
    			//bytenum+=4;
    		}
    		dos.writeChar('$');//码本结束标识
    		//bytenum+=2;
    		
    		char firstChar=(char)dis.readByte();
    		boolean tran=(firstChar=='$')?true:false; //转义控制
    		int end=2;  //结束表示计数
    		
    		//首次处理
    		low.setStream(charMap.get(firstChar).getLeft());
    		high.setStream(charMap.get(firstChar).getRight());
    		range=high.copyOf().sub(low.getStream());
    		
    		while(dis.available()>=0) {
    			if(end==0)
    				break;
    			char c;
    			if(tran) {
    				c='$';          //对结束字符进行转义 $->$$
    				tran=false;
    			}
    			else {
    				if(dis.available()>0) {
    				    c=(char)dis.readByte();
    				    if(c=='$')
    					    tran=true;
    				}
    				else {
    					if(end==2)    //结束表示 ->$e
    						c='$';
    					else
    						c='e';
    					end--;
    				}
    					
    			}
    			
    			//算术编码计算
    			high=low.copyOf().add(range.copyOf().mul(charMap.get(c).getRight()).getStream());
    			low.add(range.copyOf().mul(charMap.get(c).getLeft()).getStream());
    			
    			//滑动窗输出与调整
    			int delLen=high.del(low.getStream());
    			bufferStr+=low.out(delLen);
    			low.resetLow();
    			high.resetHigh();
    			range=high.copyOf().sub(low.getStream());
    			
    			//文件输出
    			while(bufferStr.length()>=8) {
    				dos.writeByte(strToByte(bufferStr.substring(0,8)));
    				bytenum++;
    				bufferStr=bufferStr.substring(8);
    			}
    		}
    		bufferStr+='1';//结束区间应在low与high之间，故加一
    		dos.writeByte(strToByte(bufferStr));
    		bytenum++;
    	}catch(IOException ioe) {
    		ioe.printStackTrace();
    	}finally {
    		try {
    			if(dis!=null)
    				dis.close();
    			if(dos!=null)
    				dos.close();
    		}catch(IOException e) {
    			e.printStackTrace();
    		}
    	}
    	System.out.println(bytenum);
    	return true;
    }
    
    /**
     * 字符流转byte类型
     * @param num
     * @return
     */
    public static byte strToByte(String num) {
    	int count=-128;
    	int iter=0;
    	for(char c:num.toCharArray()) {
    		if(c=='1')
    			count+=Math.pow(2, iter);
    		iter++;
    	}
    	return (byte)count;
    }
    public static void main(String[] args) {
    	Encode en=new Encode();
    	en.encode("f://one.txt", "f://a");
    }
}
