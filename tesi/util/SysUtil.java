package tesi.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
	
	public static String getAbsolutePath(String relativepath) throws IOException{
		Path p1=Paths.get(relativepath);
			return p1.toRealPath(LinkOption.NOFOLLOW_LINKS).toString();
	}
	
    public static String getVersionfinal (Object obj) {
	String version = null;
	String shortClassName = obj.getClass().getName().substring(obj.getClass().getName().lastIndexOf(".") + 1);
	try {
		ClassLoader cl = SysUtil.class.getClassLoader();
		String threadContexteClass = obj.getClass().getName().replace('.', '/');
		URL url = cl.getResource(threadContexteClass + ".class");
		if ( url == null ) {
			version = shortClassName + " $ (no manifest)";
		} else {
			String path = url.getPath();
			String jarExt = ".jar";
			int index = path.indexOf(jarExt);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			if (index != -1) {
				String jarPath = path.substring(0, index + jarExt.length());
				File file = new File(jarPath);
				String jarVersion = file.getName();
				JarFile jarFile = new JarFile(new File(new URI(jarPath)));
				JarEntry entry = jarFile.getJarEntry("META-INF/MANIFEST.MF");
				version = shortClassName + " $ " + jarVersion.substring(0, jarVersion.length()
						- jarExt.length()) + " $ "
						+ sdf.format(new Date(entry.getTime()));
				jarFile.close();
			} else {
				File file = new File(path);
				version = shortClassName + " $ " + sdf.format(new Date(file.lastModified()));
			}
		}
	} catch (Exception e) {
		version = shortClassName + " $ " + e.toString();
	}
	return version;
}
    public static String jarBuildTime(){
    	String s=getVersionfinal(new StringUtil());
    	return s.substring(1+s.lastIndexOf("$"));
    }

}
