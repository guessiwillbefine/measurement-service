# REST-measurement-service

### Simple rest client which can be used to store data of measurements from temperature sensors. If measure will be higher than normal, logger will write in log file about it. If current measure is equal to previous, it wouldn't be saved to DB. Database stores sensor id, measurement value and time of each measurement.
### As a database i used MySQL server with one simple one-to-many relashionship 
also added my arduino code to measure temperature and send it to API

to start the project with Docker you need to run this command from root directory : 
> docker-compose up
