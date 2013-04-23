/**
 * Response formatter
 * $Id: ResponseFormat.java,v 1.11 2010/07/28 22:05:30 dwd Exp $
 *
 * Copyright (c) 2009, FERMI NATIONAL ACCELERATOR LABORATORY
 * All rights reserved. 
 *
 * For details of the Fermitools (BSD) license see Fermilab-2009.txt or
 *  http://fermitools.fnal.gov/about/terms.html
 */

package atlas.frontier.fdo;

import java.io.OutputStream;
import java.io.PrintStream;

public class FrontierResponseFormat
 {
	public static String frontierServerName = "Frontier";

  static void handleError(String where, Exception e)
   {
    System.out.println("Error is ignored in ResponseFormat."+where+": "+e.getMessage());
   }

  
  static String xml_str(String msg)
   {
    StringBuffer b=new StringBuffer(msg);
    while(true)
     {
      int pos=b.indexOf("&");
      if(pos<0) break;
      b.replace(pos,pos+1,"&amp;");
     }
    while(true)
     {
      int pos=b.indexOf("'");
      if(pos<0) break;
      b.replace(pos,pos+1,"&apos;");
     }
    while(true)
     {
      int pos=b.indexOf("\"");
      if(pos<0) break;
      b.replace(pos,pos+1,"&quot;");
     }
    while(true)
     {
      int pos=b.indexOf(">");
      if(pos<0) break;
      b.replace(pos,pos+1,"&gt;");
     }
    while(true)
     {
      int pos=b.indexOf("<");
      if(pos<0) break;
      b.replace(pos,pos+1,"&lt;");
     }
    return b.toString();
   }
  
  public static void begin(OutputStream out,String version,String xmlversion) throws Exception
   {
	  PrintStream ps = new PrintStream(out);
	  ps.print("<?xml version=\"1.0\" encoding=\"US-ASCII\"?>\n");
	  ps.print("<!DOCTYPE frontier SYSTEM \"http://frontier.fnal.gov/frontier.dtd\">\n");
	  ps.print("<frontier version=\"");
	  ps.print(version);
	  ps.print("\" xmlversion=\"");
	  ps.print(xmlversion);
	  ps.print("\">\n");
   }

   
  public static void transaction_start(OutputStream out,int num) throws Exception
   {
	  PrintStream ps = new PrintStream(out);
	  ps.print(" <transaction payloads=\"");
	  ps.print(num);
	  ps.print("\">\n");
   }

   
  public static void transaction_end(OutputStream out) throws Exception
   {
	  PrintStream ps = new PrintStream(out);
    ps.print(" </transaction>\n");
   }
      
   
  public static void payload_start(OutputStream out,String type,String version,String encoder) throws Exception
   {
	  PrintStream ps = new PrintStream(out);
    ps.print("  <payload type=\"");
    ps.print(type);
    ps.print("\" version=\"");
    ps.print(version);
    ps.print("\" encoding=\"");
    ps.print(encoder);
    ps.print("\">\n");
    ps.print("   <data>");
   }

   
  public static void payload_end(OutputStream out,int err_code,String err_msg,String md5,int rec_num,long full_size) throws Exception
   {
	  PrintStream ps = new PrintStream(out);
    ps.print("</data>\n");
    ps.print("   <quality error=\"");
    ps.print(err_code);
    if(err_msg.length()>0)
     {
      ps.print("\" message=\"");
      err_msg=frontierServerName+" "+err_msg;
      ps.print(xml_str(err_msg));
     }
    if(md5.length()>0)
     {
      ps.print("\" md5=\"");
      ps.print(md5);
     }    
    if(rec_num>=0)
     {
      ps.print("\" records=\"");
      ps.print(rec_num);
     }
    ps.print("\" full_size=\"");
    ps.print(full_size);
    ps.print("\"/>\n");
    ps.print("  </payload>\n");
   }
         
  static void commit(OutputStream out) throws Exception
   {
	  PrintStream ps = new PrintStream(out);
    // a flush finishes & sends the response header and everything else
    //  written so far
    ps.flush();
   }

  static void keepalive(OutputStream out) throws Exception
   {
	  PrintStream ps = new PrintStream(out);
    ps.print("\n   <keepalive />");
    commit(out); /* make sure keepalive is sent immediately */
   }
  
  static void putGlobalError(OutputStream out,String msg)
   {
    msg=frontierServerName+" "+msg;
    try
     {
  	  PrintStream ps = new PrintStream(out);
      ps.print("\n  <global_error msg=\""+xml_str(msg)+"\"/>\n");
     }
    catch(Exception e)
     {
      handleError("putGlobalError",e);
     }
   }

      
  public static void close(OutputStream out)
   {
    try
     {
  	  PrintStream ps = new PrintStream(out);
      ps.print("</frontier>\n");
     }
    catch(Exception e)
     {
      handleError("close",e);
     }
   }   
 }
