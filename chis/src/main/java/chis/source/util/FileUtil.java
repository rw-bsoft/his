/**
 * @(#)FileUtil.java Created on 2013-5-19 下午5:58:58
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.util;

import java.io.File;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class FileUtil {
	// 验证字符串是否为正确路径名的正则表达式
	private static String matches = "[A-Za-z]:\\\\[^:?\"><*]*";

	// 通过 sPath.matches(matches) 方法的返回值判断是否正确
	// sPath 为路径字符串

	/**
	 * 
	 * @Description:根据路径删除指定的目录或文件，无论存在与否
	 * @param sPath
	 *            要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false
	 * @author ChenXianRui 2013-5-19 下午6:02:20
	 * @Modify:
	 */
	public static boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(sPath);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}

	/**
	 * 
	 * @Description:删除单个文件
	 * @param sPath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 * @author ChenXianRui 2013-5-19 下午6:03:31
	 * @Modify:
	 */
	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 
	 * @Description:删除目录（文件夹）以及目录下的文件
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 * @author ChenXianRui 2013-5-19 下午6:06:00
	 * @Modify:
	 */
	public static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @Description:创建目录（文件夹）
	 * @param folderPath 被创建目录的文件路径
	 * @return 目录创建成功返回true，否则返回false
	 * @author ChenXianRui 2013-5-19 下午6:11:53
	 * @Modify:
	 */
	public static boolean CreateFolder(String folderPath) {
		boolean flag = false;
		try {
			String filePath = folderPath;
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				flag = myFilePath.mkdir();
			}
		} catch (Exception e) {
			System.out.println("新建文件夹操作出错");
			e.printStackTrace();
		}
		return flag;
	}

//	public static void main(String[] args) {
//		 String path = "D:\\Abc\\123\\Ab1";
//		 boolean result = FileUtil.CreateFolder(path);
//		 System.out.println(result);
//		 path = "D:\\Abc\\124";
//		 result = FileUtil.DeleteFolder(path);
//		 System.out.println(result);
//
//	}
}
