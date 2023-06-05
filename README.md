# ISS

# Aplicația Bibliotecă

Această aplicație rezolvă problema gestionării unei biblioteci, oferind funcționalități pentru abonați și bibliotecari. Abonații pot căuta și împrumuta cărți, iar bibliotecarii se ocupă de gestionarea cărților și a împrumuturilor.

## Conținut 
1. Introducere
2. Tehnologii utilizate
3. Utilizare
4. Arhitectură
5. Diagrame UML

## Introducere

Biblioteca este o aplicație care permite abonaților săi să acceseze și să împrumute cărți disponibile în bibliotecă. Fiecare abonat are un cod unic de identificare și poate:
- vizualiza cărțile disponibile, 
- împrumuta cărți,
- vizualiza lista cărților împrumutate până în prezent și data la care trebuie să le returneze.

Bibliotecarii au acces la funcționalități de gestionare a cărților și a abonaților. Ei pot:
- adăuga, 
- actualiza și 
- șterge cărți din baza de date a bibliotecii, respectiv pot
- înregistra abonați și
- returna cărți.

## Tehnologii utilizate

Aplicația este dezvoltată utilizând următoarele tehnologii și biblioteci:

- `Java` - limbajul de programare principal
- `Hibernate ORM` - bibliotecă pentru interacțiunea cu baza de date prin intermediul unei biblioteci ORM
- `JavaFX` - framework pentru dezvoltarea interfeței grafice
- `RPC` (Remote Procedure Call) - protocol pentru comunicarea între client și server
- `Gradle` - sistem de automatizare a construirii proiectului și gestionare a dependențelor

## Utilizare

### Autentificare
Pentru a utiliza aplicația, trebuie să te autentifici ca abonat sau ca bibliotecar.

- Pentru autentificarea *abonaților*, se vor introduce numele de utilizator și parola în formularul de autentificare.
- Pentru autentificarea *bibliotecarilor*, se vor introduce numele de utilizator și parola în formularul de autentificare, respectiv o cheie de verificare suplimentară.

### Funcționalități abonați
După autentificare, abonații au acces la următoarele funcționalități:

- Vizualizare cărți disponibile: aceste cărți vor apărea într-un tabel de la pornirea aplicației și va fi actualizată în timp real.
- Împrumut cărți: abonații pot solicita împrumutul unei cărți disponibile. 
- Vizualizare cărți împrumutate: abonații pot vedea lista cărților pe care le-au împrumutat și data la care trebuie să le returneze.

### Funcționalități bibliotecari
După autentificare, bibliotecarii au acces la următoarele funcționalități:

- Gestionare cărți: bibliotecarii pot `adăuga`, `actualiza` și `șterge` cărți din baza de date a bibliotecii.
- Gestionare abonați: bibliotecarii pot înregistra abonați. Pentru fiecare abonat, se rețin informații precum CNP, nume, adresă și număr de telefon (plus nume de utilizator și parolă).

## Arhitectura aplicației
### Subproiecte

``` python 
Domain
```
Acest subproiect conține următoarele clase:
- `Book`: reprezintă o carte din bibliotecă.
- `BookLoan`: reprezintă un împrumut de carte.
- `Librarian`: reprezintă un bibliotecar.
- `Reader`: reprezintă un abonat al bibliotecii.
- `Status` (enum): definește starea unei cărți (disponibilă, împrumutată), respectiv starea unui împrumut (încă în desfășurare, returnat).
- `Pair`: o pereche formată dintr-un abonat și o carte.
- `PairLibrarianLoan`: o pereche formată dintr-un bibliotecar și un id de împrumut.

``` python 
LibrarianFX
```
Acest subproiect conține o interfață grafică și următoarele controlere:

- `LibrarianController`: gestionează operațiunile efectuate de un bibliotecar.
- `ReaderController`: gestionează operațiunile efectuate de un abonat.
- `ManagementController`: gestionează operațiunile de administrare a cărților (adăugare, actualizare și ștergere).
- `LoginController`: gestionează operațiunile de autentificare.

``` python 
Networking
```
Acest subproiect conține codul pentru comunicarea între server și client prin RPC (Remote Procedure Call).

``` python 
Persistence
```
Acest subproiect conține clasele pentru gestionarea persistenței datelor, folosind o bibliotecă ORM (Object-Relational Mapping) - Hibernate. 

Conține următoarele interfețe și implementări:

- `IRepository`: interfață generică pentru operațiile de bază pe entități.
- `IBookRepository`: extinde IRepository și gestionează operațiunile specifice cărților.
    - `BookDBIRepository`: implementare a interfeței IBookRepository folosind Hibernate.
- `IBookLoanRepository`: extinde IRepository și gestionează operațiunile specifice împrumuturilor de cărți.
    - `BookLoanDBIRepository`: implementare a interfeței IBookLoanRepository folosind Hibernate.
- `ILibrarianRepository`: extinde IRepository și gestionează operațiunile specifice bibliotecarilor.
    - `LibrarianDBIRepository`: implementare a interfeței ILibrarianRepository folosind Hibernate.
- `IReaderRepository`: extinde IRepository și gestionează operațiunile specifice abonaților.
    - `ReaderDBIRepository`: implementare a interfeței IReaderRepository folosind Hibernate.

``` python 
ServerRPC
```
Acest subproiect conține clasa `StartRpcServer` pentru pornirea serverului și implementarea concretă a serviciilor aplicației în clasa `ServicesImpl`.

``` python 
Services
```
Acest subproiect conține următoarele componente:

- `IService`: interfață pentru serviciile aplicației.
- `IObserver`: interfață pentru gestionarea notificărilor între utilizatori.
- `MyException`: clasă de excepții personalizată pentru aplicație.

## Diagrame
Pentru vizualizarea diagramelelor, accesați folderul [Faza 3/DiagrameISS.mdj](https://github.com/MartinFabianIonut/ISS/blob/main/Faza%203/DiagrameISS.mdj).

# Cuprins:

## 1. Diagrama cazurilor de utilizare

Se află în dosarul *UML functional*, sub denumirea "Biblioteca - UML functional".

## 2. Diagrama de clase

Se află în dosarul *UML conceptual*, sub denumirea "Biblioteca - UML conceptual rafinat".

## 3. Diagramele de secvență
Toate se află în directorul *UML conceptual*, cu următoarele căi:

- SD Cit Login -> `SD - autentificare cititor`.
- SD Bib Login -> `SD - autentificare bibliotecar`.
- SD Cit Available -> `SD - cititorul vede cărțile disponibile`.
- SD Cit Borrowed -> `SD - cititorul vede cărțile împrumutate`.
- SD Bib Available -> `SD - bibliotecarul vede cărțile disponibile`.
- SD Bib Borrowed -> `SD - bibliotecarul vede cărțile împrumutate`.

## 4. Diagramele de comunicare
Toate se află în directorul *UML conceptual*, cu următoarele căi:
- CD Cit Loan -> `CD - cititorul împrumută o carte`.
- CD Bib Return -> `CD - bibliotecarul returnează o carte`.
- CD Bib Add -> `CD - bibliotecarul adaugă o carte`.
- CD Bib Update -> `CD - bibliotecarul actualizează o carte`.
- CD Bib Delete -> `CD - bibliotecarul șterge o carte`.
- CD Bib Register -> `CD - bibliotecarul înregistrează un cititor`.

*! Toate diagramele de comunicare, cu excepția ultimei, prezintă și o variantă Observer, în care sunt adăugate și celelalte controllere. Deoarece notificarea făcută de server este dinamică și se propagă la toți utilizatorii conectați, în situația în care avem atât abonați, cât și bibliotecari conectați, toți vor trebui să vadă modificările efectuate în timp real.*
