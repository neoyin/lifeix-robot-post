package com.lifeix.post;

import java.util.List;

public class DoveboxFormData {

	
	private String html;
	
	private String fileName;
	
	/** 标题 */
	private String title;
	/** */
	private String desc;
	/** 图片位置*/
	private List<String>  pic;
	
	private String tag;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}


	public List<String> getPic() {
		return pic;
	}

	public void setPic(List<String> pic) {
		this.pic = pic;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public DoveboxFormData(String title, String desc, List<String> pic, String tag) {
		super();
		this.title = title;
		this.desc = desc;
		this.pic = pic;
		this.tag = tag;
	}

	
	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	@Override
	public String toString() {
		return "DoveboxFormData [title=" + title + ", desc=" + desc + ", pic="
				+ pic + ", tag=" + tag + "]";
	}

}
