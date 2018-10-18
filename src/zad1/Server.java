/**
 *
 *  @author Marchyshak Kostiantyn S15159
 *
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class Server {
	
	private String host = "127.0.0.1";
	private final int port = 6789;
	
	private ServerSocketChannel ssc = null;
	private Selector selector = null;
	
	private static Charset charset  = Charset.forName("ISO-8859-2");
	
	private static final int BSIZE = 1024;
	private ByteBuffer buf = ByteBuffer.allocate(BSIZE);
//	private StringBuffer reqString = new StringBuffer();
	
	public Server(){
		
		try {
			ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);
			ssc.bind(new InetSocketAddress(host, port));
			selector = Selector.open();
			
			ssc.register(selector, SelectionKey.OP_ACCEPT);

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Server started");
	    serviceConnections();
		
	}
	
	private void serviceConnections(){
		SelectionKey key;
		int count;
		while(ssc.isOpen()){
			try {

				selector.select();
				Set<SelectionKey> keys = selector.selectedKeys();
		        Iterator<SelectionKey> iter = keys.iterator();
		        
		        while(iter.hasNext()) {

		            key = iter.next(); 
		            iter.remove();
		            
		            if (key.isAcceptable()) {
		            	SocketChannel cc = ssc.accept();
		            	cc.configureBlocking(false);
		            	cc.register(selector, SelectionKey.OP_READ);
		            }

		            if (key.isReadable()) { 
		            	buf.clear();
		              	SocketChannel cc = (SocketChannel) key.channel();
		              	try {
		              		if((count = cc.read(buf)) > 0){
				              	buf.flip();
				              	sendToAll(buf);
				              	buf.clear();
			              	}
		                } catch (Exception e) { key.cancel(); count = -1; }
		              	
		              	if(count < 0) cc.close();
		              	
		            }

		        }

			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
	}
	

	
	private void sendToAll(ByteBuffer buf) throws IOException {
		for(SelectionKey key : selector.keys()) {
			if(key.isValid() && key.channel() instanceof SocketChannel) {
				SocketChannel sch=(SocketChannel) key.channel();
				sch.write(buf);
				buf.rewind();
			}
		}
	}

	

	public static void main(String[] args) {
		new Server();		
		
	}
}
