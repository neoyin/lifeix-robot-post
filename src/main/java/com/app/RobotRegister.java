package com.app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.lifeix.client.DoveBoxResponse;
import com.lifeix.client.DoveboxClientUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.impl.MultiPartWriter;

public class RobotRegister {

	private static Client getClient(){
		ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(MultiPartWriter.class);
		Client client = Client.create(cc);
		return client;
	}
	
	private static final String PASSWORD ="123123";
	private static final String DOVEBOX_PATH ="http://dbapi.xy.l99.com/";
	
	private static String generateName(){
		return "立方块";
	}
	
	private static String generateEmail(){
		return "l";
	}
	
	private static void regester(){

		Client client = getClient();
		
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		
		params.add("auth_key",generateEmail());
		params.add("client", "API");
		params.add("vilidate","true");
		params.add("name", generateName());
		params.add("password",PASSWORD);
		
		WebResource webResource = client.resource(DOVEBOX_PATH+"dovebox/user/register");
		String response= webResource.queryParams(params).type(MediaType.TEXT_PLAIN).accept(MediaType.APPLICATION_JSON).post(String.class);
		
		//DoveBoxResponse tResponse = new DoveBoxResponse();
		//tResponse.setResponse(response);
		System.out.println(response);
	
	}
	
	public static void main(String[] args) {
		//regester();
	
		List<String> pageLinks = new ArrayList<String>();
		pageLinks.add("test");
		pageLinks.add("test");
		
		Set<String> pl = new HashSet<String>(pageLinks);
		for (String string : pl) {
			System.out.println(string);
		}
	}
	
	
}
