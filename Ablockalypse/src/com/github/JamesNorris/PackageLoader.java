package com.github.JamesNorris;

public class PackageLoader {// TODO annotations, use maven to fix package problem NOT COMPLETE!
	public enum PackageType {
		NMS("net.minecraft.server."), OBC("org.bukkit.craftbukkit.");
		private final String assignment;

		PackageType(String assignment) {
			this.assignment = assignment;
		}

		public String getAssignment() {
			return assignment;
		}
	}

	private static final String PACKAGE_SEPARATOR = ".";
	public static String MINECRAFT_VERSION = "v1_4_5";// the doomsday version when Bukkit decided to destroy my plugins...

	public static Object getObjectByName(String className, PackageType type) {
		try {
			return (Class.forName(type.getAssignment() + MINECRAFT_VERSION + "." + className)).getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Package getPackageByName(String packageName) {
		return Package.getPackage(packageName);
	}

	public static String getPackageName(@SuppressWarnings("rawtypes") Class clazz) throws Exception {
		return clazz.getPackage().getName();
	}

	public static String getPackageName(String classname) {
		int index = classname.lastIndexOf(PACKAGE_SEPARATOR);
		if (index != -1)
			return classname.substring(0, index);
		return "";
	}

	public static boolean isAvailable(String className) {
		boolean found = false;
		try {
			Class.forName(className, false, null);
			found = true;
		} catch (ClassNotFoundException e) {
			found = false;
		}
		return found;
	}

	public static String toPackagePath(Class<?> clazz) {
		String result = clazz.getName();
		int index = result.lastIndexOf('.');
		if (index < 1)
			return "";
		result = result.substring(0, index);
		result = result.replace('.', '/');
		return result;
	}

	public PackageLoader() {
		String version = "v1_4_5";
		StringBuilder builder = new StringBuilder();
		if (!isAvailable("net.minecraft.server." + version + ".World") || !isAvailable("org.bukkit.craftbukkit." + version + ".World"))
			for (int i = 0; i <= 9; i++)
				for (int j = 0; j <= 9; j++)
					for (int k = 0; k <= 0; k++) {
						version = builder.append("v").append(i).append("_").append(j).append("_").append(k).toString();
						if (isAvailable("net.minecraft.server." + version + ".World") || isAvailable("org.bukkit.craftbukkit." + version + ".World")) {
							MINECRAFT_VERSION = version;
							break;
						}
					}
	}
}
