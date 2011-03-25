
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author sam
 */
public class Finder {

	public static void main(String[] args) throws Exception {
		String path = args[0];
		final String expr = args[1];

		List l = new ArrayList();
		findFile(new File(path), new P() {
			public boolean accept(String t) {
				return t.matches(expr) || isZip(t);
			}
		}, l);

		List r = new ArrayList();
		for (Iterator it = l.iterator(); it.hasNext();) {
			File f = (File) it.next();
			String fn = f + "";
			if (fn.matches(expr)) r.add(fn);
			if (isZip(f.getName())) {
				findZip(fn, new FileInputStream(f), new P() {
					public boolean accept(String t) {
						return t.matches(expr);
					}
				}, r);
			}
		}

		for (Iterator it = r.iterator(); it.hasNext();) {
			System.out.println(it.next());
		}
	}

	static void findFile(File f, P p, List r) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) findFile(files[i], p, r);
		} else if (p.accept(f + "")) {
			r.add(f);
		}
	}

	static void findZip(String f, InputStream in, P p, List r) throws IOException {
		ZipInputStream zin = new ZipInputStream(in);

		ZipEntry en;
		while ((en = zin.getNextEntry()) != null) {
			if (p.accept(en.getName())) r.add(f + "!" + en);
			if (isZip(en.getName())) findZip(f + "!" + en, zin, p, r);
		}
	}

	static String[] ZIP_EXTENSIONS = { ".zip", ".jar", ".war", ".ear" };

	static boolean isZip(String t) {
		for (int i = 0; i < ZIP_EXTENSIONS.length; i++) {
			if (t.endsWith(ZIP_EXTENSIONS[i])) {
				return true;
			}
		}
		return false;
	}

	static interface P {
		public boolean accept(String t);
	}
}
