//
//  SessionManager.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-03-15.
//

import Foundation
import Amplify

enum AuthState {
    case signUp
    case logIn
    case confirmCode(username: String)
    case session(user: AuthUser)
}

final class SessionManager: ObservableObject {
    @Published var authState: AuthState = .logIn
    
}
