//
//  CognitoAuthView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-03-15.
//

import Foundation
import SwiftUI
import Amplify
import AWSCognitoAuthPlugin

struct CognitoAuthView: View {
    @State var email: String = ""
    @State var password: String = ""
    @State var isSignIn: Bool = true
    @State var errorMessage: String? = nil

    var body: some View {
        VStack {
            Text("Welcome to MyApp")
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
                } else {
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
        }
        .padding()
    }
}
