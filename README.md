# 🚀 QuickPrint - Real-Time Smart Printing Dashboard

**QuickPrint** is a full-stack automated printing management system designed for cyber cafes and document centers. It allows users to scan a QR code, upload files, and enables the shop owner to manage, print, and track requests in real-time without any manual intervention.

---

## 🔗 Repository Links
* **Frontend Repository:** [Click Here to View](https://github.com/shivamChauhan4712/quickprint-frontend)
* **Backend Repository:** [Click Here to View](https://github.com/shivamChauhan4712/quickprint-backend)

---

## 🌟 Key Features
* **Real-Time Dashboard:** Instant file arrival notifications using **WebSockets (STOMP + SockJS)**.
* **Bulk Operations:** Integrated **Bulk Download** (sequential) and **Bulk Delete** (atomic backend transactions).
* **Advanced Filtering:** Filter requests by status (Pending, Printed, Downloaded) and search by filename.
* **Secure Authentication:** Owner login protected by **JWT (JSON Web Tokens)** and Spring Security.
* **Responsive UI:** Fully responsive dashboard built with **React** and **Bootstrap 5**.
* **Status Tracking:** Track the lifecycle of a print request from 'Pending' to 'Printed'.
* **QR Code Integration:** Unique QR codes for every cafe to route files to the correct dashboard.

---

## 🛠️ Tech Stack

### **Frontend**
* **React.js** (Functional Components & Hooks)
* **React Router Dom** (Protected Routes)
* **Axios** (API Integration)
* **SweetAlert2** (Interactive UI Modals)
* **Bootstrap 5** (Styling & Layout)

### **Backend**
* **Java (Spring Boot)**
* **Spring Security** (Authentication & CORS Management)
* **Spring Data JPA** (MySQL Database)
* **WebSockets** (Real-time Communication)
* **Hibernate** (DDL Auto-mapping)

### **Infrastructure**
* **Ngrok:** Secure tunneling for local backend access.
* **Vercel:** Frontend deployment.
* **GitHub:** Version control.

---

## ⚙️ Project Architecture
1. **Client-Side:** User scans the QR and uploads a file via a dedicated landing page.
2. **Server-Side:** Spring Boot processes the file, saves it to the local `uploads` directory, and records metadata in MySQL.
3. **Real-Time Update:** The server broadcasts a message to the specific Cafe's WebSocket topic.
4. **Dashboard:** The React app receives the message and updates the UI instantly without a page refresh.

---

## 🚀 Installation & Setup

### **Backend**
1. Clone the repository.
2. Update `application.properties` with your MySQL credentials.
3. Run the Spring Boot application.
4. Start ngrok tunnel:
```
ngrok http --url=your-static-domain.ngrok-free.dev 8080
```

### **2. Frontend**
1. Navigate to the frontend folder.
2. Create a .env file and add your backend URL:
```
VITE_API_BASE_URL=https://your-static-domain.ngrok-free.dev
```
3. Install dependencies and start the app:
```
npm install
npm run dev
```
## 📈 Future Enhancements
1. Cloud Storage: Integration with AWS S3 for scalable file management.
2. Payments: Payment gateway integration (UPI/Razorpay) before printing.
3. Multi-tenancy: Multi-owner role management for larger cyber cafes.
4. Smart PDF Logic: Automated PDF page counting logic for price estimation.

---

**Developed with ❤️ by [Shivam Chauhan](https://github.com/shivamChauhan4712)**
