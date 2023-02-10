# Hautausmaakierros-backend 

Projekti on tehty syvent�vien opintojen harjoitusty�n�. Backendin lis�ksi on olemassa frontend sovellus (github link).

## Backendin kuvaus:
- spring boot (hibernate)
- postgresql
- azure

Backend on tehty Spring Bootilla sek� Spring Securityll�, eli sovelluksessa on oma API ja k�ytt�j�n autentikointi+authorisointi.
Java-kirjastojen hallintaan k�ytet��n mavenia. 

### Auth yleisesti
Autentikaatioon k�ytet��n JWT-tokeneita, access + refresh. Refresh token haetaan kun access token menee vanhaksi.
Refersh token haetaan AuthorizationController.java luokan kautta. 

Tunnuksen luontiin oli tarkoitus luoda s�hk�postiin tuleva 
aktivointilinkki, mutta sit� ei ehditty toteuttamaan t�m�n projektin aikana (AppUserController.java:registerUser() & activateUser()).

BCryptPasswordEncoder hoitaa salasanan kryptaukset, joten tietokannassa ei ole puhtaita salasanoja tallennettuna.

### API-reittien suojauksien hallinta
API-reittien suojauksia hallitaan SecurityConfig.java luokassa. Esimerkiksi:

<code>
http.authorizeRequests().antMatchers("/api/v1/route/public").permitAll();
</code>

sallii kaikki pyynn�t /route/public osoitteeseen, kun taas

<code>
http.authorizeRequests().antMatchers("/api/v1/user/save/**").hasAnyAuthority(ADMIN);
</code>

sallii vain k�ytt�j�t, joilla on ADMIN rooli osoitteeseen /user/save/...

### Miten API-reitit toimivat
API-reitit hakevat tietoja tietokannasta niille injektoiduilla palveluilla, kuten RouteService.java.
Palveluluokat k�ytt�v�t hyv�kseen repository Interfaceja, joissa kuvaillaan niille sallitut metodit, esim. findAppRoleByName().
Kaikkia metodeja ei tarvitse kuvailla t�ss�, vaan perustoiminnot kuten findById ja save l�ytyy JpaRepository luokan kautta, jota t�m�n projektin repositoryt extendaavat.

### Tietokantayhteys
Tietokantayhteys m��ritell��n application.properties tiedostossa. Sovellus on tarkoitettu toimivaksi postgresql kantaa vasten.

### Azure
Kaikki sovelluksen osat on pystytetty Azureen, mutta ne voi olla my�s paikallisia deploymentteja jatkossa.
Tietokanta on luotu oletusarvoilla Azure Database for PostgreSQL palvelulla. 

Backend sovellus py�rii Azure App Servicen avulla osoitteessa https://fmprojectbackendrmdev.azurewebsites.net.
Jos sit� haluaa jatkossa k�ytt��, niin pom.xml tiedostoon pit�� muuttaa <code><groupId>com.microsoft.azure</groupId></code> kohtaan oman subscriptionin ID, resource group ja App Servicen nimi.

Sovellus on viety manuaalisesti (ei pipelinea) Azure App Serviceen komennolla 
<code>mvn package com.microsoft.azure:azure-webapp-maven-plugin:2.6.1:deploy</code>

### Kehitysymp�rist�n pystytys
Suositeltu IDE: IntelliJ IDEA

Jos projektin avaa ensimm�ist� kertaa IDEAssa, anna rauhassa indeksoida. IDEA on my�s luonut automaattisesti Run Configurationin t�lle Spring
Boot sovellukselle. Jos ei, niin luo sellainen (https://www.jetbrains.com/help/idea/run-debug-configuration-spring-boot.html).

Sen j�lkeen m��rit� tietokantayhteys application.properties tiedostoon jdbc muodossa. Korvaa {} sulkujen sis�ll� olevat arvot oman tietokantasi arvoilla. 
Lis�apua: https://www.codejava.net/frameworks/spring-boot/connect-to-postgresql-database-examples.

Sen j�lkeen aja <code>mvn install</code>, jolloin projekti asentuu ja sen voi k�ynnist�� joko IDEAn Run Configuraatiosta tai manuaalisesti /target kansiosta l�ytyv�st� .jar tiedostosta.

Nyt sovellus toimii API-palvelimena, voit kokeilla pyynt�j� esim. selaimella menem�ll� localhost:8080/api/v1/routes/public.

### Jatkokehitys
- S�hk�postiaktivointi (AppUserController.java).
- PostGIS lis�osan k�ytt��notto -> mahdollistaa paikkatiedon paremman k�yt�n.
- Yksikk�testej�
