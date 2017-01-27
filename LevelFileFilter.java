package me.simon;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class LevelFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String ext = getExtension(f);
		if (ext != null) {
			if (ext.equals("level")) {
				return true;
			}
			else {
				return false;
			}
		}
		
		return false;
	}

	@Override
	public String getDescription() {
		return "JCE Level";
	}
	
	public String getExtension(File f) {
		String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
	}

}
