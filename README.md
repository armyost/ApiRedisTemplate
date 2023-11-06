# Redis Cache Strategy Sample

## OverView
This project shows two types of cache stragety. One is "LookAside", Another is "WriteBack".

## Installation
> ./gradlew build --exclude-task test
> java -jar .\build\libs\demo-0.0.1-SNAPSHOT.jar

## Architecture
![appmap](/img/1.png)

## Explanation
### "DemoLookAside" is look-aside strategy API.
- It will read a data from cache first. When there is no data matched in cache, It will retreive the data from database.

### "DemoWriteBack" is write-through strategy API.
- It write on redis first. And scheduler will sync the data to database. ( Application → redis  → database )
- It supports Read-Through strategy also.
- In redis cache store, there are 2 types of data. One is "SET TYPE : sample:write_back" which contains datas to write on database. Another is "STRING TYPE" which will stay at cache-store for read-Through.

![redis-cache-readthrough](/img/3.png)
![redis-cache-writeback](/img/2.png)