//
//  RegisterView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-01-09.
//
import SwiftUI
import HealthKit
import CodeScanner

struct VerificationView: View {
    @State private var isScanning = false
    @State private var authCode: String = ""
    @State private var caregiverId: String = ""
    @State private var verifyMessage: String = ""
    @State private var isShowingVerifyingResult: Bool = false
    @State private var verified: Bool = false
    @State private var selection = 0
    @Binding var isAuthenticated: Bool
    @Binding var patientId: String
    @Binding var idToken: String
    @Binding var hasCaregivers: Bool
    @EnvironmentObject var deepLinkURL: DeepLinkURL
    
    func displayVerifyingResult(verifyLoading: Bool) {
        var displayTime = DispatchTimeInterval.seconds(3)
        if verifyLoading {
            displayTime = DispatchTimeInterval.seconds(6)
        }
        self.isShowingVerifyingResult = true
        DispatchQueue.main.asyncAfter(deadline: .now() + displayTime) {
            if(self.verifyMessage == "Success") {
                self.hasCaregivers = true
            }
            self.isShowingVerifyingResult = false
            self.verifyMessage = ""
        }
    }
    
    func handleScanResult(result: Result<ScanResult, ScanError>) {
        self.isScanning = false
        self.isShowingVerifyingResult = true
        self.verifyMessage = "Verifying..."
        
        switch result {
        case .success(let result):
            let jsonString = result.string
            let jsonData = jsonString.data(using: .utf8)!

            do {
                let jsonObject = try JSONSerialization.jsonObject(with: jsonData, options: []) as? [String: String]
                self.authCode = jsonObject?["auth_code"] ?? ""
                self.caregiverId = jsonObject?["caregiver_id"] ?? ""
                
                if (self.patientId != "" && authCode != "" && caregiverId != "" && self.idToken != "") {
                    verifyPatient(patientId: self.patientId, caregiverId: caregiverId, authCode: authCode, idToken: self.idToken) { result in
                        switch result {
                            case .success(let response):
                                self.verifyMessage = "Success"
                            self.displayVerifyingResult(verifyLoading: false)
                                print(response)
                            case .failure(let error):
                                self.verifyMessage = "Failed to verify new caregiver. Please double-check the QR code!"
                                self.displayVerifyingResult(verifyLoading: false)
                                print("Verification failed: \(error.localizedDescription)")
                        }
                    }
                } else {
                    if self.patientId == "" {
                        self.verifyMessage = "QR code contains insufficient data. Failed to verify!"
                    } else if authCode == "" {
                        self.verifyMessage = "QR code contains insufficient data. Failed to verify!"
                    } else if caregiverId == "" {
                        self.verifyMessage = "QR code contains insufficient data. Failed to verify!"
                    } else if self.idToken == "" {
                        self.verifyMessage = "You are not authenticated. Failed to verify!"
                    } else {
                        self.verifyMessage = "Unknown error occurred. Failed to verify."
                    }
                    
                    self.displayVerifyingResult(verifyLoading: false)
                }
            } catch {
                self.verifyMessage = "Failed to read QR code: \(error.localizedDescription)"
                print("Error while parsing JSON: \(error.localizedDescription)")
            }
            
        case .failure(let error):
            self.verifyMessage = "Scanning failed: \(error.localizedDescription)"
            print("Scanning failed: \(error.localizedDescription)")
        }
    }
    
    var body: some View {
        NavigationView {
            ZStack {
                VStack {
                    HStack {
                        Text("Verification")
                            .font(.largeTitle)
                        
                        Spacer()
                        
                        LogoutButtonView(isAuthenticated: $isAuthenticated)
                    }
                    
                    Spacer()
                    
                    Text("Verify a new caregiver remotely:")
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
                                Text("Verify a new caregiver in person:")
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
                }
                .padding(.horizontal, 32)
                .sheet(isPresented: $isScanning) {
                    CodeScannerView(codeTypes: [.qr], completion: handleScanResult(result:))
                }
                .onAppear() {
                    if let url = deepLinkURL.url {
                        self.verifyMessage = "Verifying..."
                        self.displayVerifyingResult(verifyLoading: true)
                        
                        // trigger the link change again
                        deepLinkURL.url = nil
                        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                            deepLinkURL.url = url
                        }
                    }
                }
                .onChange(of: deepLinkURL.url) { newUrlValue in
                    if let url = deepLinkURL.url {
                        self.verifyMessage = "Verifying..."
                        self.displayVerifyingResult(verifyLoading: true)
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
                                    self.displayVerifyingResult(verifyLoading: false)
                                    print(response)
                                case .failure(let error):
                                    self.verifyMessage = "Failed to verify new caregiver. Please double-check the link!"
                                    self.displayVerifyingResult(verifyLoading: false)
                                    print("Verification failed: \(error.localizedDescription)")
                                }
                            }
                        } else {
                            self.verifyMessage = "Link is not formatted correctly"
                            self.displayVerifyingResult(verifyLoading: false)
                        }
                    }
                    
                    deepLinkURL.url = nil
                }
                
                if isShowingVerifyingResult {
                    let maxPopUpHeight = self.verifyMessage == "Success" || self.verifyMessage == "Verifying..." ? 0.1 : 0.2

                    GeometryReader { geometry in
                        VStack {
                            RoundedRectangle(cornerRadius: 12)
                                .fill(Color.white)
                                .shadow(radius: 10)
                                .overlay(
                                    VStack(alignment: .center) {
                                        if self.verifyMessage == "Success" {
                                            Text(self.verifyMessage)
                                                .font(.body)
                                                .foregroundColor(.green)
                                                .padding()
                                        } else if self.verifyMessage == "Verifying..."{
                                            Text(self.verifyMessage)
                                                .font(.body)
                                                .foregroundColor(.black)
                                                .padding()
                                        } else {
                                            Text(self.verifyMessage)
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
    }
}
