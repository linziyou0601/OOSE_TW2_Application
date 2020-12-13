# OOSE TW2 Application

A teamwork of Dr. Jenny's Object-oriented software engineering course for Java implementation of the proposal, last teamwork, about IoT Devices Application.

## Key Functionality

### 1. For User

- Can register and login to the system.
- Allow user search for available classroom and book them.
- User can check all of his/her bookings.
- During using time, user can control the IoT Device of classroom through this application.

### 2. For Admin

- Can login to the management system.
- Can see that all classroom and its devices' state.
- Can control all IoT devices in the classroom on the control panel.
- Can manage all bookings and book for user when needed.

## Design Pattens Implemented

### 1. Singleton Pattern:

Because all our ViewModel objects will access the SessionConext instance, the SessionContext should be global and there is only one instance in the system. We must ensure that all Session objects accessed by the ViewModel are the same.

### 2. Iterator Pattern:

We use different types of data storage in the project, such as ArrayList, HashMap, etc. To easily access storage elements. We use the Iterator Pattern to provide the same interface for accessing elements without having to understand the underlying structure of storage.

### 3. Bridge Pattern:

We have two databases, MariaDB and MongoDB. One of them is a relational database and the other is a NoSQL database. Therefore, we use Bridge Pattern to implement different database implementors, so that database manager objects can connect to different databases more flexibly. Moreover, the DB Implementor of the Manager can be changed without changing the access interface provided to the ViewModel object.

### 4. Proxy Pattern

Since the database is a remote database, the data that is not likely to change will be cached after the first query, without having to repeatedly access the database to speed up the retrieval of the database.

### 5. Builder Pattern

Because the composition of Alert has Layout Pane, Title, Body, Button, etc., but the small parts between each type of Alert can still be replaced, like a Basic Alert and Input Alert, the body of them are different. However, their construction process are similar. So we use Builder Pattern to make these complex object to seperate the construction from its representation.

### 6. Observer Pattern

Because we want to automatically change the color and layout of the buttons, icons, and label components of the IoT Device information Pane when the state of each IoT Devices in the classroom changes. So, we use the Observer Pattern to define a dependency between these two object, when state of IoT Device changes, it will notify the component of View automatically.

### 7. Adapter Pattern

In order to achieve the above purpose, observe object, we need to first convert the buttons and icons that cannot be observed into the Observer Interface. So we use Adapter to do this.

### 8. Factory Method Pattern

Since we have different kinds of IoT Devices object, so we use Factory Method Pattern to create these objects.

## Technology Stack
Java, JavaFx, Gradle

## Badge
![GitHub last commit](https://img.shields.io/github/last-commit/linziyou0601/OOSE_TW2_Application?style=for-the-badge) ![](https://img.shields.io/badge/language-java-blue.svg?style=for-the-badge) ![](https://img.shields.io/badge/author-linziyou0601-red.svg?style=for-the-badge)