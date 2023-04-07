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
    @State var birthday: Date = Date()
    @State var sex: String = ""
    @State var weight: Int = 0
    @State var height: Int = 0
    
    @State var isSignIn: Bool = true
    @State var showPasswords: Bool = false
    @State var message: String = ""
    @State var isShowingMessage: Bool = false
    @State var popupTextHeight: CGFloat = 50
    @State var validPassword: Bool = false
    @State var isLoading = false
    @Binding var isAuthenticated: Bool
    
    func displayMessage(message: String, loading: Bool) {
        var displayTime = DispatchTimeInterval.seconds(3)
        if isLoading {
            displayTime = DispatchTimeInterval.seconds(6)
        }
        
        self.message = message
        self.isShowingMessage = true
        DispatchQueue.main.asyncAfter(deadline: .now() + displayTime) {
            self.isLoading = false
            self.isShowingMessage = false
            self.message = ""
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
        birthday = Date()
        weight = 0
        height = 0
        validPassword = false
        isShowingMessage = false
    }
    
    var body: some View {
        ZStack {
            VStack {
                WelcomeText()
                
                Spacer()
                
                if (isSignIn) {
                    EmailField(email: $email)
                    
                    PasswordFields(isSignIn: $isSignIn, password: $password, passwordAgain: $passwordAgain, showPasswords: $showPasswords, validPassword: $validPassword)
                    
                    Spacer()
                } else {
                    ScrollView {
                        EmailField(email: $email)
                        
                        PasswordFields(isSignIn: $isSignIn, password: $password, passwordAgain: $passwordAgain, showPasswords: $showPasswords, validPassword: $validPassword)
                        
                        if !isSignIn {
                            SignUpFields(firstName: $firstName, lastName: $lastName, phoneNumber: $phoneNumber, birthday: $birthday, sex: $sex, weight: $weight, height: $height)
                        }
                    }
                }
                
                GeometryReader { geometry in
                    HStack {
                        Spacer()
                        Button(action: {
                            if isSignIn {
                                Task {
                                    if(password == "" || email == "") {
                                        displayMessage(message: "Please fill out email and password", loading: false)
                                    } else {
                                        try await signIn(username: email.trimmingCharacters(in: .whitespaces), password: password)
                                    }
                                }
                            } else {
                                if (email == "" || password == "" || passwordAgain == "" || firstName == "" || lastName == "" || phoneNumber == "" || sex == "" || weight == 0 || height == 0) {
                                    displayMessage(message: "Please fill out all the required fields", loading: false)
                                } else if (!validPassword) {
                                    displayMessage(message: "Please provide a strong password", loading: false)
                                }
                                else if (!CharacterSet.decimalDigits.isSuperset(of: CharacterSet(charactersIn: phoneNumber))) {
                                    displayMessage(message: "Phone number must only contain numeric values", loading: false)
                                } else if (password == passwordAgain) {
                                    Task {
                                        displayMessage(message: "Loading", loading: true)
                                        try await createPatient(email: email.trimmingCharacters(in: .whitespaces), password: password, firstName: firstName, lastName: lastName, phoneNumber: phoneNumber, sex: sex, height: Double(self.height), weight: Double(self.weight), birthday: self.birthday) { result in
                                            switch result {
                                            case .success(let responseObject): // patient id is in reponseObject
                                                print("Successfully created patient: \(responseObject)")
                                                displayMessage(message: "Success", loading: false)
                                                DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                                                    resetFields(keepSignInFields: true)
                                                    isSignIn = true
                                                }
                                                
                                            case .failure(let error):
                                                print("Failed to create patient: \(error.localizedDescription)")
                                                displayMessage(message: "\(error.localizedDescription)", loading: false)
                                            }
                                        }
                                    }
                                } else {
                                    displayMessage(message: "Passwords do not match", loading: false)
                                }
                            }
                        }) {
                            ZStack {
                                ButtonStyling.color
                                    .cornerRadius(ButtonStyling.cornerRadius)
                                if isSignIn {
                                    Text("Sign In")
                                } else {
                                    Text("Sign Up")
                                }
                            }
                            .frame(minWidth: geometry.size.width * 2/3, maxWidth: geometry.size.width * 2/3, minHeight: geometry.size.height * 2/5, maxHeight: geometry.size.height * 2/5)
                        }
                        .font(ButtonStyling.font)
                        .foregroundColor(ButtonStyling.foreGroundColor)
                        .padding()
                        .background(ButtonStyling.color)
                        .cornerRadius(ButtonStyling.cornerRadius)
                        .disabled(isShowingMessage)
                        
                        Spacer()
                    }
                }
                .frame(maxHeight: 100)
                .padding(.top, 10)
                .padding(.bottom, 10)
                
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
            .modifier(DismissingKeyboard())
            
            if isShowingMessage {
                var maxPopUpHeight = (self.message == "Success" || self.message == "Loading") ? 0.1 : 0.2

                GeometryReader { geometry in
                    VStack {
                        RoundedRectangle(cornerRadius: 12)
                            .fill(Color.white)
                            .shadow(radius: 10)
                            .overlay(
                                VStack(alignment: .center) {
                                    if self.message == "Success" {
                                        Text(self.message)
                                            .font(.body)
                                            .foregroundColor(.green)
                                            .padding()
                                    } else if self.message == "Loading" {
                                        Text(self.message)
                                            .font(.body)
                                            .foregroundColor(.black)
                                            .padding()
                                    } else {
                                        Text(self.message)
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
        .ignoresSafeArea(.keyboard)
    }
    
    func signIn(username: String, password: String) async {
        do {
            let signInResult = try await Amplify.Auth.signIn(username: username, password: password)
            switch signInResult.nextStep {
            case .confirmSignInWithSMSMFACode(let deliveryDetails, let info):
                print("SMS code send to \(deliveryDetails.destination)")
                print("Additional info \(String(describing: info))")
            
            case .confirmSignInWithCustomChallenge(let info):
                print("Custom challenge, additional info \(String(describing: info))")
            
            case .confirmSignInWithNewPassword(let info):
                print("New password additional info \(String(describing: info))")

            case .resetPassword(let info):
                print("Reset password additional info \(String(describing: info))")
            
            case .confirmSignUp(let info):
                print("Confirm signup additional info \(String(describing: info))")

            case .done:
                print("Signin complete")
                displayMessage(message: "Success", loading: false)
                DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                    isAuthenticated = true
                }
            }
        } catch let error as AuthError{
            print ("Sign in failed \(error)")
            displayMessage(message: "Sign in failed. \(error.errorDescription)", loading: false)
        } catch {
            print("Unexpected error: \(error)")
            displayMessage(message: "Failed to login, unexpected error", loading: false)
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

struct SignUpFields: View {
    @Binding var firstName: String
    @Binding var lastName: String
    @Binding var phoneNumber: String
    @Binding var birthday: Date
    @Binding var sex: String
    @Binding var weight: Int
    @Binding var height: Int
    
    private var weightTextFieldBinding: Binding<String> {
        Binding<String>(
            get: { weight == 0 ? "" : "\(weight)" },
            set: { newValue in
                if let value = Int(newValue) {
                    weight = value
                } else {
                    weight = 0
                }
            }
        )
    }

    private var heightTextFieldBinding: Binding<String> {
        Binding<String>(
            get: { height == 0 ? "" : "\(height)" },
            set: { newValue in
                if let value = Int(newValue) {
                    height = value
                } else {
                    height = 0
                }
            }
        )
    }

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
            .keyboardType(.numberPad)
        
        let minDate = Calendar.current.date(byAdding: .year, value: -18, to: Date())!
        
        HStack {
            DatePicker("Birthday", selection: $birthday, in: ...minDate, displayedComponents: .date)
                .padding()
                .background(Color(.systemGray6))
                .tint(MobimonTheme.purple)
                .cornerRadius(5.0)
            
            Picker("Sex", selection: $sex) {
                Text("Male").tag("M")
                Text("Female").tag("F")
                Text("Other").tag("O")
            }
            .padding()
            .background(Color(.systemGray6))
            .tint(.black)
            .cornerRadius(5.0)
        }
        .padding(.bottom, 20)
        
        VStack {
            HStack {
                Text("Weight")
                    .font(.headline)
                Spacer()
                TextField("E.g 60", text: weightTextFieldBinding)
                    .multilineTextAlignment(.trailing)
                    .frame(width: 80)
                    .font(.headline)
                    .foregroundColor(weight == 0 ? .secondary : .primary)
                Text("kg")
            }
            .padding(.bottom, 10)
            .keyboardType(.numberPad)
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(5.0)
        .padding(.bottom, 20)

        VStack {
            HStack {
                Text("Height")
                    .font(.headline)
                Spacer()
                TextField("E.g 170", text: heightTextFieldBinding)
                    .multilineTextAlignment(.trailing)
                    .frame(width: 80)
                    .font(.headline)
                    .foregroundColor(height == 0 ? .secondary : .primary)
                Text("cm")
            }
            .padding(.bottom, 10)
            .keyboardType(.numberPad)
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(5.0)
        .padding(.bottom, 120)    }
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
