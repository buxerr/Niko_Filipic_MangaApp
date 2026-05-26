# MangaApp

JavaFX aplikacija za temu **39: Manga / Stripovi**.

## Potrebno

- Java 25
- Maven
- Docker Desktop

## Baza

Pokretanje PostgreSQL baze:

```powershell
docker compose up -d
```

Reset baze i ponovno ucitavanje inicijalnih skripti:

```powershell
docker compose down -v
docker compose up -d
```

Baza:

```text
host: localhost
port: 5433
database: mangaapp
username: postgres
password: postgres
```

SQL skripte se automatski ucitavaju iz:

```text
database/init
```

## Pokretanje aplikacije

Iz IDE-a pokrenuti glavnu klasu:

```text
hr.algebra.mangaapp.AppLauncher
```

Ili iz terminala:

```powershell
mvn javafx:run
```

## Login

Administrator:

```text
username: admin
password: admin
```

Obicni korisnik se registrira kroz **Register** ekran i dobiva rolu `USER`.

## Funkcionalnosti

- Login i registracija
- Home pregled i pretraga manga/stripova
- CRUD za mangu, autore, zanrove, izdavace i likove
- Drag and drop dodavanje lika u mangu
- Admin brisanje svih podataka
- XML export kataloga po autoru
- Cover slike u `assets/covers`
