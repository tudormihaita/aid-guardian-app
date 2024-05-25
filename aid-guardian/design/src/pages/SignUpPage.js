import '../../../frontend/src/pages/SignUp/SignUpPage.css';
import {useNavigate} from "react-router-dom";
import {useState} from "react";

const SignUpPage = () => {
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [gender, setGender] = useState("male");
    const [dob, setDob] = useState("");
    const [height, setHeight] = useState("");
    const [weight, setWeight] = useState("");
    const [bloodGroup, setBloodGroup] = useState("A_POSITIVE");
    const [medicalHistory, setMedicalHistory] = useState("");
    const [certificationFile, setCertificationFile] = useState();
    const [isFirstResponder, setIsFirstResponder] = useState(false);

    const handleSubmit = (event) => {
        event.preventDefault();
        fetch("http://localhost:8080/register", {
            method: 'POST',
            body: JSON.stringify({
                email: email,
                username: username,
                password: password,
                confirmPassword: confirmPassword,
                firstName: firstName,
                lastName: lastName,
                gender: gender,
                dob: dob,
                height: height,
                weight: weight,
                bloodGroup: bloodGroup,
                medicalHistory: medicalHistory,
                certificationFile: certificationFile,
                isFirstResponder: isFirstResponder,
            })
        }).then(response => {
            navigate("/profile");

        })
    }

    const validateCertificate = (event) => {
        event.preventDefault();
        const formData = new FormData();
        formData.append("certificate", certificationFile);
        formData.append("name", `${lastName} ${firstName}`);
        fetch("http://localhost:5000/certificate-recognition", {method: 'POST', body: formData}).then(response => {
            response.json().then(data => {
                setIsFirstResponder(data["is_valid"]);
            })
        })
    }


    return (
        <div className="register-container">
            <div className="register-header">
                <div className="logo">
                    <img src="assets/shield.png" alt="Aid Guardian Logo"/>
                </div>
                <h2>Register to Aid Guardian</h2>
            </div>
            <form onSubmit={handleSubmit} className="register-form">
                <div className="form-group">
                    <label form="email">Email:</label>
                    <input type="email" id="email" name="email" value={email} onChange={(e) => setEmail(e.target.value)}
                           required/>
                </div>
                <div className="form-group">
                    <label form="username">Username:</label>
                    <input type="text" id="username" name="username" value={username}
                           onChange={(e) => setUsername(e.target.value)} required/>
                </div>
                <div className="form-group">
                    <label form="password">Password:</label>
                    <input type="password" id="password" name="password" value={password}
                           onChange={(e) => setPassword(e.target.value)} required/>
                </div>
                <div className="form-group">
                    <label form="confirm-password">Confirm Password:</label>
                    <input type="password" id="confirm-password" name="confirm-password" value={confirmPassword}
                           onChange={(e) => setConfirmPassword(e.target.value)} required/>
                </div>
                <div className="form-group">
                    <label form="first-name">First Name:</label>
                    <input type="text" id="first-name" name="first-name" value={firstName}
                           onChange={(e) => setFirstName(e.target.value)} required/>
                </div>
                <div className="form-group">
                    <label form="last-name">Last Name:</label>
                    <input type="text" id="last-name" name="last-name" value={lastName}
                           onChange={(e) => setLastName(e.target.value)} required/>
                </div>
                <div className="form-group">
                    <label form="gender">Gender:</label>
                    <select id="gender" name="gender" value={gender} onChange={(e) => setGender(e.target.value)}>
                        <option value="female">Female</option>
                        <option value="male">Male</option>
                        <option value="other">Other</option>
                    </select>
                </div>
                <div className="form-group">
                    <label form="dob">Date of Birth:</label>
                    <input type="date" id="dob" name="dob" value={dob} onChange={(e) => setDob(e.target.value)}
                           required/>
                </div>
                <div className="form-group">
                    <label form="height">Height (cm):</label>
                    <input type="number" id="height" name="height" value={height}
                           onChange={(e) => setHeight(e.target.value)} required/>
                </div>
                <div className="form-group">
                    <label form="weight">Weight (kg):</label>
                    <input type="number" id="weight" name="weight" value={weight}
                           onChange={(e) => setWeight(e.target.value)} required/>
                </div>
                <div className="form-group">
                    <label form="blood-group">Blood Group:</label>
                    <select id="blood-group" name="blood-group" value={bloodGroup}
                            onChange={(e) => setBloodGroup(e.target.value)}>
                        <option value="A+">A+</option>
                        <option value="A-">A-</option>
                        <option value="B+">B+</option>
                        <option value="B-">B-</option>
                        <option value="AB+">AB+</option>
                        <option value="AB-">AB-</option>
                        <option value="O+">O+</option>
                        <option value="O-">O-</option>
                    </select>
                </div>
                <div className="form-group">
                    <label form="medical-history">Medical History:</label>
                    <textarea id="medical-history" name="medical-history" rows="4" value={medicalHistory}
                              onChange={(e) => setMedicalHistory(e.target.value)}></textarea>
                </div>
                <div className="form-group-upload-certificate">
                    <label form="certification">Medical/First-aid Certification:</label>
                    <input type="file" id="certification" name="certification"
                           onChange={(e) => setCertificationFile(e.target.files[0])}/>
                    <button id="validate-certificate" onClick={(event) => {
                        if (certificationFile) {
                            validateCertificate(event)
                        }
                    }}>Validate</button>
                </div>
                <div className="form-group-validate"></div>
                <div className="form-group-information">
                    <small>Please upload your medical or first-aid certification here. Only PDF, JPG, and PNG formats
                        are accepted.</small>
                </div>
                <button type="submit" className="register-button">Register</button>
            </form>
        </div>
    );
}

export default SignUpPage;