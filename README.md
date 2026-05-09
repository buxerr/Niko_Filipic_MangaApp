# MangaApp

JavaFX aplikacija za temu 39: Manga / Stripovi.

Aplikacija omogucuje administratoru upravljanje katalogom manga/stripova, a korisniku pregled i pretrazivanje kataloga. Podaci se spremaju u PostgreSQL bazu i aplikacija s bazom komunicira kroz repository sloj i SQL funkcije/procedure.

## Tehnologije

- Java 25
- JavaFX
- Maven
- PostgreSQL 16
- Docker Compose
- JDBC
- Jakarta XML Binding
- SLF4J + Logback

## Pokretanje baze

Iz root direktorija projekta pokrenuti:

```powershell
docker compose down -v
docker compose up -d
docker logs mangaapp-postgres
```

U logu ne smije biti SQL errora. `NOTICE: table ... does not exist, skipping` je normalno kod prvog pokretanja jer skripta koristi `DROP TABLE IF EXISTS`.

Baza se pokrece na:

```text
localhost:5433
```

Podaci za spajanje nalaze se u:

```text
src/main/resources/hr/algebra/mangaapp/config.xml
```

## Pokretanje aplikacije

Projekt se moze pokrenuti iz IntelliJ IDEA kao JavaFX/Maven projekt.

Glavna klasa za pokretanje je:

```text
hr.algebra.mangaapp.AppLauncher
```

JavaFX aplikacija ucitava pocetni login ekran:

```text
/hr/algebra/mangaapp/view/login.fxml
```

Ako je Maven dostupan u terminalu, aplikacija se moze pokrenuti i kroz:

```powershell
mvn javafx:run
```

## Login podaci

Inicijalni administrator:

```text
username: admin
password: admin
```

Admin lozinka je spremljena kao SHA-256 hash u inicijalnoj SQL skripti.

Obicni korisnik se moze registrirati kroz Register ekran. Novi korisnici dobivaju rolu `USER`.

## Role

USER:

- vidi Home ekran
- moze pretrazivati katalog
- moze pregledati detalje mange/stripa
- ne vidi Manage meni
- ne vidi Admin meni

ADMIN:

- vidi Home ekran
- vidi Manage meni
- vidi Admin meni
- moze dodavati, uredjivati i brisati mange/stripove
- moze upravljati zanrovima, autorima, izdavacima i likovima
- moze pokrenuti XML export
- moze obrisati sve podatke kroz admin funkciju

## Glavne funkcionalnosti

- Login i registracija korisnika
- Role-based prikaz izbornika
- Home pretraga manga/stripova
- CRUD za mangu/strip
- CRUD za zanrove
- CRUD za autore
- CRUD za izdavace
- CRUD za likove
- Povezivanje mange s autorima, zanrovima, izdavacem i likovima
- Drag and drop dodavanje lika u mangu/strip
- Prikaz naslovnice u Manga ekranu
- Prikaz naslovnice u Home detaljima
- Online JSON import manga podataka kroz Jikan API
- XML export kataloga manga/stripova po odabranom autoru
- Statistics popup
- Logiranje kroz SLF4J/Logback
- XML action log korisnickih akcija

## Home search

Home ekran podrzava pretragu po:

```text
title
genre
author
publisher
status
```

Odabirom mange u tablici prikazuju se detalji, povezani autori, zanrovi, likovi i cover image ako postoji ispravna putanja.

## Manga management

Administrator kroz Manage -> Manga moze:

- dodati mangu/strip
- urediti mangu/strip
- obrisati mangu/strip
- postaviti title, synopsis, release year, volumes, status i imagePath
- povezati publishera
- povezati autore
- povezati zanrove
- povezati likove
- dodati lika drag-and-drop akcijom iz Available characters u Selected characters

Nakon dodavanja ili uredjivanja, relacije se spremaju u tablice:

```text
manga_author
manga_genre
manga_character
```

## Cover slike

Slike naslovnica nalaze se u:

```text
assets/covers
```

U bazi se sprema relativna putanja, npr.:

```text
assets/covers/berserk.jpg
```

Ako putanja nije ispravna, aplikacija ne puca nego samo ne prikazuje sliku.

## XML export

XML export se pokrece kroz:

```text
Admin -> Export XML Catalog
```

Flow:

1. Odabere se autor.
2. Odabere se lokacija za spremanje XML datoteke.
3. Aplikacija eksportira sve mange/stripove tog autora.

Sam JAXB export izvodi se kroz JavaFX `Task`, pa ne blokira JavaFX Application Thread.

XML sadrzi:

- author atribut na catalog elementu
- title
- description
- releaseYear
- volumes
- status
- publisher
- imagePath
- authors
- genres
- characters

Implementacija se nalazi u:

```text
src/main/java/hr/algebra/mangaapp/xml/MangaXmlExportService.java
src/main/java/hr/algebra/mangaapp/xml/dto
```

## Online JSON import

Online import se pokrece kroz:

```text
Admin -> Import Data
```

Import koristi besplatni Jikan REST API:

```text
https://api.jikan.moe/v4/top/manga?type=manga&limit=5
```

Aplikacija kroz JavaFX `Task` u pozadini:

- preuzima JSON podatke o top mangama
- kreira izdavace prema Jikan serialization podacima
- kreira autore
- kreira zanrove
- preuzima likove za mangu
- preuzima cover slike u `assets/covers`
- sprema mangu i relacije kroz repository sloj

Postojeci naslovi se preskacu da se ne stvaraju duplikati.

## Baza podataka

Inicijalizacijske skripte nalaze se u:

```text
database/init
```

Redoslijed:

```text
01_create_tables.sql
02_create_functions.sql
03_insert_admin.sql
05_insert_test_data.sql
```

Glavne tablice:

```text
publisher
genre
author
story_character
app_user
manga
manga_genre
manga_author
manga_character
```

Brisanje svih aplikacijskih podataka radi kroz proceduru:

```sql
CALL sp_clear_all_data();
```

Procedura nakon brisanja ponovno kreira admin korisnika s hashiranom lozinkom.

## Repository sloj

Repository interfejsi nalaze se u:

```text
src/main/java/hr/algebra/mangaapp/repository
```

SQL implementacije nalaze se u:

```text
src/main/java/hr/algebra/mangaapp/repository/sql
```

Kontroleri koriste `RepositoryFactory`, npr.:

```java
RepositoryFactory.getMangaRepository()
```

## Smoke test

Repository smoke test nalazi se u:

```text
src/main/java/hr/algebra/mangaapp/RepositorySmokeTest.java
```

Test provjerava osnovni rad:

- Genre CRUD/search
- Publisher CRUD/search
- Author CRUD/search
- StoryCharacter CRUD/search
- User create/find/exists
- Manga create/find/update/search/delete
- Manga relations

Pokrenuti ga nakon clean Docker inicijalizacije.

## Logovi

Logback konfiguracija:

```text
src/main/resources/logback.xml
```

Logovi se zapisuju u:

```text
logs/mangaapp.log
```

Logiraju se login/logout, view loading greske, XML export i vazne aplikacijske greske.

XML action log korisnickih akcija zapisuje se u:

```text
logs/action-log.xml
```

U XML log se zapisuju akcije poput:

- login success/failure
- registracija korisnika
- logout i exit
- otvaranje glavnih ekrana
- statistics popup
- admin clear data
- online JSON import success/failure/cancel
- XML export success/failure/cancel

## Poznata ogranicenja

- Cover slika se bira rucnim unosom relativne putanje, bez FileChooser dijaloga.
- RepositorySmokeTest je pomocni test za provjeru repository/database sloja.
