# Hautausmaakierros-backend 

Projekti on tehty syventävien opintojen harjoitustyönä. Backendin lisäksi on olemassa frontend sovellus (github link).

## Backendin kuvaus:
- spring boot (hibernate)
- postgresql
- azure

Backend on tehty Spring Bootilla sekä Spring Securityllä, eli sovelluksessa on oma API ja käyttäjän autentikointi+authorisointi.
Java-kirjastojen hallintaan käytetään mavenia. 

### Auth yleisesti
Autentikaatioon käytetään JWT-tokeneita, access + refresh. Refresh token haetaan kun access token menee vanhaksi.
Refersh token haetaan AuthorizationController.java luokan kautta. 

Tunnuksen luontiin oli tarkoitus luoda sähköpostiin tuleva 
aktivointilinkki, mutta sitä ei ehditty toteuttamaan tämän projektin aikana (AppUserController.java:registerUser() & activateUser()).

BCryptPasswordEncoder hoitaa salasanan kryptaukset, joten tietokannassa ei ole puhtaita salasanoja tallennettuna.

### API-reittien suojauksien hallinta
API-reittien suojauksia hallitaan SecurityConfig.java luokassa. Esimerkiksi:

<code>
http.authorizeRequests().antMatchers("/api/v1/route/public").permitAll();
</code>

sallii kaikki pyynnöt /route/public osoitteeseen, kun taas

<code>
http.authorizeRequests().antMatchers("/api/v1/user/save/**").hasAnyAuthority(ADMIN);
</code>

sallii vain käyttäjät, joilla on ADMIN rooli osoitteeseen /user/save/...

### Miten API-reitit toimivat
API-reitit hakevat tietoja tietokannasta niille injektoiduilla palveluilla, kuten RouteService.java.
Palveluluokat käyttävät hyväkseen repository Interfaceja, joissa kuvaillaan niille sallitut metodit, esim. findAppRoleByName().
Kaikkia metodeja ei tarvitse kuvailla tässä, vaan perustoiminnot kuten findById ja save löytyy JpaRepository luokan kautta, jota tämän projektin repositoryt extendaavat.

### Tietokantayhteys
Tietokantayhteys määritellään application.properties tiedostossa. Sovellus on tarkoitettu toimivaksi postgresql kantaa vasten.

### Azure
Kaikki sovelluksen osat on pystytetty Azureen, mutta ne voi olla myös paikallisia deploymentteja jatkossa.
Tietokanta on luotu oletusarvoilla Azure Database for PostgreSQL palvelulla. 

Backend sovellus pyörii Azure App Servicen avulla osoitteessa https://fmprojectbackendrmdev.azurewebsites.net.
Jos sitä haluaa jatkossa käyttää, niin pom.xml tiedostoon pitää muuttaa <code><groupId>com.microsoft.azure</groupId></code> kohtaan oman subscriptionin ID, resource group ja App Servicen nimi.

Sovellus on viety manuaalisesti (ei pipelinea) Azure App Serviceen komennolla 
<code>mvn package com.microsoft.azure:azure-webapp-maven-plugin:2.6.1:deploy</code>

### Kehitysympäristön pystytys
Suositeltu IDE: IntelliJ IDEA

Jos projektin avaa ensimmäistä kertaa IDEAssa, anna rauhassa indeksoida. IDEA on myös luonut automaattisesti Run Configurationin tälle Spring
Boot sovellukselle. Jos ei, niin luo sellainen (https://www.jetbrains.com/help/idea/run-debug-configuration-spring-boot.html).

Sen jälkeen määritä tietokantayhteys application.properties tiedostoon jdbc muodossa. Korvaa {} sulkujen sisällä olevat arvot oman tietokantasi arvoilla. 
Lisäapua: https://www.codejava.net/frameworks/spring-boot/connect-to-postgresql-database-examples.

Sen jälkeen aja <code>mvn install</code>, jolloin projekti asentuu ja sen voi käynnistää joko IDEAn Run Configuraatiosta tai manuaalisesti /target kansiosta löytyvästä .jar tiedostosta.

Nyt sovellus toimii API-palvelimena, voit kokeilla pyyntöjä esim. selaimella menemällä localhost:8080/api/v1/routes/public.

### Jatkokehitys
- Sähköpostiaktivointi (AppUserController.java).
- PostGIS lisäosan käyttöönotto -> mahdollistaa paikkatiedon paremman käytön.
- Yksikkötestejä
