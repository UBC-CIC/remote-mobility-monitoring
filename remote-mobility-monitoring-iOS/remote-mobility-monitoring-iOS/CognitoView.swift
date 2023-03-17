//
//  CognitoView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-03-16.
//

import SwiftUI
import Amplify
import AWSCognitoAuthPlugin
import AWSPluginsCore
import JWTDecode

struct CognitoView: View {
    @State var email: String = ""
    @State var password: String = ""
    @State var passwordAgain: String = ""
    @State var firstName: String = ""
    @State var lastName: String = ""
    @State var phoneNumber: String = ""
    @State var isSignIn: Bool = true
    @State var errorMessage: String? = nil
    @State var successMessage: String? = nil
    @State var isSignedIn: Bool = false
    @State var showPasswords: Bool = false // added state variable to show/hide passwords

    var body: some View {
        VStack {
            WelcomeText()
                        
            EmailField(email: $email)
            
            PasswordFields(isSignIn: $isSignIn, password: $password, passwordAgain: $passwordAgain, showPasswords: $showPasswords)
            
            if !isSignIn {
                SignUpFields(firstName: $firstName, lastName: $lastName, phoneNumber: $phoneNumber)
            }
            
            ShowPasswordsToggle(showPasswords: $showPasswords)
            
            Button(action: {
                if isSignIn {
                    Task {
                        try await signIn(username: email, password: password)
                    }
                } else {
                    if (password == passwordAgain) { // check if passwords match 
                        Task {
                            try await createPatient(email: email, password: password, firstName: firstName, lastName: lastName, phoneNumber: phoneNumber) { result in
                                switch result {
                                case .success(let responseObject): // patient id is in reponseObject
                                    print("Successfully created patient: \(responseObject)")
                                    successMessage = "Succesfully created account!"
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                                        successMessage = nil
                                    }
                                case .failure(let error):
                                    print("Failed to create patient: \(error.localizedDescription)")
                                    errorMessage = "Failed to create an account! \(error.localizedDescription)"
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                                        errorMessage = nil
                                    }
                                }
                            }
                        }
                    } else {
                        errorMessage = "Passwords do not match."
                        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                            errorMessage = nil
                        }
                    }
                }
            }) {
                if isSignIn {
                    Text("Sign In")
                } else {
                    Text("Sign Up")
                }
            }
            .frame(minWidth: 0, maxWidth: .infinity)
            .padding()
            .foregroundColor(.white)
            .background(Color.blue)
            .cornerRadius(5.0)
            .padding(.bottom, 20)
            
            Button(action: {
                isSignIn.toggle()
            }) {
                if isSignIn {
                    Text("Don't have an account? Sign up here.")
                } else {
                    Text("Already have an account? Sign in here.")
                }
            }
            .padding(.bottom, 20)
            
            // Display error message if it exists
            if let errorMessage = errorMessage {
                Text(errorMessage)
                    .foregroundColor(.red)
                    .padding(.bottom, 20)
            }
            
            // Display error message if it exists
            if let successMessage = successMessage {
                Text(successMessage)
                    .foregroundColor(.green)
                    .padding(.bottom, 20)
            }
            
            Button(action: {
                Task {
                    try await getAuthIdToken()
                }
            }) {
                Text("Get Auth Token")
            }
        }
        .padding()
    }
    
    func getAuthIdToken() async {
        do {
            let session = try await Amplify.Auth.fetchAuthSession(options: .forceRefresh())
            // Get cognito user pool token
            if let cognitoTokenProvider = session as? AuthCognitoTokensProvider {
                let tokens = try cognitoTokenProvider.getCognitoTokens().get()
                // Decode the JWT token
                let jwt = try decode(jwt: tokens.idToken)
                // Extract the user ID from the JWT payload
                if let user_id = jwt["sub"].string {
                    print("user id: \(user_id)")
                }
                print("full id token: \(tokens.idToken)")
            }
        } catch let error as AuthError {
            print("Fetch auth session failed with error - \(error)")
        } catch {
            print("Unexpected error: \(error)")
        }
    }
    
    func signIn(username: String, password: String) async {
        do {
            let signInResult = try await Amplify.Auth.signIn(username: username, password: password)
            switch signInResult.nextStep {
            case .confirmSignInWithSMSMFACode(let deliveryDetails, let info):
                print("SMS code send to \(deliveryDetails.destination)")
                print("Additional info \(String(describing: info))")

                // Prompt the user to enter the SMSMFA code they received
                // Then invoke `confirmSignIn` api with the code
            
            case .confirmSignInWithCustomChallenge(let info):
                print("Custom challenge, additional info \(String(describing: info))")
                
                // Prompt the user to enter custom challenge answer
                // Then invoke `confirmSignIn` api with the answer
            
            case .confirmSignInWithNewPassword(let info):
                print("New password additional info \(String(describing: info))")
                
                // Prompt the user to enter a new password
                // Then invoke `confirmSignIn` api with new password
            
            case .resetPassword(let info):
                print("Reset password additional info \(String(describing: info))")
                
                // User needs to reset their password.
                // Invoke `resetPassword` api to start the reset password
                // flow, and once reset password flow completes, invoke
                // `signIn` api to trigger signin flow again.
            
            case .confirmSignUp(let info):
                print("Confirm signup additional info \(String(describing: info))")
                
                // User was not confirmed during the signup process.
                // Invoke `confirmSignUp` api to confirm the user if
                // they have the confirmation code. If they do not have the
                // confirmation code, invoke `resendSignUpCode` to send the
                // code again.
                // After the user is confirmed, invoke the `signIn` api again.
            case .done:
                // Use has successfully signed in to the app
                print("Signin complete")
                successMessage = "Succesfully logged in"
                DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                    successMessage = nil
                }
            }
        } catch let error as AuthError{
            print ("Sign in failed \(error)")
        } catch {
            print("Unexpected error: \(error)")
        }
    }
}

struct WelcomeText: View {
    var body: some View {
        Text("Welcome to Mobimon")
            .font(.title)
            .padding(.bottom, 20)
    }
}

struct EmailField: View {
    @Binding var email: String

    var body: some View {
        TextField("Email", text: $email)
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(5.0)
            .padding(.bottom, 20)
            .accessibilityLabel("Email")
            .accessibilityHint("Enter your email address.")
    }
}

struct PasswordFields: View {
    @Binding var isSignIn: Bool
    @Binding var password: String
    @Binding var passwordAgain: String
    @Binding var showPasswords: Bool

    var body: some View {
        Group {
            if showPasswords {
                TextField("Password", text: $password)
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(5.0)
                    .padding(.bottom, 20)
                if(!isSignIn) {
                    TextField("Confirm Password", text: $passwordAgain)
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(5.0)
                        .padding(.bottom, 20)
                }
            } else {
                SecureField("Password", text: $password)
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(5.0)
                    .padding(.bottom, 20)
                if(!isSignIn) {
                    SecureField("Confirm Password", text: $passwordAgain)
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(5.0)
                        .padding(.bottom, 20)
                }
            }
        }
    }
}

struct SignUpFields: View {
    @Binding var firstName: String
    @Binding var lastName: String
    @Binding var phoneNumber: String

    var body: some View {
        TextField("First Name", text: $firstName)
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(5.0)
            .padding(.bottom, 20)
        
        TextField("Last Name", text: $lastName)
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(5.0)
            .padding(.bottom, 20)
        
        TextField("Phone Number", text: $phoneNumber)
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(5.0)
            .padding(.bottom, 20)
    }
}

struct ShowPasswordsToggle: View {
    @Binding var showPasswords: Bool

    var body: some View {
        Toggle("Show Passwords", isOn: $showPasswords)
            .padding(.bottom, 20)
            .accessibilityLabel("Show Passwords")
            .accessibilityHint("Toggle this switch to show or hide the passwords.")
    }
}
