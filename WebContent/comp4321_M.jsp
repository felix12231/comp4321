<html>
<body>
<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="comp4321-Error.jsp"%>

<%@page import="java.util.*" %>
<%@page import="IRUtilities.*" %>
<%@page import="searchingRelated.*" %>

<% 

if(request.getParameter("keywords")!=null){
	
	
	String allString = request.getParameter("keywords");
	char spaceChar = ' ';
	char quoteChar = '\"';
	char plusChar = '+';
	Vector<String> wordVec = new Vector<String>();
	int i = 0, startIndex = 0;
	boolean quoteFound = false;
	boolean firstPlus = true;
	for (; i < allString.length(); i++){
	    char c = allString.charAt(i);   
	    if(c == spaceChar && !quoteFound && firstPlus){
	    	// save the current String
	    	if(i > startIndex){ // safety check
	    		wordVec.add(new String(allString.substring(startIndex, i)));
	    	}
	    	// skip and set startIndex = i+1 for next String
	    	startIndex = i+1;
	    }else if(c == quoteChar){
	    	if(quoteFound){
	    		// the close quotation mark 
	    		if(i > startIndex){ // safety check
		    		wordVec.add(new String(allString.substring(startIndex, i)));
		    	}
	    		quoteFound = false;
	    		startIndex = i+1;
	    	}else{
	    		// the open quotation mark 
	    		startIndex = i+1;
	    		quoteFound = true;
	    	}
	    }else if(c == plusChar){
	    	if(firstPlus){
	    		startIndex = i+1;
	 			firstPlus = false;
			}
		}else if(c == spaceChar){
			wordVec.add(new String(allString.substring(startIndex, i)).replace("+"," "));
			firstPlus = true;
		}
	}

	// handle the last keyword
	if(i > startIndex){
		wordVec.add(new String(allString.substring(startIndex, i)));
	}
	
	if(wordVec.size() == 0 ){
		out.println("Please input keyword(s) to search.");
	}else{
		out.println("The results of "+ wordVec +" are:<hr/>");
		// use Searcher to compute the result
		Searcher searchEngine = new Searcher();
		Vector<Page> resultPageVec = searchEngine.search(wordVec);
		if(resultPageVec != null){
			if(resultPageVec.size() > 0){
				out.println("<table cellspacing=\"50px\">");
				for(i = 0; i < resultPageVec.size(); i++){
					Page currPage = resultPageVec.elementAt(i);
					out.println("<tr><td valign=\"top\" >"+currPage.getScore()+"</td>");
					out.println("<td>");
					out.println("<a href=\""+currPage.getUrl()+"\"> "+currPage.getPageTitle()+"</a><br/>");
					out.println("<a href=\""+currPage.getUrl()+"\"> "+currPage.getUrl()+"</a><br/>");
					out.println(currPage.getLastUpdateTime()+", "+currPage.getPageSize()+"<br/>");
					// Displays up to 5 most frequent stemmed keywords 
					Vector<WordWithFrequency> matchVec = currPage.getTopFiveWord();
					for(int j = 0; j<matchVec.size() ; ++j){
						out.print(matchVec.elementAt(j).getWord()+" "+matchVec.elementAt(j).getFrequency()+"; ");
					}
					out.println("<br/>");
					// Parent link
					out.println( "<font color=\"red\">"+ "Parent Link:"+"</font><br/>");
					Vector<String> parentLinkVec = currPage.getParentLink();
					for(int j = 0; j< parentLinkVec.size(); ++j){
						out.println(parentLinkVec.elementAt(j)+"<br/>");
					}
					
					// Child link 
					out.println("<font color=\"red\">"+"Child Link:"+"</font><br/>");
					Vector<String> childLinkVec = currPage.getChildrenLink();
					for(int j = 0; j< childLinkVec.size(); ++j){
		                out.println(childLinkVec.elementAt(j)+"<br/>");
		            }
					out.println("<br/></td></tr>");
					if(i == 50){
						// up to 50 web pages
						break;
					}
				}
				out.println("</table>");
			}else{
				out.println("No document matches your search.");
			}
		}else{
			out.println("No document is available.");
			out.println("<p>Path of database: "+ searchEngine.getDebugMsg() +"</p>");
			out.println("<p>Path of stopwords.txt: "+ searchEngine.getDebugMsg2() + "</p>");
		}
		
		searchEngine.close();
	}
	
}	


%>
</body>
</html>
