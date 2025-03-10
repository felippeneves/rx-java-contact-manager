# RxJava & RxAndroid with Room - Study Project

## 📌 Overview
This project is a **study repository** for learning and practicing **RxJava** and **RxAndroid** in an Android application. It integrates **Room** as the database layer to perform database operations reactively. The purpose of this project is to understand how to work with **observables**, **schedulers**, and **disposables** while ensuring efficient data handling.

## 🎯 Features
- Store and retrieve data using **Room** as a local database 📄
- Observe database changes reactively with **RxJava**
- Manage **Schedulers** to handle background and UI thread execution ⚡
- Properly manage **disposables** to avoid memory leaks
- Implement **DAO (Data Access Object)** operations with RxJava integration

## 🛠️ Tech Stack
- **Language:** Java
- **Database:** Room (SQLite)
- **Reactive Programming:** RxJava & RxAndroid
- **UI Components:** XML

## 🔧 Setup
1. **Clone the repository:**
   ```sh
   git clone https://github.com/felippeneves/rx-java-contact-manager.git
   ```
2. **Open the project in Android Studio**.
3. **Build and run the project** 🚀.

## 🗄 Database Integration (Room)
- **Entities & Tables:** Defines data models stored in SQLite.
- **DAO (Data Access Object):** Interfaces for database queries using **RxJava**.

## 🛠️ Dependencies
```gradle
dependencies {
    implementation 'android.arch.persistence.room:runtime:1.1.1'
    implementation 'android.arch.persistence.room:rxjava2:1.1.1'
    annotationProcessor 'android.arch.persistence.room:compiler:1.1.1'

    implementation 'io.reactivex.rxjava2:rxjava:2.2.0'
    implementation 'io.reactivex.rxjava2:rxandroid:2.1.0'
}
```

## 📌 Key Learnings
✅ How to integrate **RxJava** with **Room**  
✅ Using **Schedulers** for background processing  
✅ Managing **disposables** to avoid memory leaks  
✅ Observing database changes reactively  

## 🤝 Contributing
Feel free to fork this repository and contribute with new examples, fixes, or improvements!

## 📝 License
This project is licensed under the **MIT License**.

## Author
[Felippe Neves](https://github.com/felippeneves)

