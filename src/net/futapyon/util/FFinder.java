package net.futapyon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

public class FFinder {

	private static String SEP = System.getProperty("file.separator");
	
	private static boolean isZipped(File file){
		return isZipped(file.getAbsolutePath());
	}
	
	private static boolean isZipped(String path){
		String lowerPath = path.toLowerCase();
		return (  lowerPath.endsWith(".zip")
				||lowerPath.endsWith(".jar")
				||lowerPath.endsWith(".war")
				||lowerPath.endsWith(".ear")
				||lowerPath.endsWith(".mar")
				||lowerPath.endsWith(".rar"));
	}	
	

	private String filesToFind;   //part of filename , or regular expression to match filename

	public void setFilesToFind(String filesToFind) {
		this.filesToFind = filesToFind;
	}
	
	
	public static void main(String[] args) throws IOException {
		FFinder finder = new FFinder();
		System.out.println("#### Files to find: "+ args[0]);
		finder.setFilesToFind(args[0]);
		for(int i=1;i<args.length;i++){
			System.out.println("#### Searching in: "+ args[i]);
			finder.find(new File(args[i]));
		}
	}

	public void find(File file) throws ZipException, IOException{
		if(file.isDirectory()){
			processDirectory(file);
		}
		if(file.isFile()){
			processFile(file);
			if(isZipped(file)){
				ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
				processZipInputStream(zis,file.getAbsolutePath());
				zis.close();
				
			}
		}
	}
	
	private boolean evaluate(String path){
		if(path.matches(filesToFind)||path.contains(filesToFind)){
			return true;
		}else{
			return false;
		}
	}
	
	private void processFile(File file){
		if(evaluate(file.getName())){
			System.out.println(file.getAbsolutePath());
		}
	}

	private void processDirectory(File dir) throws ZipException, IOException{
		if(evaluate(dir.getName())){
			System.out.println(dir.getAbsolutePath());
		}
		File[] files = dir.listFiles();
		for(File file:files){
			find(file);
		}
	}
	

	private void processZipInputStream(ZipInputStream zis,String parent) throws IOException{
		ZipEntry ze=null;
		while((ze = zis.getNextEntry())!=null){
			if(evaluate(ze.getName())){
				System.out.println(parent+"!"+SEP+ze.getName());
			}
			if(isZipped(ze.getName())){
				ZipInputStream zzis = new ZipInputStream(zis);
				processZipInputStream(zzis,parent+"!"+SEP+ze.getName());
			}
		}
	}
	
}
