SMART UNIVERSITY EVENT & FEST MANAGEMENT SYSTEM
Core Java + CSV File Handling + 7 Design Patterns

Patterns:
1. Singleton - singleton/FestManagementSystem.java
2. Proxy - proxy/EventServiceProxy.java
3. Factory Method - factory/EventCreator and concrete creators
4. Chain of Responsibility - Faculty -> HOD -> Dean, based on Event Level
5. Abstract Factory - abstractfactory/ResourceFactory and concrete factories
6. Observer - observer/EventUpdateSubject / StudentObserver
7. Bridge - bridge/Notification + NotificationSender hierarchies

Storage: CSV only. No SQL/JDBC. No ArrayList/HashMap is used.

Event approval levels:
- DEPARTMENT: Faculty gives final approval.
- INTER_DEPARTMENT: Faculty forwards to HOD; HOD gives final approval.
- UNIVERSITY: Faculty forwards to HOD; HOD forwards to Dean; Dean gives final approval.

All transactional CSV files start with header only. users.csv retains login accounts.
IMPORTANT: Keep CSV files closed in Excel while Java is updating them.

Sample users:
Student: aarav / aarav123
Student: meera / meera123
Faculty: rahul / rahul123
HOD: priya / priya123
Dean: anjali / anjali123
Admin: admin / admin123

Continuous application flow:
- Run Main.java only once.
- Main menu provides 1. Login and 0. Exit Application.
- Choosing 0 inside any role menu logs that user out and returns to the main login menu.
- Invalid login also returns to the main menu instead of terminating the application.
- Use 0. Exit Application from the main menu only when the complete demo is finished.
