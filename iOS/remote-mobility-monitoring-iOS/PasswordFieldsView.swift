//
//  PasswordFieldsView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-04-04.
//

import Foundation
import SwiftUI

struct PasswordFields: View {
    // Properties
    @Binding var isSignIn: Bool
    @Binding var password: String
    @Binding var passwordAgain: String
    @Binding var showPasswords: Bool
    @Binding var validPassword: Bool

    // Variables to track password requirements
    @State private var hasUpperCase = false
    @State private var hasLowerCase = false
    @State private var hasNumber = false
    @State private var hasSpecialCharacter = false
    @State private var hasValidLength = false
    @State private var passwordsMatch = false
    @State private var showRequirements = false
    
    private func textFieldChanged(_ text: String) {
        if (!isSignIn) {
            self.showRequirements = true
            self.validatePassword()
        }
    }
    
    func containsSpecialCharacter(_ string: String) -> Bool {
        let pattern = #"[^a-zA-Z0-9\s]"#
        let regex = try! NSRegularExpression(pattern: pattern)
        let range = NSRange(location: 0, length: string.utf16.count)
        return regex.firstMatch(in: string, options: [], range: range) != nil
    }

    var body: some View {
        let passwordBinding = Binding<String>(
                    get: { self.password },
                    set: { self.password = $0; self.textFieldChanged($0) }
                )
        let passwordAgainBinding = Binding<String>(
                    get: { self.passwordAgain },
                    set: { self.passwordAgain = $0; self.textFieldChanged($0) }
                )
        ZStack(alignment: .trailing) {
            if showPasswords {
                TextField("Password", text: passwordBinding)
            } else {
                SecureField("Password", text: passwordBinding)
            }
            Button(action: {
                showPasswords.toggle()
            }) {
                Image(systemName: showPasswords ? "eye.fill" : "eye.slash.fill")
                    .foregroundColor(Color(.systemGray4))
            }
            .padding(.trailing)
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(5.0)
        .padding(.bottom, 20)

        // Password Confirmation Field
        if (!isSignIn) {
            ZStack(alignment: .trailing) {
                if showPasswords {
                    TextField("Confirm Password", text: passwordAgainBinding)
                } else {
                    SecureField("Confirm Password", text: passwordAgainBinding)
                }
                Button(action: {
                    showPasswords.toggle()
                }) {
                    Image(systemName: showPasswords ? "eye.fill" : "eye.slash.fill")
                        .foregroundColor(Color(.systemGray4))
                }
                .padding(.trailing)
            }
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(5.0)
            .padding(.bottom, 20)
        }

        // Password Requirements
        if showRequirements {
            VStack(alignment: .leading) {
                Text("Password Requirements:")
                    .font(.headline)
                    .padding(.bottom, 5)

                // Lowercase letter
                HStack {
                    Image(systemName: hasLowerCase ? "checkmark" : "xmark")
                        .foregroundColor(hasLowerCase ? .green : .red)
                        .font(.system(size: 20))

                    Text("Lowercase letter")
                        .foregroundColor(.secondary)
                }

                // Uppercase letter
                HStack {
                    Image(systemName: hasUpperCase ? "checkmark" : "xmark")
                        .foregroundColor(hasUpperCase ? .green : .red)
                        .font(.system(size: 20))

                    Text("Uppercase letter")
                        .foregroundColor(.secondary)
                }

                // Number
                HStack {
                    Image(systemName: hasNumber ? "checkmark" : "xmark")
                        .foregroundColor(hasNumber ? .green : .red)
                        .font(.system(size: 20))

                    Text("Number")
                        .foregroundColor(.secondary)
                }

                // Special character or space
                HStack {
                    Image(systemName: hasSpecialCharacter ? "checkmark" : "xmark")
                        .foregroundColor(hasSpecialCharacter ? .green : .red)
                        .font(.system(size: 20))

                    Text("Special character or space")
                        .foregroundColor(.secondary)
                }

                // At least 8 characters
                HStack {
                    Image(systemName: hasValidLength ? "checkmark" : "xmark")
                        .foregroundColor(hasValidLength ? .green : .red)
                        .font(.system(size: 20))

                    Text("At least 8 characters")
                        .foregroundColor(.secondary)
                }

                // Passwords match
                if !isSignIn {
                    HStack {
                        Image(systemName: passwordsMatch ? "checkmark" : "xmark")
                            .foregroundColor(passwordsMatch ? .green : .red)
                            .font(.system(size: 20))

                        Text("Passwords match")
                            .foregroundColor(.secondary)
                    }
                }

                // No leading or trailing spaces
                HStack {
                    Image(systemName: !hasLeadingOrTrailingSpaces() ? "checkmark" : "xmark")
                        .foregroundColor(!hasLeadingOrTrailingSpaces() ? .green : .red)
                        .font(.system(size: 20))

                    Text("No leading or trailing spaces")
                        .foregroundColor(.secondary)
                }
            }
        }
    }

    // Validate the password and update the state variables
    private func validatePassword() {
        hasUpperCase = password.contains(where: { $0.isUppercase })
        hasLowerCase = password.contains(where: { $0.isLowercase })
        hasNumber = password.contains(where: { $0.isNumber })
        hasSpecialCharacter = containsSpecialCharacter(password)
        hasValidLength = password.count >= 8
        passwordsMatch = password == passwordAgain
        showRequirements = !(hasUpperCase && hasLowerCase && hasNumber && hasSpecialCharacter && hasValidLength && passwordsMatch)
        validPassword = !showRequirements
    }

    private func hasLeadingOrTrailingSpaces() -> Bool {
        return password.first?.isWhitespace == true || password.last?.isWhitespace == true
    }
}
