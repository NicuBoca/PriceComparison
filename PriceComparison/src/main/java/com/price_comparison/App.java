package com.price_comparison;

import java.util.Scanner;

import com.price_comparison.crawler.CrawlEngine;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.print("Introduceti produsul: ");
		String product= scan.nextLine();
		CrawlEngine engine = new CrawlEngine();
		engine.crawl(product);
    }
}
