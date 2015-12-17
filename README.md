## Hotel Oasis Booking System
![Image](http://i1030.photobucket.com/albums/y369/MariaPhotoB/Logo_zpsy2ewc8b7.png)


### Description
This web based system has been designed to **manage the bookings and stays** of the *Hotel Oasis* hotel chain.
Through this prototype users can **check the room availability** and the rate for specific dates and a certain room type.
This web system will also handle **cancellations** (and its economic penalty), **hotel stays** (check-in, check-out), **services consumed** (and their charge) and **billing** (possibility of download a copy of the bill in pdf format).
The web system has an **administration interface** to manage hotels and their features, **language support for English :uk: and Spanish :es:** and **security control** (authentication and authorization). All the requisites were specified on an assignment for the “Ingeniería Web” (Web Engineering) module that I took at university (2014-2015). One of my classmates participated with me during this project.

### Technology
![Image](http://i1030.photobucket.com/albums/y369/MariaPhotoB/Technologies_zpslh3qreit.png)
- **Java**
- Java Platform Enterprise Edition (**Java EE**)
- **Spring Framework**, that utilizes the MVC architecture.
- **Spring Roo**
- **Hibernate ORM**
- HTML, XML, CSS, Bootstrap, jQuery, SQL, MySQL, Apache Maven, Apache Tomcat, Git and Assembla.

### Documentation

[Click here to access to Assembla](https://www.assembla.com/spaces/iw2015-hotel-oasis/wiki/E1_-_Documento_de_plan_de_proyecto)


### License
This project is licensed under the [Apache 2 License](http://www.apache.org/licenses/LICENSE-2.0). 


### Deploying the Web App using STS
You must have installed and configured the Java Development Kit (JDK), Spring Tool Suite (STS), Apache Tomcat 7 (or later) and a RDBMS.

1. Clone the repository or download the zip file and extract it.
2. Import the project into STS. File > Import... Select Existing Project into Workspace.
3. Create a new Server. From Servers, New > Other... Select Apache/Tomcat 7 or later.
3. Import the .sql file attached into your RDBMS (you can use phpMyAdmin or MySQL Workbench for this duty)
4. Modify your database connection values in **database.properties**.
5. Run As > Run on Server. Anyway, the url where the web app will be deployed is http://localhost:8080/HotelOasis

### Demo

![demo](http://i1030.photobucket.com/albums/y369/MariaPhotoB/HotelOasis_zpsskvcpr7l.gif)

