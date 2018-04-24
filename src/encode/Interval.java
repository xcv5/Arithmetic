package encode;

/**
 * 
 * ��Դ����������
 * �������ұ߽���Ψһ�ı�ʾһ����Դ����
 * @author �����
 *
 */
public final class Interval {
    private String left;
    private String right;
    
    public Interval(double left,double right) {
    	this.left=douToStr(left);
    	this.right=douToStr(right);
    }
    
    public Interval(String left,String right) {
    	this.left=left;
    	this.right=right;
    }
    
    public String getLeft() {
    	return this.left;
    }
    
    public String getRight() {
    	return this.right;
    }
    
    /**
     * double����ת��������
     * @param num
     * @return
     */
    public static String douToStr(double num) {
    	StringBuilder sb=new StringBuilder();
    	for(int i=0;i<16;i++) {
    		if(toDouble(sb.toString())<num) {
    			sb.append('1');
    		}
    		else if(toDouble(sb.toString())==num){
    			sb.append('0');
    		}
    		if(toDouble(sb.toString())>num) {
    			sb.setCharAt(sb.length()-1, '0');    //���ռ���ó��Ķ������ַ�����СС��ԭdouble������ȡ����ΧС��1��
    		}
    	}
    	return sb.toString();
    }
    
    /**
     * ��������תdouble���ͷ���
     * @param numString
     * @return
     */
    public static double toDouble(String numString) {
    	double res=0.0;
    	if(numString.length()==0) {
    		return res;
    	}
    	for(int i=0;i<numString.length();i++) {
    		if(numString.charAt(i)=='1') {
    			res+=Math.pow(0.5, i+1);
    		}
    	}
    	return res;
    }
}
