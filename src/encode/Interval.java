package encode;

/**
 * 
 * 信源符号区间类
 * 设置左右边界来唯一的表示一个信源符号
 * @author 徐嘉兴
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
     * double类型转二进制流
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
    			sb.setCharAt(sb.length()-1, '0');    //最终计算得出的二进制字符串大小小于原double数，总取键范围小于1；
    		}
    	}
    	return sb.toString();
    }
    
    /**
     * 二进制流转double类型方法
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
