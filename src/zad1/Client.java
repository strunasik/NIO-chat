/**
 *
 *  @author Marchyshak Kostiantyn S15159
 *
 */

package zad1;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client  extends JFrame implements ActionListener{

	public final static int port = 6789;
	private String host = "127.0.0.1";
	private SocketChannel channel;
	
	private JTextArea ta = new JTextArea(20, 50);
	private JTextField tf = new JTextField(50);
	private Container cp = getContentPane();
	
	private String login;
	
	private static Charset charset  = Charset.forName("UTF-8");
	
	private static final int BSIZE = 1024;
	private ByteBuffer buf = ByteBuffer.allocate(BSIZE);
	  
	
	public Client(){
		super("Chat NIO");
		JTextField username = new JTextField();
		Object[] message = {
		    "Username:", username,
		};
		
		int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
		login = username.getText();
		
		Font f = new Font("Dialog", Font.ROMAN_BASELINE, 14);
	    ta.setFont(f);
	    ta.setBackground(Color.CYAN);
	    tf.setFont(f);
	    tf.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
	    cp.add(new JScrollPane(ta));
	    cp.add(tf, "South");
	    
	    tf.addActionListener(this);
	    addWindowListener( new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	          dispose();
	          try {
	           channel.close();
	           channel.socket().close();
	          } catch(Exception exc) {}
	          System.exit(0);
	        }
	      });
	    
	    pack();
	    show();
	    int count = 0;
	    try {
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(new InetSocketAddress(host, port));
		  
		    while (!channel.finishConnect());
		    
		    System.out.println("Connected");

		    
		    while(channel.isOpen()){
		    	buf.flip();
		    	buf.clear();
		    	
		    	if((count = channel.read(buf)) > 0){
			    	buf.flip();
		    		ta.setText(ta.getText() + '\n' + new String(buf.array(), 0, count, "UTF-8"));
		    	}
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    

	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			CharBuffer cb = CharBuffer.wrap(login + ": " + tf.getText());
			channel.write(charset.encode(cb));
			tf.setText("");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		new Client();
	
	}

	
}
