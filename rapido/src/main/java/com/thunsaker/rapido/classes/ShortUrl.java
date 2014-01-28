package com.thunsaker.rapido.classes;

public class ShortUrl {
	private String long_url;
	private String url;
	private String hash;
	private String global_hash;
	private String new_hash;
	
	public String getLong_url() {
		return long_url;
	}
	public void setLong_url(String long_url) {
		this.long_url = long_url;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public String getGlobal_hash() {
		return global_hash;
	}
	public void setGlobal_hash(String global_hash) {
		this.global_hash = global_hash;
	}
	
	public String getNew_hash() {
		return new_hash;
	}
	public void setNew_hash(String new_hash) {
		this.new_hash = new_hash;
	}
}

/*

{
	"status_code": 200,
	"status_txt": "OK",
	"data": {
		"long_url": "http://thomashunsaker.com/",
		"url": "http://bit.ly/RHFC34",
		"hash": "RHFC34",
		"global_hash": "V7s4",
		"new_hash": 0
	}
}
*/