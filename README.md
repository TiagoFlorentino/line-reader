# 📝 Line Reader

A Spring Boot REST API that serves individual lines from a immutable text file.  
This service is designed to handle multiple client requests simultaneously and to support any size file.

- Collects a line from a text file
- Line collection from a GET endpoint `/lines/<line index>`
- OpenAPI documentation (Swagger)
- Global exception handling
- Parameter validation and error responses (`200`, `413`, `500`)

---

## ⚠️ Requirements

- Java 17 & Apache Maven (check troubleshooting for installation steps)

---

## ❓ Defined Questions

### 1 - 

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
