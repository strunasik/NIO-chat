/**
 *
 *  @author Marchyshak Kostiantyn S15159
 *
 */

package zad1;


public class Main {

  public static void main(String[] args) {


	  new Server();
	  try {
		runInNewJVM();
		runInNewJVM();
	  } catch (Exception e) {
		e.printStackTrace();
	  }
	  
  }
  
  public static void runInNewJVM() throws Exception{
	  String path = System.getProperty("java.home") + System.getProperty("file.separator") + "bin" + System.getProperty("file.separator") + "java";
	  ProcessBuilder processBuilder = new ProcessBuilder(path, "-Dfile.encoding=utf-8", "-cp", System.getProperty("java.class.path"), Client.class.getCanonicalName());
	  processBuilder.start();
  }
  
}
