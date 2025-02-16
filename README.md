# Network Connection Summary

## 🔍 Project Overview
This project is a **command-line tool** that allows users to analyze **network connections** for a given process ID (PID).  
The tool extracts active **TCP and UDP connections**, displays important details like **local/foreign addresses, connection states, and direction**,  
and sorts the output **by protocol** for better readability.

### ✨ Why This Project?
- **Lightweight & Fast:** A simple tool to analyze network connections without heavy dependencies.
- **Portable Executable:** Uses **GraalVM** to generate a **native binary**, so it runs **without requiring Java installed**.
- **Cross-Platform:** Works on **macOS & Linux** using `lsof` for network analysis.

---

## 🚀 Important Things to Remember Before Compiling
### **1️⃣ Install GraalVM**
Ensure you have **GraalVM installed** and set as your Java runtime:
```sh
java -version
```
Expected output:
```
openjdk 21.0.2 2024-01-16
GraalVM CE 21.0.2
```

If `GraalVM` is not installed, download and install it from: [GraalVM Releases](https://www.graalvm.org/downloads/)

Then, install `native-image` (if missing):
```sh
gu install native-image
```

### **2️⃣ Ensure Maven is Installed**
Check if Maven is installed:
```sh
mvn -version
```
If not installed, install it via:
```sh
brew install maven  # macOS
sudo apt install maven  # Ubuntu/Linux
```

### **3️⃣ Verify `lsof` is Available**
Ensure `lsof` is installed to fetch network connection data:
```sh
which lsof  # Should return a valid path
```

If missing, install it:
```sh
brew install lsof  # macOS
sudo apt install lsof  # Ubuntu/Linux
```

---

## 🛠️ Compiling the Code
To **compile and generate a native binary**, run:
```sh
mvn clean package -Dpackage=native-image -Pnative
```

After a successful build, the **native executable binary** will be available inside:
```
target/network-connections
```

Run it using:
```sh
./target/network-connections
```

---

## 🏆 Why GraalVM?
GraalVM makes it **super easy** to convert Java-based command-line tools into native binaries.  

### **✅ Benefits of GraalVM:**
✔ **No Java Runtime Needed:** The generated binary **runs on any machine** (even without Java installed).  
✔ **Fast Execution:** Native binaries are significantly **faster** than running Java via JVM.  
✔ **Low Memory Usage:** Since it doesn’t require a JVM, memory consumption is minimal.  
✔ **Better Security:** A native binary **does not expose Java bytecode**, reducing attack vectors.  

With GraalVM, we can **write once, compile once, and run anywhere**—without requiring users to have Java! 🎯  

---

## 📜 License
This project is open-source and can be modified or extended as needed.

