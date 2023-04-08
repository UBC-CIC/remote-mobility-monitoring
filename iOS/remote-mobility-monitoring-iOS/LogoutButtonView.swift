//
//  LogoutButtonView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-04-02.
//

import Foundation
import SwiftUI
import Amplify
import AWSCognitoAuthPlugin
import AWSPluginsCore

struct LogoutButtonView: View {
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
        Button(action: {
            Task {
                try await signOutGlobally()
            }
        }) {
            Label("Logout", systemImage: "rectangle.portrait.and.arrow.right")
                .foregroundColor(MobimonTheme.purple)
        }
        .buttonStyle(BorderlessButtonStyle())
    }
}
