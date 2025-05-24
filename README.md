# 📝 Line Reader

A Spring Boot REST API that serves individual lines from an immutable text file.  
This service is designed to handle multiple client requests simultaneously and to support any file size.

- Collects a line from a text file from a GET endpoint `/lines/<line index>`

---

## ⚠️ Requirements

- Java 17 & Apache Maven (check troubleshooting for installation steps)

---

## ❓ Defined Questions

### 1 - How does the system work?

The service acts as a network server which exposes a REST API which allows clients to request a
specific line on a local file. 

The local file is read on initialization of the service and an .idx file is generated in order to map the indexes of each line - storing the offset for quick lookups on the local file.
This solution improves the performance of the service significantly.

While processing the read lines file in order to generate the index file, the user can still submit a GET request.
Unfortunately the request is not very performant given it's sharing resources and accessing the same file as the indexing process.


### 2 - How will your system perform with a 1 GB file? a 10 GB file? a 100 GB file?

🟢 1 GB File

The service efficiently handles 1 GB files by reading lines using buffered streams and indexing the file in under a minute.
With roughly 50 million lines, response times for requests typically stay under 3 seconds, providing increased responsiveness for most use cases.

🟡 10 GB File

Performance remains acceptable at this scale, with most requests completing within 30 seconds.
The indexing process takes about 3 minutes to complete fully. Accessing lines deep within the file takes longer initially, but integration with a Caffeine cache significantly improves repeated or nearby line access times by storing frequently requested lines in memory.
Overall, this caching combined with indexing enables scalable access though caching benefits depend on access patterns.

🔴 100 GB File

At this scale, performance degradation becomes significant because the current fallback method reads from the file start on each request before indexing completes, accessing lines near the file end (e.g: 100 million+ lines) incurs substantial latency. The indexing process itself also takes considerably longer.
To improve performance for files this large, a system redesign will be necessary—such as chunked indexing (splitting the index file) or resumable indexing.

### 3 - How will your system perform with 100 users? 10000 users? 1000000 users?

🟢 100 Users

At this scale, the service will handle the traffic smoothly. 
Given the caching introduced, requests that look up for similar lines will be quite fast. 
The indexing should run in the background making the operations non-blocking.
The performance will also vary for a non cached line depending on the size of the read file.

🟡 10,000 Users

Performance continues to be OK. 
The cache still helps a lot, but with many users hitting uncached lines or the file simultaneously might be an issue. 
Given this hurdle, responses times will increase compared to the 100 users.

🔴 1,000,000 Users

This is quite a large number of users. 
The current service will start struggling under this load. 
With so many concurrent requests, resources on a single machine will be limited and file access will slow things down. 
To handle this well, we will need to introduced horizontal scaling, distributed caching or rethink how we read/access the file.

### 4 - What documentation, websites, papers, etc did you consult in doing this assignment?

Spring Boot Documentation
https://docs.spring.io/spring-framework/reference/

Caffeine Cache Documentation
https://github.com/ben-manes/caffeine

Helpful Websites/Tools used for searches during the course of the development - StackOverflow, ChatGPT and Medium

### 5 - What third-party libraries or other tools does the system use? How did you choose each library or framework you used?

Spring Boot – Used to build and manage the application. Thanks to my familiarity with the framework, it allowed for rapid prototyping and explainable solution.

Lombok – Helped reduce boilerplate code, especially for logging and building model objects. It made the codebase cleaner and easier to read.

Caffeine Cache – A high-performance, in-memory caching library that was quick to integrate and significantly improved response times for repeated line requests.

(Testing) JUnit & Mockito – Used for writing unit tests and mocking file system operations. This combination made it straightforward to cover base test scenarios and simulate edge cases.

### 7 - How long did you spend on this exercise? If you had unlimited more time to spend on this, how would you spend it and how would you prioritize each item?

I have spent about 4 days on the project - starting with a sample draft where the full file was written and getting performance times below what was expected.
Given the challenges encountered when facing a larger file, the solution of adding an index was required to improve it until a satisfactory result was achieved.
The last day was mostly used to think about the solution as whole and make last minute changes that would be critical to the processing and presentation of the application.

If I had unlimited time I would strongly focus on improving performance, given that the time to index the file is quite high 
(which could be solved by splitting the index files into multiple smaller files) and making this process run on service start (blocking requests until the service was fully ready to operate).
Other themes that can be touched upon are the increased testing coverage and resilience/error handling - the current solution covers the solution as possible given the time spent on development.

### 8 - If you were to critique your code, what would you have to say about it?

Starting with the things that went well, I believe that most of the code structure is quite well divided between operations and concepts which allows for a good readability and future maintainability.
Additionally, the asynchronous indexing done on startup allows the service to start right away (sadly at the cost of the performance of the requests during it's execution) and the use of the Caffeine cache demonstrate an increased focus on boosting the performance of the application.

As far as areas of improvement, the current solution is not performing as well as desired for a file of 10GB (and for larger files). Large changes would have to be performed in order to improve the solution.
Additionally the concurrency during indexing is not performing as gracefully as expected, this needs quite sizable changes in order to be improved.
With least priority, increasing the test coverage/quality in addition to changing some of the variables to be more flexible is something that can be improved.

---

## 🔨 Troubleshooting

### ✅ Install Java (if you don't have it)

1. Download JDK from https://www.azul.com/downloads/?package=jdk#zulu
2. Unzip the portable solution
3. Set `JAVA_HOME` on bashrc as the example

      ```bash
      export JAVA_HOME="/c/dev/java_folder"
      export PATH="$JAVA_HOME/bin:$PATH"
      ```

4. Check the java version

      ```bash
      java -version
      ```

---

### ✅ Install Maven (if you don't have it)

1. Download Maven from: https://maven.apache.org/download.cgi
2. Unzip the portable solution
3. Set `MAVEN_HOME` on bashrc as the example

   ```bash
   export MAVEN_HOME="/c/dev/maven_folder"
   export PATH="$MAVEN_HOME/bin:$PATH"
   ```
