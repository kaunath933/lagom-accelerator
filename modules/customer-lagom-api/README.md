#Common-lagom-api
The common-lagom-api provides four REST endpoints
* Get the details of all customers in the database   
```bash
http://localhost:9000/api/details/get
```

* Get the details of a customer using customer id 
```bash
http"//localhost:9000/api/details/get/:id
```

* Add a customers details in customer database 
```bash
curl --location --request POST 'http://localhost:9000/api/details/add/' \ --header 'Content-Type: application/json' \ --data-raw '{ "id":"002", "name":"seafold", "email":"av@gmail.com" }'
```
* Delete a customer details from database using customer id
```bash
http://localhost:9000/api/delete/:id
```