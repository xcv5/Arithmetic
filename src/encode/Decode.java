package encode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * �������������
 * ��������ķ���Ϊ��ѹ���ļ����������
 * @author �����
 *
 */
public final class Decode {
	
	private Map<Character,Interval> charMap=new HashMap<>();
	
	/**
	 * ���뷽��
	 * @param filePath  Դ�ļ�·��
	 * @param outFilePath  Ŀ���ļ�·��
	 * @return
	 */
    public boolean decode(String filePath,String outFilePath) {
    	File file=new File(filePath);
    	if(!file.exists())
    		return false;
    	File outFile=new File(outFilePath);
    	
    	DataInputStream dis=null;
    	DataOutputStream dos=null;
    	
    	boolean end=false; //�뱾������־
    	String left="0000000000000000";//���ڹ����뱾
    	String right;
    	
    	DataStream low=new DataStream();
    	DataStream high=new DataStream();
    	DataStream range=null;
    	DataStream value=new DataStream(); //����ÿ�μ�����
    	DataStream buffer=new DataStream();//������ʵ��
    	boolean tran=false;//ת���־
    	
    	try {
    		dis=new DataInputStream(new FileInputStream(file));
    		
    		//��ȡԴ�ļ��뱾
    		while(dis.available()>0) {
    			char c=dis.readChar();
    			if(c=='$') {
    				if(end)
    					break;
    				else
    					end=true;
    			}
    			byte fir=dis.readByte(),nex=dis.readByte();
    			right=byteToStr(fir)+byteToStr(nex);
    			Interval in=new Interval(left, right);
    			charMap.put(c, in);
    			left=right;
    		}
    		if(!outFile.exists())
        		outFile.createNewFile();
    		dos=new DataOutputStream(new FileOutputStream(outFile));
    		
    		String start="";//�״����룬��ȡ���ַ�����
    		for(int i=0;i<2;i++) {
    			if(dis.available()>0)
    				start+=byteToStr(dis.readByte());
    			else
    				start+="00000000";
    		}
    		buffer.setStream(start);
    		
    		Entry<Character,Interval> outChar=findChar(buffer);
    		
    		low.setStream(outChar.getValue().getLeft());//��Low��high���г�ʼ��
    		high.setStream(outChar.getValue().getRight());
    		range=high.copyOf().sub(low.getStream());
    		
    		char c=outChar.getKey();
    		dos.writeByte((byte)c);//�ļ���������ַ�
    		
    		while(true) {
    			  while(buffer.getStream().length()<64) { //���������
    				  if(dis.available()>0)
    					  buffer.in(byteToStr(dis.readByte()));
    				  else
    					  buffer.in("00000000");
    			  }
    			  
    			  value=buffer.copyOf().sub(low.getStream()).div(range.getStream());//�������
    			  
    			  outChar=findChar(value);
    			  c=outChar.getKey();
    			  
    			  if(tran&&c=='e')//������־�ж�
    				  break;
    			  
    			  //��low���о����������ȡ��Ӧ���±߽�
    			  high=low.copyOf().add(range.copyOf().mul(outChar.getValue().getRight()).getStream());
				  low.add(range.copyOf().mul(outChar.getValue().getLeft()).getStream());
				  
				  //��������߽���������
				  int delLen=high.del(low.getStream()); 
				  low.out(delLen);
				  buffer.out(delLen);
				  low.resetLow();
				  high.resetHigh();
				  
				  range=high.copyOf().sub(low.getStream());
				  
				  //ת���ж��봦��
				  if(c=='$') {
					  if(tran) {
						  dos.writeByte((byte)c);
						  tran=false;
					  }
					  else
						  tran=true;
				  }
				  else {
					  dos.writeByte((byte)c);
				  }
					  
    		}
    		
    	}catch(FileNotFoundException e) {
    		e.printStackTrace();
    	}catch(IOException e) {
    		e.printStackTrace();
    	}finally {
    		try {
    			if(dis!=null)
    				dis.close();
    		}catch(IOException e) {
    			e.printStackTrace();
    		}
    	}
    	return true;
    }
    
    /**
     * byteת�ַ�������
     * @param by
     * @return
     */
    public String byteToStr(byte by) {
    	StringBuilder sb=new StringBuilder();
    	int num=(int)by+128;
    	int iter=0;
    	while(num/2!=0) {
    		sb.append(num%2==0?'0':'1');
    		num/=2;
    		iter++;
    	}
    	sb.append(num==0?'0':'1');
    	if(iter<7) {
    		for(;iter<7;iter++)
    			sb.append('0');
    	}
    	return sb.toString();
    }
    
    public Entry<Character, Interval> findChar(DataStream value) {
    	for(Entry<Character,Interval> en:charMap.entrySet()) {
    		if(value.comparaTo(en.getValue().getLeft())>=0&&value.comparaTo(en.getValue().getRight())<0)
    			return en;
    	}
    	return null;
    }
}
