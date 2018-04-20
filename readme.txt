*********************************************************************************************
Team information:
CHAN, Hiu Lok Felix 	SID:20177897 	(hlfchanaa@connect.ust.hk)
OR, Ka Po   		SID:20342179 	(kpor@connect.ust.hk)
WANG, Yuan  		SID:20175825 	(ywangcb@connect.ust.hk)
*********************************************************************************************

Instructions to build the spider and the test program:

1.Run Command Prompt and use command "cd" to the directory "COMP4321-Phase1-Code/"
2.Run command "java -version" and "javac -version" to confirm the right version of Java is installed.
3.Run the following command one by one to compile the program:

	javac -d bin -sourcepath src src/IRUtilities/Porter.java
	javac -d bin -sourcepath src src/StopStem.java
	javac -d bin -sourcepath src -cp lib/htmlparser.jar:lib/jsoup-1.11.2.jar src/Crawler.java
	javac -d bin -sourcepath src -cp lib/jdbm-1.0.jar src/Index.java
	javac -d bin -sourcepath src -cp lib/jdbm-1.0.jar src/InvertedIndex.java
	javac -d bin -sourcepath src -cp lib/jdbm-1.0.jar src/MoreThanOneIndex.java
	javac -d bin -sourcepath src -cp bin:lib/htmlparser.jar:lib/jdbm-1.0.jar src/Spider.java
	javac -d bin -sourcepath src -cp bin:lib/htmlparser.jar:lib/jdbm-1.0.jar src/TestProgram.java

4.Run the following command to execute the Spider:

	java -cp bin:lib/htmlparser.jar:lib/jdbm-1.0.jar:lib/jsoup-1.11.2.jar Spider

//A .db file containing all relations and a .lg file storing the transaction log will be created.

5.Run the following command to execute the test program:

	java -cp bin:lib/htmlparser.jar:lib/jdbm-1.0.jar:lib/jsoup-1.11.2.jar TestProgram

//A "spider_result.txt" will be created and it stores
---------------------------------------------------------------------------------------------

