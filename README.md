# Northwind Manager
 
Applicazione web CRUD per la gestione dei fornitori del database Northwind, realizzata con Java Servlet, Hibernate e JavaScript.
 
---
 
## Come avviare il progetto
 
### 1. Apri il repository su GitHub
Vai sulla repository GitHub del progetto e clicca sul pulsante verde **"Code"**, poi seleziona **"Open with Codespaces"** → **"New codespace"**.
 
### 2. Aspetta che il Codespace si avvii
Ci vorranno circa 30 secondi. Una volta pronto vedrai l'editor VS Code nel browser.
 
### 3. Imposta Java 17
Nel terminale in basso, lancia:
```bash
sudo apt-get install -y openjdk-17-jdk
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```
 
### 4. Entra nella cartella del progetto
```bash
cd northwind-manager
```
 
### 5. Avvia il server
```bash
mvn jetty:run
```
Maven scaricherà le dipendenze la prima volta (può richiedere qualche minuto). Aspetta finché non vedi nel log:
```
[INFO] Started Server
```
 
### 6. Apri l'applicazione nel browser
GitHub Codespaces mostrerà un popup in basso a destra: **"Open in Browser"** sulla porta `8080`. Cliccaci sopra.
 
Si aprirà direttamente la pagina di gestione fornitori.
 
---
 
## Per fermare il server
Nel terminale premi `Ctrl + C`.