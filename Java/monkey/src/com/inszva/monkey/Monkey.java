package com.inszva.monkey;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class Monkey {
	
	protected Socket client;
	protected InputStream in;
	protected OutputStream out;
	
	private byte[] int2bytes(int n) {
		byte[] bytes = new byte[4];
		int i = 0;
		while(n>0) {
			bytes[3-i] = (byte) (n % 256);
			n /= 256;
			i++;
		}
		return bytes;
	}
	
	private int bytes2int(byte[] bytes) {
		int total = 0;
		for(int i = 0;i < 4;i++) {
			total <<= 8;
			total += bytes[i];
		}
		return total;
	}
	
	private void sendMessage(String message) throws IOException {
		int total = message.length();
		byte[] header = this.int2bytes(total);
		out.write(header);
		byte[] buff = message.getBytes();
		int i = 0;
		for(i = 0;i < total - 1024;i += 1024) {
			out.write(buff, i, 1024);
		}
		out.write(buff,i,total - i);
	}
	
	private String readMessage() throws IOException {
		int total = 0;
		byte t[] = {'\0','\0','\0','\0'};
		in.read(t);
		total = this.bytes2int(t);
		byte[] buff = new byte[total];
		int i = 0;
		while(total > 1024) {
			in.read(buff,i,1024);
			total -= 1024;
			i += 1024;
		}
		in.read(buff,i,total);
		return new String(buff);
	}
	
	public Monkey(String addr,int port,String passwd) throws UnknownHostException, IOException {
		this.client = new Socket(addr,port);
		in = client.getInputStream();
		out = client.getOutputStream();
		this.sendMessage("auth " + passwd);
		System.out.println(this.readMessage());
	}
	
	public void release() throws IOException {
		this.out.close();
		this.in.close();
		this.client.close();
	}
	
	public void set(String key,String value) throws IOException {
		this.sendMessage("set " + key + " " + value);
		this.readMessage();
	}
	
	public String get(String key) throws IOException {
		this.sendMessage("get " + key);
		return this.readMessage();
	}
	
	public void remove(String key) throws IOException {
		this.sendMessage("remove " + key);
		this.readMessage();
	}
	
	public void createDB(String dbname) throws IOException {
		this.sendMessage("createdb " + dbname);
		this.readMessage();
	}
	
	public void switchDB(String dbname) throws IOException {
		this.sendMessage("switchdb " + dbname);
		this.readMessage();
	}
	
	public void dropDB(String dbname) throws IOException {
		this.sendMessage("dropdb " + dbname);
		this.readMessage();
	}
	
	public String listDB() throws IOException {
		this.sendMessage("listdb");
		return this.readMessage();
	}
}
