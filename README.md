# REST-measurement-service

### Simple rest client which can be used to store data of measurements from temperature sensors. If measure will be higher than normal, logger will write in log file about it. If current measure is equal to previous, it wouldn't be saved to DB. Database stores sensor id, measurement value and time of each measurement.
### As database i used MySQL server with one simple one-to-many relashionship 
also added my arduino code which measures temperature and send it to API

to start the project with Docker you need to run this command from root directory : 
> docker-compose up

## Main requests

### (POST) Register new sensor : 
> http://localhost/api/register
### 
return example : 
> { <br>
> &nbsp;&nbsp;"Sensor name": "Aragorn"<br>
> &nbsp;&nbsp;"Sensor id": 7<br>
> }
### if you will register like this, API will generate name for sensor by itslef, but if request will have a param named "name", it will safe yours.
> http://localhost:8080/api/register?name=newName will return : <br>
> {<br>
> &nbsp;&nbsp;"sensor name": "newName",<br>
> &nbsp;&nbsp;"sensor id": 1<br>
> }


### (POST) Save new measure. Returns a boolean value, although if the data is stored.
> http://localhost/api/save
#### RequestBody example
> {<br>
> &nbsp;&nbsp;"sensor" : 7,<br>
> &nbsp;&nbsp;"value" : 21<br>
> }

### (GET) get all measurements by sensor : 
> http://localhost/api/getMeasure/{put_your_id_here}
### 
return example : 
> [<br>
> &nbsp;&nbsp;{<br>
> &nbsp;&nbsp;&nbsp;&nbsp;"value": 21,<br>
> &nbsp;&nbsp;&nbsp;&nbsp;"localDateTime": "2022-10-04T10:00:35",<br>
> &nbsp;&nbsp;&nbsp;&nbsp;"sensor": 6<br>
> &nbsp;&nbsp;},<br>
> &nbsp;&nbsp;{<br>
> &nbsp;&nbsp;&nbsp;&nbsp;"value": 2,<br>
> &nbsp;&nbsp;&nbsp;&nbsp;"localDateTime": "2022-10-04T10:00:40",<br>
> &nbsp;&nbsp;&nbsp;&nbsp;"sensor": 6<br>
> &nbsp;&nbsp;}<br>
> ]
