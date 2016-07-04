# Engine

* [Engine overview](00_Engine_overview.md)
* [Crawler component](component-crawler.md)
* [Heritrix configuration](Heritrix_configuration.md)

---

* [Native Mysql usage](Native_mysql_usage.md)

---

* [Engine Data model](Engine_data_model.md)

---

* [Process Remarks codes](Process_remarks_codes.md)

---

##  Code Structure for Rest API

All is in the `engine` directory.

### Basic structure

All usual components (inspired from JHipster) related to the data model and its layers

* `asqatasun-api`
* `asqatasun-engine`
* `asqatasun-model`
* `asqatasun-persisitence`
* `asqatasun-resources`
* `asqatasun-services`
* `asqatasun-web` (the Rest API itself)

### Engine (business) components

... those described in [Engine overview](00_Engine_overview.md)

1. `crawler`
2. `contentLoader`
3. `contentAdapter`
4. `processor`
5. `consolidator`
6. `analyzer`

### Other technical compenents

* `commons-processing` utils: generate processResults, processRemarks & EvidenceElements
* `scenarioLoader`
* `rule-implementation-loader` dynamic load of rules classes from the DB ; could be improved in some way
* `nomenclatureLoader` utils to load nomenclatures (nomenclature = tool to deal with lists of anything, like whitelists and blacklists)

### Build

```sh
mvn clean install
```

Create a DB (say user is `asqatasun` and DB is `asqatasun5api`

```sql
CREATE DATABASE IF NOT EXISTS `asqatasun5api` CHARACTER SET utf8;
GRANT ALL PRIVILEGES ON `asqatasun5api` . * TO 'asqatasun'@'localhost';
FLUSH PRIVILEGES;
```

Then

```sh
mkdir /tmp/asqatasun5api
cd /tmp/asqatasun5api
mkdir conf
cd conf
cp /etc/asqatasun/asqatasun.conf asqatasun.properties
```

* adjust SQL credentials (yes, do it, especially if you already had an Asqatasun before, change the DB name)
* Go back to Asqatasaun source dir (where you cloned)

Then do

```sh
mvn  -DconfDir=/tmp/asqatasun5api clean install
```

and 

```sh
cd asqatasun-web
java -Dserver.port=4242 \
    -DconfDir=/tmp/asqatasun5api \
    -jar target/asqatasun-web-4.1.0-SNAPSHOT.jar
```

then browse http://localhost:4242/swagger-ui.html

You can also have a look at 

http://localhost:4242/criterions
http://localhost:4242/themes
http://localhost:4242/references