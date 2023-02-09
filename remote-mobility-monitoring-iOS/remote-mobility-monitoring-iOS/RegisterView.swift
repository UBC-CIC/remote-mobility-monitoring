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

struct RegisterView: View {
    @State private var isScanning = false
    @State private var patientId: String = ""
    @State private var authCode: String = ""
    @State private var caregiverId: String = ""
    @State private var deviceIdDebug: String = ""
    @State private var errorMessage: String = ""
    @State private var errorScanning: Bool = false
    let keychain = Keychain(service: "com.example.remote-mobility-monitoring-iOS")
    
    func getDeviceId() -> String {
        if let storedDeviceId = self.keychain["deviceId"] {
            return storedDeviceId
        } else {
            guard let newDeviceId = UIDevice.current.identifierForVendor?.uuidString else {
                errorScanning = true
                errorMessage = "Failed to retrieve deviceId from UIDevice, please contact developers"
                return ""
            }
            
            do {
                try keychain.set("deviceId", key: newDeviceId)
            } catch {
                errorScanning = true
                errorMessage = "Failed to persist deviceId, please contact developers"
            }
            
            return newDeviceId
        }
    }
    
    func handleScanResult(result: Result<ScanResult, ScanError>) {
        isScanning = false
        
        switch result {
        case .success(let result):
            let jsonString = result.string
            
            let jsonData = jsonString.data(using: .utf8)!

            do {
                let jsonObject = try JSONSerialization.jsonObject(with: jsonData, options: []) as? [String: String]
                patientId = jsonObject?["patient_id"] ?? ""
                authCode = jsonObject?["auth_code"] ?? ""
                caregiverId = jsonObject?["caregiver_id"] ?? ""
                let deviceId = getDeviceId()
                
                if (errorScanning == false && patientId != "" && authCode != "" && caregiverId != "" && deviceId != "") {
                    verifyPatient(patientId: patientId, caregiverId: caregiverId, authCode: authCode, deviceId: deviceId) { result in
                        switch result {
                            case .success(let response):
                                print(response)
                            case .failure(let error):
                                errorScanning = true
                                errorMessage = "Failed to verify patient: \(error.localizedDescription)"
                                print("Verification failed: \(error.localizedDescription)")
                        }
                    }
                } else {
                    errorScanning = true
                    if patientId == "" {
                        errorMessage = "Failed to get patientId from QR code. Failed to verify."
                    } else if authCode == "" {
                        errorMessage = "Failed to get authCode from QR code. Failed to verify."
                    } else if caregiverId == "" {
                        errorMessage = "Failed to get caregiverId from QR code. Failed to verify."
                    } else if deviceId == "" {
                        errorMessage = "Failed to get deviceId from. Failed to verify."
                    } else {
                        errorMessage = "Unknown error occurred. Failed to verify."
                    }
                }
            } catch {
                errorScanning = true
                errorMessage = "Failed to read QR code: \(error.localizedDescription)"
                print("Error while parsing JSON: \(error.localizedDescription)")
            }
            
        case .failure(let error):
            errorScanning = true
            errorMessage = "Scanning failed: \(error.localizedDescription)"
            print("Scanning failed: \(error.localizedDescription)")
        }
    }

    var body: some View {
        NavigationView {
            VStack() {
                Text("Register")
                    .font(.largeTitle)
                Spacer()
                GeometryReader { geometry in
                    HStack {
                        Spacer()
                        VStack {
                            Spacer()
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
                    Text("Patient ID: \(patientId)")
                        .font(.headline)
                    Text("Auth Code: \(authCode)")
                        .font(.headline)
                    Text("Caregiver ID: \(caregiverId)")
                        .font(.headline)
                    Text("Device Id: \(deviceIdDebug)")
                        .font(.headline)
                    if errorScanning {
                        Text(errorMessage)
                            .font(.body)
                            .foregroundColor(.red)
                    }
                }
                .padding()
                NavigationLink(destination: MobilityView()) {
                    Text("Submit")
                        .font(ButtonStyling.font)
                        .foregroundColor(ButtonStyling.foreGroundColor)
                        .padding()
                        .frame(minWidth: 0, maxWidth: .infinity)
                        .background(ButtonStyling.color)
                        .cornerRadius(ButtonStyling.cornerRadius)
                }
            }
            .padding(.horizontal, 32)
            .sheet(isPresented: $isScanning) {
                CodeScannerView(codeTypes: [.qr], completion: handleScanResult(result:))
            }
        }
    }
}
