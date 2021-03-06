package tesi.util.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contiene i settaggi globali per il Logger, ad essere sincero non so se venga utilizzato
 * @author darshan
 *
 */
public abstract class GlobalLogger {

	  public static Logger logger;
	  public static Level level;
	  public static Handler console;
	  
	  //public static final Level level_verbose=Level.FINEST;
	  //public static final Level level_quet=Level.WARNING;
	  
	  static{
		  logger=Logger.getGlobal();
		  console= new java.util.logging.ConsoleHandler();
	  }
	  
	  private static void do_init(){
		  logger.setLevel(level);
		  console.setLevel(level);
		  //logger.addHandler(console);
		  logger.finer("Inizializzo il logger");
		  
		  
	  }
	  
	  public static void init_verbose(){
		  level=Level.FINEST;//300
		  do_init();		  
	  }

	  public static void init_middle(){
		  level=Level.INFO;
		  do_init();		  
	  }	  

	  
	  public static void init_quiet(){
		  level=Level.WARNING;//900
		  do_init();		  
	  }	  
}
