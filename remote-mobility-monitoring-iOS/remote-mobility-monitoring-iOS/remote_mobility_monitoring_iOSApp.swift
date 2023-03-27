//
//  remote_mobility_monitoring_iOSApp.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-01-09.
//

import SwiftUI
import HealthKit
import Amplify
import AWSCognitoAuthPlugin
import AWSPluginsCore
import JWTDecode

@main
struct remote_mobility_monitoring_iOSApp: App {
    @UIApplicationDelegateAdaptor(CustomAppDelegate.self) var appDelegate
    @State var isAuthenticated: Bool = false
    @State var patientId: String = ""
    @State var idToken: String = ""
    
    var body: some Scene {
        WindowGroup {
            if isAuthenticated {
                VerificationView(isAuthenticated: $isAuthenticated, patientId: $patientId, idToken: $idToken)
                    .environmentObject(appDelegate.deepLinkURL)
                    .onOpenURL { url in
                        appDelegate.deepLinkURL.url = url
                    }
            } else {
                InitialLoadView(isAuthenticated: $isAuthenticated)
                    .task {
                        await authenticateUser()
                    }
            }
        }
        .onChange(of: isAuthenticated) { isAuth in
            Task.init {
                await authenticateUser()
            }
        }
    }
    
    init() {
        do {
            try Amplify.add(plugin: AWSCognitoAuthPlugin())
            try Amplify.configure()
            print("Amplify configured with auth plugin")
        } catch {
            print("Failed to initialize Amplify with \(error)")
        }
    }
    
    func authenticateUser() async {
        do {
            let session = try await Amplify.Auth.fetchAuthSession(options: .forceRefresh())
            // Get cognito user pool token
            if let cognitoTokenProvider = session as? AuthCognitoTokensProvider {
                let tokens = try cognitoTokenProvider.getCognitoTokens().get()
                // Decode the JWT token
                let jwt = try decode(jwt: tokens.idToken)
                // Extract the user ID from the JWT payload
                if let patientId = jwt["sub"].string {
                    self.patientId = "pat-" + patientId
                    print(self.patientId)
                }
                
                self.idToken = tokens.idToken
            }
            
            isAuthenticated = session.isSignedIn
        } catch let error as AuthError {
            print("Fetch auth session failed with error - \(error)")
        } catch {
            print("Unexpected error: \(error)")
        }
    }
}

class DeepLinkURL: ObservableObject {
    @Published var url: URL?
}

class CustomAppDelegate: NSObject, UIApplicationDelegate {
    @Published var deepLinkURL: DeepLinkURL = DeepLinkURL()

    func application(_ app: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Handle deep link URL when the app is launched
        if let url = launchOptions?[.url] as? URL {
            deepLinkURL.url = url
        }
        return true
    }

    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey: Any] = [:]) -> Bool {
        // Handle deep link URL when the app is already running
        deepLinkURL.url = url
        return true
    }
}
