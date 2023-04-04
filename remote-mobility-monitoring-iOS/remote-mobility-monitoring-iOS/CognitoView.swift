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

struct CognitoView: View {
    @State var email: String = ""
    @State var password: String = ""
    @State var passwordAgain: String = ""
    @State var firstName: String = ""
    @State var lastName: String = ""
    @State var phoneNumber: String = ""
    @State var isSignIn: Bool = true
    @State var showPasswords: Bool = false
    @State var authenticateMessage: String = ""
    @State var isShowingAuthenticateResult: Bool = false
    @State var popupTextHeight: CGFloat = 50
    @Binding var isAuthenticated: Bool
    
    func displayAuthenticateResult(message: String) {
        self.isShowingAuthenticateResult = true
        self.authenticateMessage = message
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            self.isShowingAuthenticateResult = false
            self.authenticateMessage = ""
        }
    }
    
    func resetFields(keepSignInFields: Bool) {
        if (!keepSignInFields) {
            email = ""
            password = ""
        }
        passwordAgain = ""
        firstName = ""
        lastName = ""
        phoneNumber = ""
        isShowingAuthenticateResult = false
    }
    
    var body: some View {
        ZStack {
            VStack {
                WelcomeText()
                
                Spacer()
                
                EmailField(email: $email)
                
                PasswordFields(isSignIn: $isSignIn, password: $password, passwordAgain: $passwordAgain, showPasswords: $showPasswords)
                
                if !isSignIn {
                    SignUpFields(firstName: $firstName, lastName: $lastName, phoneNumber: $phoneNumber)
                }
                
                ShowPasswordsToggle(showPasswords: $showPasswords)
                
                GeometryReader { geometry in
                    HStack {
                        Spacer()
                        Button(action: {
                            if isSignIn {
                                Task {
                                    if(password == "" || email == "") {
                                        displayAuthenticateResult(message: "Please fill out email and password")
                                    } else {
                                        try await signIn(username: email.trimmingCharacters(in: .whitespaces), password: password)
                                    }
                                }
                            } else {
                                if (email == "" || password == "" || passwordAgain == "" || firstName == "" || lastName == "" || phoneNumber == "") {
                                    displayAuthenticateResult(message: "Please fill out all the required fields")
                                } else if (!CharacterSet.decimalDigits.isSuperset(of: CharacterSet(charactersIn: phoneNumber))) {
                                    displayAuthenticateResult(message: "Phone number must only contain numeric values")
                                } else if (password == passwordAgain) {
                                    Task {
                                        try await createPatient(email: email.trimmingCharacters(in: .whitespaces), password: password, firstName: firstName, lastName: lastName, phoneNumber: phoneNumber) { result in
                                            switch result {
                                            case .success(let responseObject): // patient id is in reponseObject
                                                print("Successfully created patient: \(responseObject)")
                                                displayAuthenticateResult(message: "Success")
                                                DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                                                    resetFields(keepSignInFields: true)
                                                    isSignIn = true
                                                }
                                                
                                            case .failure(let error):
                                                print("Failed to create patient: \(error.localizedDescription)")
                                                displayAuthenticateResult(message: "\(error.localizedDescription)")
                                            }
                                        }
                                    }
                                } else {
                                    displayAuthenticateResult(message: "Passwords do not match")
                                }
                            }
                        }) {
                            if isSignIn {
                                Text("Sign In")
                            } else {
                                Text("Sign Up")
                            }
                        }
                        .font(ButtonStyling.font)
                        .foregroundColor(ButtonStyling.foreGroundColor)
                        .padding()
                        .frame(minWidth: 0,maxWidth: (geometry.size.width * 2/3))
                        .background(ButtonStyling.color)
                        .cornerRadius(ButtonStyling.cornerRadius)
                        
                        Spacer()
                    }
                }
                .frame(maxHeight: 100)
                
                Spacer()
                
                Button(action: {
                    resetFields(keepSignInFields: false)
                    isSignIn.toggle()
                }) {
                    if isSignIn {
                        Text("Don't have an account? Sign up here.")
                    } else {
                        Text("Already have an account? Sign in here.")
                    }
                }
                .padding(.bottom, 20)
            }
            .padding()
            
            if isShowingAuthenticateResult {
                var maxPopUpHeight = self.authenticateMessage == "Success" ? 0.1 : 0.2

                GeometryReader { geometry in
                    VStack {
                        RoundedRectangle(cornerRadius: 12)
                            .fill(Color.white)
                            .shadow(radius: 10)
                            .overlay(
                                VStack(alignment: .center) {
                                    if self.authenticateMessage == "Success" {
                                        Text(self.authenticateMessage)
                                            .font(.body)
                                            .foregroundColor(.green)
                                            .padding()
                                    } else {
                                        Text(self.authenticateMessage)
                                            .font(.body)
                                            .foregroundColor(.red)
                                            .padding()
                                    }
                                }
                            )
                            .frame(maxWidth: geometry.size.width * 0.8)
                            .frame(maxHeight: geometry.size.height * maxPopUpHeight)
                            .transition(.opacity.animation(.easeInOut(duration: 0.3)))
                            .zIndex(1)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                }
            }
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
                displayAuthenticateResult(message: "Success")
                DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                    isAuthenticated = true
                }
            }
        } catch let error as AuthError{
            print ("Sign in failed \(error)")
            displayAuthenticateResult(message: "Sign in failed. \(error.errorDescription)")
        } catch {
            print("Unexpected error: \(error)")
            displayAuthenticateResult(message: "Failed to login, unexpected error")
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
