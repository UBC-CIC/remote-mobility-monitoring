import SwiftUI
import Amplify
import AWSCognitoAuthPlugin

struct CognitoAuthView: View {
    @State var email: String = ""
    @State var password: String = ""
    @State var isSignIn: Bool = true
    @State var errorMessage: String? = nil
    @State var isSignedIn: Bool = false

    var body: some View {
        VStack {
            Text("Welcome to Mobimon")
                .font(.title)
                .padding(.bottom, 50)
            
            TextField("Email", text: $email)
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(5.0)
                .padding(.bottom, 20)
            
            SecureField("Password", text: $password)
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(5.0)
                .padding(.bottom, 20)
            
            Button(action: {
                if isSignIn {
                    Task {
                        try await signIn(username: email, password: password)
                    }
                } else {
                    Task {
                        try await signUp(username: email, password: password, email: email)
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
            
            Button(action: {
                Task {
                    try await fetchCurrentAuthSession()
                }
            }) {
                Text("Check if signed in")
            }
            
            if isSignedIn {
                Text("User is signed in")
            }
        }
        .padding()
    }
    
    func fetchCurrentAuthSession() async throws {
        let session = try await Amplify.Auth.fetchAuthSession()
        print("Is user signed in - \(session.isSignedIn)")
    }
    
    func signUp(username: String, password: String, email: String) async {
        let userAttributes = [AuthUserAttribute(.email, value: email)]
        let options = AuthSignUpRequest.Options(userAttributes: userAttributes)
        do {
            let signUpResult = try await Amplify.Auth.signUp(
                username: username,
                password: password,
                options: options
            )
            if case let .confirmUser(deliveryDetails, _, userId) = signUpResult.nextStep {
                print("Delivery details \(String(describing: deliveryDetails)) for userId: \(String(describing: userId))")
            } else {
                print("SignUp Complete")
            }
        } catch let error as AuthError {
            print("An error occurred while registering a user \(error)")
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
            }
        } catch let error as AuthError{
            print ("Sign in failed \(error)")
        } catch {
            print("Unexpected error: \(error)")
        }
    }
}
