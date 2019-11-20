package com.price_comparison;

import java.util.Scanner;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import com.price_comparison.crawler.CrawlEngine;

public class App 
{
	public static void testRedis() {
		
		RedisClient redisClient = RedisClient.create("redis://localhost:6379");
		StatefulRedisConnection<String, String> connection = redisClient.connect();
		System.out.println("Connected to Redis");
		
		RedisCommands<String, String> commands = connection.sync(); 
		commands.set("key", "Hello World");
		String key = commands.get("key");
		System.out.println(key);

		connection.close();
		redisClient.shutdown(); 
	}
	
    public static void main( String[] args ) throws Exception
    {
    	//testRedis();
    	
    	@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.print("Introduceti produsul: ");
		String product= scan.nextLine();
		CrawlEngine engine = new CrawlEngine();
		engine.crawl(product);		
		
    }
}
