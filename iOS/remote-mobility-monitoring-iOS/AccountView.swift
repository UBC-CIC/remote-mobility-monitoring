//
//  AccountView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-03-19.
//

import SwiftUI
import Amplify
import AWSCognitoAuthPlugin
import AWSPluginsCore

struct AccountView: View {
    @Environment(\.presentationMode) var presentationMode
    @Binding var isAuthenticated: Bool
    
    func signOutGlobally() async {
        let result = await Amplify.Auth.signOut(options: .init(globalSignOut: true))
        guard let signOutResult = result as? AWSCognitoSignOutResult
        else {
            print("Signout failed")
            return
        }

        print("Local signout successful: \(signOutResult.signedOutLocally)")
        switch signOutResult {
        case .complete:
            // handle successful sign out
            isAuthenticated = false
        case .failed(let error):
            // handle failed sign out
            isAuthenticated = true
        case let .partial(revokeTokenError, globalSignOutError, hostedUIError):
            // handle partial sign out
            isAuthenticated = false
        }
    }
    
    var body: some View {
        VStack {
            // Your other content here
            
            Spacer()
            
            HStack {
                Button(action: {
                    Task {
                        try await signOutGlobally()
                    }
                    // presentationMode.wrappedValue.dismiss()
                }) {
                    Text("Log out")
                        .foregroundColor(.blue)
                }
                
                Button(action: {
                    // Handle delete account action here
                    // For example:
                    // AuthService.shared.deleteAccount()
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Text("Delete account")
                        .foregroundColor(.red)
                }
            }
        }
    }
}
