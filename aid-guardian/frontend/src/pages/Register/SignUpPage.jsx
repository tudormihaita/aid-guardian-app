import './SignUpPage.css';
import {useNavigate} from "react-router-dom";
import {useState} from "react";
import {useAuth} from "../../contexts/AuthContext.jsx";
import {useData} from "../../contexts/DataContext.jsx";
import {useSocket} from "../../contexts/ConnectionContext.jsx";

const SignUpPage = () => {
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [gender, setGender] = useState("Male");
    const [dob, setDob] = useState("");
    const [height, setHeight] = useState("");
    const [weight, setWeight] = useState("");
    const [bloodGroup, setBloodGroup] = useState("A_POSITIVE");
    const [medicalHistory, setMedicalHistory] = useState("");
    const [certificationFile, setCertificationFile] = useState();
    const [isFirstResponder, setIsFirstResponder] = useState("COMMUNITY_DISPATCHER");
    const [score] = useState(1.0);

    const { setToken } = useAuth();
    const { setUser, setProfile } = useData();
    const { initializeConnection } = useSocket();

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (email === '' || username === '' || password === '' || confirmPassword === '' || firstName === ''
            || lastName === '' || dob === '' || height === '' || weight === '')
        {
            alert('Please fill in all required fields!');
            return;
        }
        if(password !== confirmPassword) {
            alert('Passwords do not match!');
            return;
        }

        // Send user register request
        fetch("http://localhost:8080/aid-guardian/users/register", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                "email": email,
                "username": username,
                "password": password,
                "role": isFirstResponder
            })
        }).then(response => {
            if (response.status === 200) {
                return response.json();
            }
            else
            {
                alert('Invalid registration credentials, please verify and try again.');
            }
        }).then(userData =>{
            console.log("New registered user: ",userData);

            const { authenticatedUser, accessToken } = userData;
            localStorage.setItem('user', JSON.stringify(authenticatedUser));
            localStorage.setItem('token', accessToken);

            setToken(userData.accessToken);
            setUser(authenticatedUser);
            initializeConnection();


            return authenticatedUser;
        }).then(savedUserData => {

            console.log("New registered user data: ", savedUserData);
            const profileData = {
                "user": savedUserData,
                "firstName": firstName,
                "lastName": lastName,
                "gender": gender,
                "birthDate": dob,
                "bloodGroup": bloodGroup,
                "height": height,
                "weight": weight,
                "medicalHistory": medicalHistory,
                "score": score
            };
            console.log("Profile data sent: ", profileData);

            return fetch("http://localhost:8080/aid-guardian/user-profiles/register", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(profileData)
            });
        }).then(response => {
                if (response.status === 401) {
                   alert('Unable to retrieve user information.');
                } else {
                    return response.json();
                }
        }).then(profileData => {
            console.log(profileData);

            localStorage.setItem('profile', JSON.stringify(profileData));
            setProfile(profileData);

            alert(`Welcome to Aid Guardian, ${firstName}! You have successfully registered as ${isFirstResponder.toLowerCase()}!`);
            navigate("/profile", {replace: true});
        }).catch(error => {
            console.log("Error on fetching registration request: " + error);
        });
    }

    const validateCertificate = async (event) => {
            event.preventDefault();

            if (!certificationFile) {
                alert('Please upload a certificate before validating!');
                return;
            }

            const formData = new FormData();
            formData.append("certificate", certificationFile);
            formData.append("name", `${lastName} ${firstName}`);

            try {
                const response = await fetch("http://localhost:5050/certificate-recognition", {
                    method: 'POST',
                    headers: {
                        'Access-Control-Allow-Origin': '*'
                    },
                    body: formData
                });

                if (!response.ok) {
                    console.error('Error validating certificate');
                    return;
                }

                const data = await response.json();
                console.log("Certificate validation response: ", data);
                if (data["is_valid"]) {
                    setIsFirstResponder("FIRST_RESPONDER");
                    alert("Congratulations! Your certificate is valid! You can now register as a first responder.")

                    console.log("Valid certificate");
                    console.log("First aid validation for user: ", isFirstResponder);
                } else {
                    alert("Validation failed. Please check again your user credentials or provide a valid certificate.");
                    console.log("Invalid certificate");
                }
            }
            catch (error) {
                console.error('Error validating certificate:', error);
            }
    };


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
                        <option value="Female">Female</option>
                        <option value="Male">Male</option>
                        <option value="Other">Other</option>
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
                    <button id="validate-certificate" onClick={validateCertificate}>Validate</button>
                </div>
                <div className="form-group-validate"></div>
                <div className="form-group-information">
                    <small>Please upload your medical or first-aid certification here. Only PDF, JPG, and PNG formats
                        are accepted.</small>
                </div>
                <button type="submit" className="register-button" >Register</button>
            </form>
        </div>
    );
}

export default SignUpPage;