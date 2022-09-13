/*-------------------------------------------------------------------------*
 *---									---*
 *---		Caller.java						---*
 *---									---*
 *---	    This file defines a Jsoup-library using program that	---*
 *---	queries a Scienceomatic website for to lookup a scientific	---*
 *---	value.								---*
 *---									---*
 *---	----	----	----	----	----	----	----	----	---*
 *---									---*
 *---	Version 1a		2022 June 7		Joseph Phillips	---*
 *---									---*
 *-------------------------------------------------------------------------*/

import	java.net.*;
import	java.io.*;
import	org.json.*;
import	org.jsoup.Jsoup;
import	org.jsoup.nodes.Document;
import	org.jsoup.Connection.Method;
import	org.jsoup.Connection.Response;
import	org.jsoup.nodes.Document;
import	org.jsoup.nodes.Element;
import	org.jsoup.select.Elements;

public
class	Caller
{

  //  PURPOSE:  To hold the default URL.
  public final static
  String	DEFAULT_URL	= "http://scienceomatic.com:8080";

  //  PURPOSE:  To hold the default subject of the query.
  public final static
  String	DEFAULT_SUBJECT	= "Earth";

  //  PURPOSE:  To hold the default attribute of the query.
  public final static
  String	DEFAULT_ATTRIBUTE	= "mass";


  //  PURPOSE:  To hold the URL of the website to query.
  private
  String	urlPrefix_	= DEFAULT_URL;


  //  PURPOSE:  To hold the subject of the query.
  private
  String	subject_	= DEFAULT_SUBJECT;


  //  PURPOSE:  To hold the attribute of the query.
  private
  String	attribute_	= DEFAULT_ATTRIBUTE;


  //  PURPOSE:  To hold the session cookie.
  private
  String	sessionCookie_	= "";


  //  PURPOSE:  To return the URL of the website to query.  No parameters.
  protected
  String	getUrlPrefix	()
				{ return(urlPrefix_); }


  //  PURPOSE:  To return the subject of the query.
  protected
  String	getSubject	()
				{ return(subject_); }


  //  PURPOSE:  To return the attribute of the query.
  protected
  String	getAttribute	()
				{ return(attribute_); }


  //  PURPOSE:  To return the session cookie.  No parameters.
  protected
  String	getSessionCookie()
				{ return(sessionCookie_); }


  //  PURPOSE:  To set the session cookie to 'newCookie'.  No return value.
  protected
  void		setSessionCookie(String		newCookie
				)
				{ sessionCookie_	= newCookie; }

  //  PURPOSE:  To do the initial call for knowledge bases matching the
  //  	the keyword query, parse the result for the initial kb number,
  //	and to return this kb number.  No parameters.
  protected
  int		getKbIntFromInitQuery
				()
				throws IOException, JSONException
  {
    Document	doc		= null;
    String	kbList		= "";
    JSONObject	json		= null;
    int		kbIndex		= 0;
    String	url		= getUrlPrefix() + "/kbList?keywords=base";

    System.out.print(url + " => ");
    System.out.flush();
    doc		= Jsoup.connect(url).ignoreContentType(true).userAgent("Jsoup").followRedirects(true).post();		//  CHANGE THAT null!
    kbList	= doc.toString();
    kbList	= kbList.substring(kbList.indexOf("{"), kbList.lastIndexOf("}")+1);		//  CHANGE THAT null!
    json	= new JSONObject(kbList);
    kbIndex	= json.getJSONArray("data").getJSONObject(0).getJSONObject("kbEntry").getInt("kbNum");		// CHANGE THAT 0
    System.out.println(kbIndex);
    return(kbIndex);
  }


  //  PURPOSE:  To give the website the kb id of an existing kb ('oldKbId')
  //	in order to log into a new kb.  Both sets the cookie from this call
  //	('sessionCookie_') and returns id of new kb that we are logged into.
  protected
  int		getKbIntFromKbLogin
				(int		oldKbId
				)
				throws IOException,
				       JSONException,
				       NumberFormatException
  {
    Response	response	= null;
    String	url		= getUrlPrefix() + "/kb/" + oldKbId;
    int		newKbId;

    System.out.print(url + " => ");
    System.out.flush();
    response	= Jsoup.connect(url).method(Method.GET).execute();  // CHANGE THAT null!
    url		= response.url().toString();
    setSessionCookie(response.cookie("session")); //  CHANGE THAT empty string!

    newKbId	= Integer.parseInt(url.substring(url.lastIndexOf("/")+1)); //  CHANGE THAT 0!

    System.out.println(newKbId);
    return(newKbId);
  }

  //  PURPOSE:  To get the JSON text of the property <'subject','attribute'>
  //  	and to from knowledge base 'kbId', and to print it.
  protected
  void		printProperty	(int		kbId,
  				 String		subject,
  				 String		attribute
  				)
				throws IOException,
				       JSONException
  {
    Response	response	= null;
    JSONObject	json		= null;
    String	url		= getUrlPrefix()	+
    				  "/kb/"		+
				  kbId			+
				  "/lookup/"		+
				  subject		+
				  "/"			+
				  attribute;

    System.out.println(url + ":");
    System.out.flush();
    response	= Jsoup.connect(url).ignoreContentType(true).cookie("session",getSessionCookie()).method(Method.GET).execute();		//  CHANGE THAT null!
    json	= new JSONObject(response.body());		//  CHANGE THAT null!
    System.out.println("\tValue:\t\t" + json.getDouble("value"));	//  CHANGE THAT empty string!
    System.out.println("\tUnits:\t\t" + json.getString("units"));	//  CHANGE THAT empty string!
    System.out.println("\tDimension:\t" + json.getString("dimension"));	//  CHANGE THAT empty string!

    System.out.print("");
  }


  //  PURPOSE:  To logout of the knowledge base with id 'kbId'.  No return
  //	value.
  protected
  void		logout		(int		kbId
  				)
				throws IOException
  {
    int		newKbId;
    Response	response	= null;
    String	url		= getUrlPrefix()	+
				  "/kb/"		+
				  kbId			+
				  "/noSaveClose?name=TetyanaLegeyda";

    System.out.print(url + " => ");
    System.out.flush();
    response	= Jsoup.connect(url).cookie("session",getSessionCookie()).method(Method.GET).execute();		//  CHANGE THAT null!
    System.out.println("\n" + response.statusMessage().toString());
  }


  //  PURPOSE:  To initialize 'this' to query website 'newUrlPrefix'.
  //	No return value.
  public
  Caller			(String		newUrlPrefix,
  				 String		newSubject
  				)
  {
    urlPrefix_	= newUrlPrefix;
    subject_	= newSubject;
  }


  //  PURPOSE:  To run the program.   Returns 0 on success or 1 otherwise.
  public
  int		run		()
  {
    int			statusForSys	= 0;

    try
    {
      //  (1) Get the base kb id:
      int	oldKbId	= getKbIntFromInitQuery();

      //  (2) Get the new kb id:
      int	newKbId	= getKbIntFromKbLogin(oldKbId);

      //  (3) Get the property
      printProperty(newKbId,getSubject(),getAttribute());

      //  (4) Logout:
      logout(newKbId);
    }
    catch  (JSONException error)
    {
      System.err.println("Error: " + error);
      statusForSys	= 1;
    }
    catch  (IOException error)
    {
      System.err.println("Error: " + error);
      statusForSys	= 1;
    }
    catch  (NumberFormatException error)
    {
      System.err.println("Error: " + error);
      statusForSys	= 1;
    }

    return(statusForSys);
  }


  //  PURPOSE:  To query the URL either given in 'args[0]' (if it exists) or
  //	'DEFAULT_URL' for the property of <'Earth','mass'>.  Returns 0 to OS on
  //	success or 1 otherwise.
  public static
  void		main		(String[]		args
				)
  {
    String	urlPrefix	= (args.length < 1) ? DEFAULT_URL : args[0];
    String	subject		= (args.length < 2) ? DEFAULT_SUBJECT : args[1];
    Caller	caller		= new Caller(urlPrefix,subject);
    int		statusForOs	= caller.run();

    System.exit(statusForOs);
  }

}