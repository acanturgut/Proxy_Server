# ProxyServer-Safe-Internet

1. Purpose of the Project:
For this project, it was designed a proxy server. Proxy server gets http request from user and if there is no banned word, it forward this request to main server.
This program gives 3 option at the beginning. If you press Y, proxy server is run. If you press N, proxy server is closed. If you press A, program lists all banned words and ask you a new banned word if user wants to add.

2. Classes and Methods:
In this project, 3 class were used.
SafeInternet Class:
This method get port number as an argument. Then it asks if user wants to close program, run proxy server or manage banned words.
Y mode: If the user choice to run proxy server(Y), ProxyServer class is called. Also, thread starts, which makes user able to connect more than one website.
A Mode: If the use choice to manage banned words(A), program lists all banned words and ask user if she wants to add new one to database.txt file. It ask user a new word to ban after user press d.
N Mode: If the user choice to close program(N), program is closed.
ProxyServer Class:
This class creates a socket using the input port number. If there is no input port number, it uses 8080 as a default. After creating socket, it runs in a thread. It listens this socket until the program is closed.
ProxyThread Class:
This class is the most functional class in this project. It has getHttp, readLine, forwardHttp and filter methods. It uses these methods to get http request as an input from Client, reading this input, filtering the URL request read and streaming this request if it is not banned.
This class also keep the fallowing data in logfile.txt file:
1) Date 2) User IP 3) requested URL 4) delay if website is not banned 5) banned word if website is banned


Method 1: getHttp()
It gets the request data came from client, a StringBuffer and a type. It creates a ByteArrayOutputStream object and call forwardHttp to write Datas read on Client’s input.

Method 2: readLine()
It gets the request data comes from the client as an input. It just read a line of Client’s input and write it to StringBuffer. At the end, It transforsm StringBuffer to String and return this string.

Method 3: forwardHttp()
It gets the request data came from client, a StringBuffer and type and an
ByteArrayOutputStream created by getHttp().
It reads all lines and look for http request, response code, host and content length. It saves this data to other variables defined before. Also, it writes all lines it read to ByteArrayOutputStream.

Method 4: Filter()
This method gets a string as an input. It opens the file database.txt to get list of banned words. Then it searches if there is a banned word in its input. If there is, it saves it in a string defined before to be able to print it in other methods. Then it returns false. Otherwise, it returns true.

RUN TIME:
Run method gets some data such as request, port number, host name, IP address, start time, end time, etc. Then, it creates a new socket to connect internet. After that, it filters the host name. If there is a banned word, it sent a message to browser using BufferedOutputStream which is created on the socket. If there is no, it connects to server. It sends request and get Response. If there is no banned word, it sends this response to client. Then, it modifies logFile.txt document and print what it does to command prompt.

3. Problems about Code
This code was written to work if and only if http request is done. However, some websites use https such as google, ku.edu.tr, Wikipedia, facebook etc. Thus, it is not possible to connect this sites if computer connected to this program. Reason of that this web pages require HTTPS. If any web page use HTTPS, with SafeInternet program user unable to connect that kind of web pages.
In that case, if any web page use HTTPS, not openable with SafeInternet proxy server.
In addition, this program transfers data as a byte. It can transfer some data such as pictures and texts properly. But, for example, it may not be able to transfer javascript or video. Thus, some websites cannot be seen as it should be.
Uncorrected Observations
Some webpages load completely after applying interruption on the running program (ctrl + c).

4. Results and Conclusion:
This SafeInternet program was tasted using Chrome, Firefox and Edge. It was observed that it works properly for all http website. There are only two problems which was explained in the part “3) Problems about Code”. If the website does not contain any banned word, proxy server stream the URL request and response. If it contains any banned word, “This Webpage Is Forbiden To Access” error is written to browser. All requests are saved to logfile.txt.

