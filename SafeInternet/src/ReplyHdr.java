/*
 * ReplyHdr.java
 */
 


public class ReplyHdr 
{

   static String CR="\r\n";
   static String HTTP_PROTOCOL="HTTP/1.1";
   static String HTTP_SERVER="Java Proxy Server";

   String lastModified ="";
   long   contentLength=0;
   String extraErrorString ="";

	// Set the last modify date
	public boolean setModifiedDate(String date)
   	{
      lastModified = date;
      return true;
    }
	// Adds error description
	public boolean addErrorDescription(String str)
	{
      extraErrorString = str;
      return true;
	}

	// Forms an OK reply
	public String formOk(String ContentType,long  ContentLength)
    {
	    contentLength = ContentLength;
   
        String out =new String();
   	    
        out += HTTP_PROTOCOL + " 200 OK" + CR;
        out += "Server: " + HTTP_SERVER  + CR;
        out += "MIME-version: 1.0"       + CR;

       if (0 < ContentType.length()) 
           out += "Content-type: " + ContentType + CR;
       else
           out += "Content-Type: text/html" + CR;

       if (0 != contentLength)
           out += "Content-Length: " + Long.toString(contentLength) + CR;

       if (0 < lastModified.length())
           out +="Last-Modified: " + lastModified + CR;

       out +=CR;

       return out;
   }


// Forms an error reply body
private String formErrorBody(String Error,String Description)
   {
   String out;
   // Generate Error Body
   out  ="<HTML><HEAD><TITLE>";
   out += Error ; 
   out +="</TITLE></HEAD>";
   out +="<BODY><H2>" + Error +"</H2>\n";
   out +="</P></H3>";
   out += Description;                      
   out +="</BODY></HTML>";
   return out;
   }


           
// Forms an error reply
private String formError(String Error, String Description)
   {
   String body=formErrorBody(Error,Description);
   String header =new String();

   header +=HTTP_PROTOCOL +" " + Error + CR;
   header +="Server: " + HTTP_SERVER   + CR;
   header +="MIME-version: 1.0"        + CR;
   header +="Content-type: text/html"  + CR;
   
   if (0 < lastModified.length())
       header +="Last-Modified: " + lastModified +CR;

   header +="Content-Length: " + String.valueOf(body.length())+ CR;

   header += CR;
   header += body;
   
   return header;
   }


// Forms a created form
public String formCreated()
   {
   return formError("201 Created","Object was created");
   }

// Forms an acceptance form
public String formAccepted()
   {   
   return formError("202 Accepted","Object checked in");
   }

// Forms a partial acceptance form
public String  formPartial()
   {
   return formError("203 Partial","Only partial document available");
   }       

// Forms a moved form
public String formMoved()
   {
   //300 codes tell client to do actions
   return formError("301 Moved","File has moved");
   }

// Forms a found form
public String formFound()
   {
   return formError("302 Found","Object was found");
   }

// Forms a not supported form
public String formMethod()
   {       
   return formError("303 Method unseported","Method unseported");
   }                               

// Forms a not modified form
public String formNotModified()
   { 
   return formError("304 Not modified","Use local copy");
   }

// Forms an unauthorized form
public String formUnauthorized()
   {
   return formError("401 Unauthorized","Unauthorized use of this service");
   }

// Forms a payment required form
public String formPaymentNeeded()
   {
   return formError("402 Payment required","Payment is required");
   }

// Forms a forbidden form
public String formForbidden()
   {
   return formError("403 Forbidden","You need permission for this service");
   }
   
// Forms a not found form
public String formNotFound()
   {
   return formError("404 Not_found","Requested object was not found");
   }

// Forms an internal error form
public String formInternalError() 
   {
   return formError("500 Internal server error","Server broke");
   }

// Forms a not implemented form
public String formNotImplemented()
   {
   return formError("501 Method not implemented","Service not implemented, programer was lazy");
   }

// Forms an overloaded form
public String formOverloaded()
   {
   return formError("502 Server overloaded","Try again latter");
   }   

// Forms a timeout form
public String formTimeout()
   {
   return formError("503 Gateway timeout","The connection timed out");
   }

// Forms a server not found form
public String formServerNotFound()
   {
   return formError("503 Gateway timeout","The requested server was not found");
   }

// Forms a not allowed form
public String formNotAllowed()
   {
   return formError("403 Access Denied","Access is not allowed");
   }
}
