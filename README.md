### Relazione Progetto Programmazione 2

Il progetto richiede di realizzare un’interfaccia SecureDataContainer che  rappresenta una collezione di dati di tipo generico E, a cui possono accedere solo utenti provvisti di nome_utente e password e autorizzati, quindi ogni utente potrà vedere/agire solo dati per i quali è stata inserita specifica autorizzazione dell’amministratore.
Oltre a possedere dei dati, i vari utenti della collezione possono copiare un dato, ed avere più copie di quest’ultimo, oppure condividerlo con altri utenti. In questo caso viene condivisa una copia del dato con l’altro utente, e quest’ultima va a finire in una lista di dati condivisi che appartiene all’altro utente. Sta poi a questo utente decidere, se la fonte è affidabile o il dato interessante, accettare la condivisione e porre l’oggetto nella lista di dati appartenenti a lui.
La lista dei dati condivisi non fa parte degli oggetti di un determinato utente A: questo perché chiunque può inserirvi ulteriori dati e, se la lista appartenesse all’utente A, questi non avrebbe alcun controllo sui dati inseriti dagli altri utenti. La lista di condivisione, quindi, permette ad ogni utente di controllare il flusso di dati che riceve, e decidere di accettare solo quelli che gli interessano. 
Tutto il codice è stato opportunamente commentato con Overview, typical element, Funzione ed invariante di rappresentazione per le classi quando necessario, e requires/modifies/effect/throws quando vengono dichiarati i metodi.

Di seguito verrà descritto il funzionamento di ogni classe.

# Data

Rappresenta due insiemi, di oggetti generici di tipo E, definiti come liste. Il primo contiene dati appartenenti ad un utente mentre il secondo dati condivisi con l’utente. Questa classe funziona solo da contenitore. Inoltre la seconda lista è del tipo SharedData.

# SharedData

Rappresenta un oggetto formato da un dato di tipo generico E ed una stringa, che identifica l’utente che ha condiviso il dato.

# User

Rappresenta un utente e ha come valori due stringhe: il nome utente e la password. La password, quando viene chiamato il costruttore, viene criptata usando l’algoritmo MD5. La classe contiene anche il metodo checkHash, che permette di controllare se una determinata password passata per parametro sia uguale a quella salvata. L’algoritmo funziona criptando a sua volta la password passata come parametro e confrontando l’hash così ottenuto con quello salvato. Se i due corrispondono allora la password è corretta, e viene restituito il valore true per consentire l’accesso.

# UserWithData

Rappresenta l’ utente e i dati a lui collegati. La password in questo caso è criptata usando l’algoritmo SHA-256. Per accedere ai dati dell’utente bisogna richiamare il metodo getData  mentre per accedere ai dati condivisi con l’utente il metodo getSharedData. Entrambi richiedono una password come parametro e restituiscono la lista di dati richiesta soltanto se la password corrisponde. Per verificarlo utilizzano lo stesso metodo checkHash della classe User, che, in questa implementazione, è stato reso invece privato.

# Hashing

Questa classe contiene due metodi statici che servono a criptare una password passata come parametro. Un metodo converte usando lo SHA-256 mentre l’altro l’MD5.

# SecureDataContainer

Rappresenta l’interfaccia da implementare. Oltre ad i metodi che sono stati richiesti nella consegna comprende anche metodi get e remove che usano come parametro un indice invece che il dato, un getIterator per i dati condivisi e una funzione per accettare e spostare un dato dalla lista shared a quella di dati proprietari. 

# SecureDataContainerDoubleList

Questa classe è la prima implementazione dell’interfaccia: utilizza due liste, una di User e l’altra di Data di tipo generico E. Ad un determinato indice uguale per le due liste, i dati presenti nella seconda lista corrispondono a quelli dell’utente selezionato nella prima lista. 
Oltre ai metodi dell’interfaccia è stato implementato anche un metodo getUser privato che, dato nome utente e password, restituisce l’oggetto User corrispondente, il tutto per semplificare la scrittura del codice.

# SecureDataContainerList

È la seconda implementazione dell’interfaccia. Utilizza una sola lista di UserWithData, di tipo generico E. Utilizza un metodo privato per rendere più leggibile il codice, getUser, che prende per argomento il nome utente e restituisce l’oggetto UserWithData corrispondente. L’unico parametro necessario è il nome utente perché la password viene controllata quando si accede alle sue risorse con metodi tipo getData o getSharedData.

# Test

Nella batteria di test è stato utilizzato un oggetto SecureDataContainer, chiamato sdcl, creato appositamente ed utilizzato per provare tutti i metodi dell’interfaccia. 
Con un for le prove sono state rese cicliche e ripetute due volte. 
Subito dopo il for un if permette di controllare se si sta eseguendo la prima iterazione, nel qual caso sdcl diventa un oggetto della classe SecureDataContainerList e quindi testa i metodi con questa implementazione. Nella seconda iterazione sdcl è un nuovo oggetto della classe SecureDataContainerDoubleList e testa nuovamente gli stessi metodi di prima. Quindi entrambe le implementazioni vengono testate in modo sequenziale.

# Considerazioni finali

Il codice risulta ottimale per gestire un contenitore di dati che sfrutta credenziali e multiutenza.
La criptazione delle password usando sia l’id utente che la password stessa rende gli hash unici tra utente ed utente che hanno necessariamente nomi diversi ma che potrebbero avere password uguali.
Sarebbe stato preferibile utilizzare bcrypt ma l’assenza di librerie native Java che lo implementano  ha reso più conveniente utilizzare metodi meno potenti ma comunque efficienti come lo SHA-256 e l’MD5. 
Il dividere i dati in condivisi e in proprietari, oltre ad imporre la necessità di avere un account registrato permette di limitare lo spam ed evita che un utente si ritrovi la sua collezione di dati invasa da condivisioni di altri utenti senza che abbia la possibilità di accettare o meno le condivisioni che riceve. 
Tra le due implementazioni sopra riportate si preferisce quella a singola lista perché la classe UserWithData può funzionare anche da sola restando comunque  al 100 % sicura. 
Al contrario, le classi User e Data non possono essere usate da sole, dato che non sono in relazione tra loro a meno che un’altra classe o il programmatore non la crei. E se si usasse un’implementazione diversa da SecureDataContainerDoubleList, non si potrebbe essere sicuri che il metodo checkHash venga richiamato per controllare le credenziali.

# Nota

Progetto compilato con Java 10, ma dovrebbe avere problemi funzionare anche con Java 8. Non utilizza librerie esterne. Bisogna attivare le Assertion per testare il main, che ne fa un uso esaustivo. Per attivare le assertion basta avviare il progetto con il parametro -ea
