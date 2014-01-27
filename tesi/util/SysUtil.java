package tesi.util;

public abstract class SysUtil {
	/**
	 * Get the method name for a depth in call stack. <br />
	 * Utility function
	 * @param depth depth in the call stack (0 means current method, 1 means call method, ...)
	 * @return method name
	 * @see http://stackoverflow.com/questions/442747/getting-the-name-of-the-current-executing-method
	 */
	public static String getMethodName(final int depth)
	{
	  final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
	  final StackTraceElement caller = ste[ste.length - 1 - depth];
	 
	  return String.format("%s # %s (%d)", caller.getClassName(),caller.getMethodName(),caller.getLineNumber());
	}
}
