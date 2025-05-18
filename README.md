<H2>Contributors</H2>
<ul>
  <li>Matías E. Borra Quiroz</li>
  <li>Adrián Bermejo Naranjo</li>
  <li>Pablo García Sánchez-Fotún</li>
</ul>

<h2>Table of Contents</h2>
<ul>
  <li><a href="#overview">Overview</a></li>
  <li><a href="#features">🚀 Features</a></li>
  <li><a href="#technologies">🛠️ Technologies</a></li>
  <li><a href="#project-structure">📂 Project Structure</a></li>
  <li><a href="#installation">🎯 Installation</a></li>
  <li><a href="#sftp-setup">⚙️ SFTP Setup</a></li>
  <li><a href="#pdf-generation">📝 PDF & Reports</a></li>
  <li><a href="#contributing">🤝 Contributing</a></li>
  <li><a href="#license">📄 License</a></li>
</ul>

<hr>

<h2 id="overview">Overview</h2>
<p>
  <strong>PSP-PhysioCare</strong> is a JavaFX desktop application designed to streamline
  the management of physiotherapy clinics. It offers secure authentication, full CRUD
  (Create, Read, Update, Delete) for Patients, Physiotherapists and Appointments, plus
  calendar visualization and PDF reporting with SFTP upload.
</p>

<hr>

<h2>Backend</h2>
<p>
  <a href="https://github.com/CleanMatyx/PhysioCareWeb">
    https://github.com/CleanMatyx/PhysioCareWeb
  </a>
</p>

<hr>

<h2 id="features">🚀 Features</h2>
<ul>
  <li>
    <strong>User Authentication</strong><br>
    Login via REST API using Gson + HTTPURLConnection. Stores JWT token &amp; userId.
  </li>
  <li>
    <strong>Patient &amp; Physio Management</strong><br>
    Searchable lists with add/edit/delete. DTO &amp; Response models for API mapping.
  </li>
  <li>
    <strong>Appointments CRUD</strong><br>
    Manage appointments: date, patient, physio, treatment, diagnosis, observations, price.
  </li>
  <li>
    <strong>Calendar View</strong><br>
    Integrates CalendarFX to display a logged-in physio’s appointments.
  </li>
  <li>
    <strong>PDF Reports</strong><br>
    In-memory generation (iText7) of:
    <ul>
      <li>Medical record PDF</li>
      <li>Appointments PDF</li>
      <li>Salary report PDF</li>
      <li>Custom Reports via createInMemory APIs</li>
    </ul>
  </li>
  <li>
    <strong>SFTP Upload</strong><br>
    Async upload of generated PDFs to VPS via JSch, with directory creation &amp; error handling.
  </li>
</ul>

<hr>

<h2 id="technologies">🛠️ Technologies</h2>
<ul>
  <li><strong>Java 23</strong></li>
  <li><strong>JavaFX 21</strong></li>
  <li><strong>CalendarFX</strong> (calendar UI)</li>
  <li><strong>Gson</strong> (JSON mapping)</li>
  <li><strong>iText 7</strong> (PDF creation)</li>
  <li><strong>JSch</strong> (SFTP)</li>
  <li><strong>Maven Wrapper</strong> (<code>./mvnw</code> / <code>mvnw.cmd</code>)</li>
</ul>

<hr>

<h2 id="project-structure">📂 Project Structure</h2>
<pre><code>
src/main
├── java
│   └── com/matias/physiocarepsp
│       ├── MainApplication.java
│       ├── models
│       │   ├── Appointment
│       │   │   ├── Appointment.java
│       │   │   ├── AppointmentDto.java
│       │   │   ├── AppointmentResponse.java
│       │   │   ├── AppointmentListResponse.java
│       │   │   └── AppointmentRequest.java
│       │   ├── Auth
│       │   │   ├── LoginRequest.java
│       │   │   └── AuthResponse.java
│       │   ├── Patient
│       │   ├── Physio
│       │   └── Record
│       │       ├── Record.java
│       │       ├── RecordListResponse.java
│       │       └── RecordResponse.java
│       ├── utils
│       │   ├── ServiceUtils.java
│       │   ├── LocalDateAdapter.java
│       │   ├── PDFUtil.java
│       │   ├── SftpUploader.java
│       │   └── Utils.java
│       └── viewscontroller
│           ├── LoginViewController.java
│           ├── FirstViewController.java
│           ├── AppointmentsViewController.java
│           ├── CalendarController.java
│           ├── PatientsViewController.java
│           └── PhysiosViewController.java
└── resources
    └── com/matias/physiocarepsp/fxmlviews
        ├── login-view.fxml
        ├── first-view.fxml
        ├── appointments-view.fxml
        ├── calendar-view.fxml
        ├── patients-view.fxml
        └── physios-view.fxml
</code></pre>

<hr>

<h2 id="installation">🎯 Installation</h2>
<ol>
  <li>
    <strong>Clone the repository</strong><br>
    <code>git clone https://github.com/pablogsf/PSP-PhysioCare.git</code><br>
    <code>cd PSP-PhysioCare/PhysioCarePSP-master</code>
  </li>
  <li><strong>Ensure Java 23</strong> is installed and on your <code>PATH</code>.</li>
  <li>
    <strong>Run the app</strong>:<br>
    <em>Linux/macOS:</em><br>
    <code>./mvnw clean javafx:run</code><br>
    <em>Windows:</em><br>
    <code>mvnw.cmd clean javafx:run</code>
  </li>
  <li><strong>Login</strong> with your credentials to begin.</li>
</ol>

<hr>

<h2 id="sftp-setup">⚙️ SFTP Setup</h2>
<p>
  To upload PDFs via SFTP, create the global folder <code>/records</code> on your VPS:
</p>
<pre><code>
sudo mkdir -p /records
sudo chown &lt;user&gt;:&lt;user&gt; /records
chmod 755 /records
</code></pre>
<p>
  Replace <code>&lt;user&gt;</code> with your SSH username (e.g. <code>matiasdev</code>).
  The app will then <code>cd("/records")</code> and <code>put(...)</code> without permission issues.
</p>

<hr>

<h2 id="pdf-generation">📝 PDF & Reports</h2>
<ul>
  <li>
    <strong>In-memory PDF</strong><br>
    <code>PDFUtil</code> methods return <code>byte[]</code> directly:
    <ul>
      <li><code>generateMedicalRecordPdf</code></li>
      <li><code>generateAppointmentsPdf</code></li>
      <li><code>generateSalaryPdf</code></li>
      <li><code>createRecordPdfInMemory</code> (with optional disk save)</li>
    </ul>
  </li>
  <li>
    <strong>Async upload</strong><br>
    Use <code>SftpUploader.uploadAsync(...)</code> to push reports to VPS without blocking UI.
  </li>
</ul>

<hr>

<h2 id="contributing">🤝 Contributing</h2>
<ol>
  <li>Fork the repo</li>
  <li>Create a feature branch:<br><code>git checkout -b feature/awesome-feature</code></li>
  <li>Commit your changes</li>
  <li>Push to your fork &amp; open a Pull Request</li>
</ol>

<hr>

<h2 id="license">📄 License</h2>
<p>
  This project is licensed under the <strong>MIT License</strong>. See
  <a href="LICENSE">LICENSE</a> for details.
</p>
