package view;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import encode.Decode;
import encode.Encode;

/**
 * 
 * @author �����
 * �û������������
 *
 */
public class Main extends JFrame{
	private JLabel jl;
	private JButton encode;
	private JButton decode;
	private JTextField jtSou;
	private JTextField jtPo;
	private JTextField jtf;
	
	public Main() {
		super("Arithmetic Encode");
		setLayout(null);
		jl=new JLabel("���������ʵ��");
		jl.setBounds(120,50,500,30);
		jl.setFont(new Font("����", Font.PLAIN, 30) );
		add(jl);
		jtSou=new JTextField();
		jtSou.setBounds(80,100,150,30);
		jtPo=new JTextField();
		jtPo.setBounds(280,100,150,30);
		add(jtSou);
		add(jtPo);
		jtf=new JTextField("������Դ�ļ�·��(eg: d://exp.text)�뱣��·��(eg: d://ans)");
		jtf.setBounds(80,150,350,30);
		jtf.setEditable(false);
		add(jtf);
		encode=new JButton("ѹ��");
		decode=new JButton("����");
		encode.setBounds(100, 200, 100, 30);
		decode.setBounds(280, 200, 100, 30);
		add(encode);
		add(decode);
		encode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String filePath=jtSou.getText();
				String outFilePath=jtPo.getText();
				boolean finnished=false;
				Encode en=new Encode();
				finnished=en.encode(filePath, outFilePath);
				if(finnished) {
					jtf.setText("����ɹ���ѹ���ļ��ѱ�����"+outFilePath);
				}
				else {
					jtf.setText("����ʧ��");
				}
			}
		});
		decode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String filePath=jtSou.getText();
				String outFilePath=jtPo.getText();
				boolean finnished=false;
				Decode de=new Decode();
				finnished=de.decode(filePath, outFilePath);
				if(finnished) {
					jtf.setText("����ɹ���ѹ���ļ��ѱ�����"+outFilePath);
				}
				else {
					jtf.setText("����ʧ��");
				}
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500,300);
		setVisible(true);
		this.setResizable(true);
	    this.setLocationRelativeTo(null);
	}
	static Main userViewer;
	
    public static void main(String[] args) {
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			userViewer=new Main();
    		}
    	});
    	
    }
}
