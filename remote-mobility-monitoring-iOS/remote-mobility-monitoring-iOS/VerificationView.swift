//
//  RegisterView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-01-09.
//
import SwiftUI
import HealthKit
import CodeScanner
import KeychainAccess

struct VerificationView: View {
    @State private var isScanning = false
    @State private var authCode: String = ""
    @State private var caregiverId: String = ""
    @State private var deviceId: String = ""
    @State private var verifyMessage: String = ""
    @State private var errorScanning: Bool = false
    @State private var errorDeepLinking: Bool = false
    @State private var isShowingScanningResult: Bool = false
    @State private var isShowingDeepLinkingResult: Bool = false
    @State private var verified: Bool = false
    @State private var selection = 0
    @Binding var isAuthenticated: Bool
    @EnvironmentObject var deepLinkURL: DeepLinkURL
    @Binding var patientId: String
    @Binding var idToken: String
    
    func displayVerifyingResult() {
        self.isShowingScanningResult = true
        self.isShowingDeepLinkingResult = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            if(self.verifyMessage == "Success") {
                self.verified = true
            } else {
                self.verified = false
            }
            self.isShowingScanningResult = false
            self.isShowingDeepLinkingResult = false
            self.errorScanning = false
            self.errorDeepLinking = false
            self.verifyMessage = ""
        }
    }
    
    func handleScanResult(result: Result<ScanResult, ScanError>) {
        self.isScanning = false
        
        switch result {
        case .success(let result):
            let jsonString = result.string
            let jsonData = jsonString.data(using: .utf8)!

            do {
                let jsonObject = try JSONSerialization.jsonObject(with: jsonData, options: []) as? [String: String]
                self.authCode = jsonObject?["auth_code"] ?? ""
                self.caregiverId = jsonObject?["caregiver_id"] ?? ""
                
                if (errorScanning == false && self.patientId != "" && authCode != "" && caregiverId != "" && deviceId != "" && self.idToken != "") {
                    verifyPatient(patientId: self.patientId, caregiverId: caregiverId, authCode: authCode, idToken: self.idToken) { result in
                        switch result {
                            case .success(let response):
                                self.verifyMessage = "Success"
                                self.displayVerifyingResult()
                                print(response)
                            case .failure(let error):
                                self.errorScanning = true
                                self.verifyMessage = "Failed to verify new patient!"
                                self.displayVerifyingResult()
                                print("Verification failed: \(error.localizedDescription)")
                        }
                    }
                } else {
                    self.errorScanning = true
                    if self.patientId == "" {
                        self.verifyMessage = "Failed to get patientId from QR code. Failed to verify."
                    } else if authCode == "" {
                        self.verifyMessage = "Failed to get authCode from QR code. Failed to verify."
                    } else if caregiverId == "" {
                        self.verifyMessage = "Failed to get caregiverId from QR code. Failed to verify."
                    } else if deviceId == "" {
                        self.verifyMessage = "Failed to get deviceId from. Failed to verify."
                    } else if self.idToken == "" {
                        self.verifyMessage = "Failed to authenticate. Failed to verify."
                    } else {
                        self.verifyMessage = "Unknown error occurred. Failed to verify."
                    }
                    
                    self.displayVerifyingResult()
                }
            } catch {
                self.errorScanning = true
                self.verifyMessage = "Failed to read QR code: \(error.localizedDescription)"
                print("Error while parsing JSON: \(error.localizedDescription)")
            }
            
        case .failure(let error):
            self.errorScanning = true
            self.verifyMessage = "Scanning failed: \(error.localizedDescription)"
            print("Scanning failed: \(error.localizedDescription)")
        }
    }
    
    var body: some View {
        NavigationView {
            if !verified {
                VStack {
                    HStack {
                        Text("Verification")
                            .font(.largeTitle)
                        
                        Spacer()
                        
                        NavigationLink(destination: AccountView(isAuthenticated: $isAuthenticated)) {
                            Image(systemName: "person.circle")
                                .foregroundColor(.blue)
                        }
                    }
                    
                    Spacer()
                    
                    Text("Verify your account remotely:")
                        .font(.system(size: 20, weight: .bold))
                        .padding(.bottom, 18)
                        .padding(.top, 50)
                    
                    Text("Contact your caregiver for a verification email!")
                        .font(.system(size: 20))
                        .multilineTextAlignment(.center)
                    
                    GeometryReader { geometry in
                        HStack {
                            Spacer()
                            VStack {
                                Spacer()
                                Text("Verify your account in person:")
                                    .font(.system(size: 20, weight: .bold))
                                    .padding(.bottom, 20)
                                    .padding(.top, 50)
                                Button(action: {
                                    self.isScanning = true
                                }) {
                                    Text("Scan QR Code")
                                        .font(ButtonStyling.font)
                                        .foregroundColor(ButtonStyling.foreGroundColor)
                                        .padding()
                                        .frame(minWidth: 0,maxWidth: (geometry.size.width * 2/3))
                                        .background(ButtonStyling.color)
                                        .cornerRadius(ButtonStyling.cornerRadius)
                                }
                                Spacer()
                            }
                            Spacer()
                        }
                    }
                    
                    Spacer()
                    
                    VStack(alignment: .leading) {
                        if isShowingScanningResult {
                            if self.verifyMessage == "Success" {
                                Text(self.verifyMessage)
                                    .font(.body)
                                    .foregroundColor(.green)
                                    .frame(minWidth: 0, maxWidth: .infinity, alignment: .center)
                            } else {
                                Text(self.verifyMessage)
                                    .font(.body)
                                    .foregroundColor(.red)
                                    .frame(minWidth: 0, maxWidth: .infinity, alignment: .center)
                            }
                        }
                    }
                    .padding()
                }
                .padding(.horizontal, 32)
                .sheet(isPresented: $isScanning) {
                    CodeScannerView(codeTypes: [.qr], completion: handleScanResult(result:))
                }
                .onChange(of: deepLinkURL.url) { newUrlValue in
                    if let url = deepLinkURL.url {
                        self.errorDeepLinking = false
                        if let urlComponents = URLComponents(url: url, resolvingAgainstBaseURL: false),
                           let authCode = urlComponents.queryItems?.first(where: { $0.name == "authCode" })?.value,
                           let caregiverId = urlComponents.queryItems?.first(where: { $0.name == "caregiverId" })?.value {
                            self.authCode = authCode
                            self.caregiverId = caregiverId
                            
                            print(authCode)
                            print(caregiverId)
                            print(self.patientId)
                            
                            verifyPatient(patientId: self.patientId, caregiverId: caregiverId, authCode: authCode, idToken: self.idToken) { result in
                                switch result {
                                case .success(let response):
                                    self.verifyMessage = "Success"
                                    self.displayVerifyingResult()
                                    print(response)
                                case .failure(let error):
                                    self.errorDeepLinking = true
                                    self.verifyMessage = "Failed to verify new patient!"
                                    self.displayVerifyingResult()
                                    print("Verification failed: \(error.localizedDescription)")
                                }
                            }
                        } else {
                            self.errorDeepLinking = true
                            self.verifyMessage = "Link is not formatted correctly"
                            self.displayVerifyingResult()
                        }
                    }
                    
                    // reset
                    deepLinkURL.url = nil
                }
            } else {
                MobilityView()
            }
        }
    }
}
