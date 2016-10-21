package com.app;

public class SpiderPojo {
	
	
	private String descOffset;
	private String titleOffset;
	private String tagsOffset;
	private String imageOffset;
	private String photoOffset;
	private String photoSrc;
	private String imagePath;
	private String spiderUrl;
	private String longNO;
	private String password;
	private String mPassword;
	private String filterOffset;
	private String charSet;
	private String postPath;
	private String postParams;
	private String postLocal;
	private String pageOffset;
	private String draft;
	
	public String getPageOffset() {
		return pageOffset;
	}
	public void setPageOffset(String pageOffset) {
		this.pageOffset = pageOffset;
	}
	public String getPhotoSrc() {
		return photoSrc;
	}
	public void setPhotoSrc(String photoSrc) {
		this.photoSrc = photoSrc;
	}
	public String getPhotoOffset() {
		return photoOffset;
	}
	public void setPhotoOffset(String photoOffset) {
		this.photoOffset = photoOffset;
	}
	public String getPostLocal() {
		return postLocal;
	}
	public void setPostLocal(String postLocal) {
		this.postLocal = postLocal;
	}
	public String getPostParams() {
		return postParams;
	}
	public void setPostParams(String postParams) {
		this.postParams = postParams;
	}
	public String getPostPath() {
		return postPath;
	}
	public void setPostPath(String postPath) {
		this.postPath = postPath;
	}
	public String getCharSet() {
		return charSet;
	}
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}
	public String getDescOffset() {
		return descOffset;
	}
	public void setDescOffset(String descOffset) {
		this.descOffset = descOffset;
	}
	public String getTitleOffset() {
		return titleOffset;
	}
	public void setTitleOffset(String titleOffset) {
		this.titleOffset = titleOffset;
	}
	public String getTagsOffset() {
		return tagsOffset;
	}
	public void setTagsOffset(String tagsOffset) {
		this.tagsOffset = tagsOffset;
	}
	public String getImageOffset() {
		return imageOffset;
	}
	public void setImageOffset(String imageOffset) {
		this.imageOffset = imageOffset;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getSpiderUrl() {
		return spiderUrl;
	}
	public void setSpiderUrl(String spiderUrl) {
		this.spiderUrl = spiderUrl;
	}
	
	public String getLongNO() {
		return longNO;
	}
	public void setLongNO(String longNO) {
		this.longNO = longNO;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getmPassword() {
		return mPassword;
	}
	public void setmPassword(String mPassword) {
		this.mPassword = mPassword;
	}
	public SpiderPojo() {
	}
	
	public String getFilterOffset() {
		return filterOffset;
	}
	public void setFilterOffset(String filterOffset) {
		this.filterOffset = filterOffset;
	}
	
	public String getDraft() {
		return draft;
	}
	public void setDraft(String draft) {
		this.draft = draft;
	}
	@Override
	public String toString() {
		return "SpiderPojo [descOffset=" + descOffset + ", titleOffset="
				+ titleOffset + ", tagsOffset=" + tagsOffset + ", imageOffset="
				+ imageOffset + ", photoOffset=" + photoOffset + ", photoSrc="
				+ photoSrc + ", imagePath=" + imagePath + ", spiderUrl="
				+ spiderUrl + ", longNO=" + longNO + ", password=" + password
				+ ", mPassword=" + mPassword + ", filterOffset=" + filterOffset
				+ ", charSet=" + charSet + ", postPath=" + postPath
				+ ", postParams=" + postParams + ", postLocal=" + postLocal
				+ "]";
	}
	
	
}
