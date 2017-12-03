package com.hxiong.audiodebug.adapter;

public class FileItem {
    
	public static final int TYPE_DIR=1;   //表示是文件夹
	public static final int TYPE_FILE=2;  //表示是文件
	
	public String fileName;  //文件的名称
	public String fileInfo;  //文件的一些信息，比如这个目录下还有多少的文件，或者这个文件的大小是多少
	public int fileType;  //文件的类型，目前只考虑是文件或文件夹
	
	public FileItem(String name,String info,int type){
		this.fileName=name;
		this.fileInfo=info;
		this.fileType=type;
	}
	
}
