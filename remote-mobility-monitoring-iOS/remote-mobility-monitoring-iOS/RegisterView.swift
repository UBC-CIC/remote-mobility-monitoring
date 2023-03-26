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
    @State private var deviceId: String = ""
    @State private var scanningMessage: String = ""
    @State private var errorScanning: Bool = false
    @State private var isShowingScanningResult: Bool = false
    @State private var verified: Bool = false
    let keychain = Keychain(service: "com.example.remote-mobility-monitoring-iOS")
    
    func getDeviceId() -> String {
        if let storedDeviceId = self.keychain["deviceId"] {
            return storedDeviceId
        } else {
            guard let newDeviceId = UIDevice.current.identifierForVendor?.uuidString else {
                errorScanning = true
                scanningMessage = "Failed to retrieve deviceId from UIDevice, please contact developers"
                return ""
            }
            
            do {
                try keychain.set("deviceId", key: newDeviceId)
            } catch {
                errorScanning = true
                scanningMessage = "Failed to persist deviceId, please contact developers"
            }
            
            return newDeviceId
        }
    }
    
    func displayScanningResult() {
        self.isShowingScanningResult = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            if(self.scanningMessage == "Success") {
                self.verified = true
                
            }
            self.isShowingScanningResult = false
            self.errorScanning = false
            self.scanningMessage = ""
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
                deviceId = getDeviceId()
                
                if (errorScanning == false && patientId != "" && authCode != "" && caregiverId != "" && deviceId != "") {
                    verifyPatient(patientId: patientId, caregiverId: caregiverId, authCode: authCode, deviceId: deviceId) { result in
                        switch result {
                            case .success(let response):
                                self.scanningMessage = "Success"
                                self.displayScanningResult()
                                print(response)
                            case .failure(let error):
                                self.errorScanning = true
                                self.scanningMessage = "Failed to verify new patient!"
                                self.displayScanningResult()
                                print("Verification failed: \(error.localizedDescription)")
                        }
                    }
                } else {
                    self.errorScanning = true
                    if patientId == "" {
                        self.scanningMessage = "Failed to get patientId from QR code. Failed to verify."
                    } else if authCode == "" {
                        self.scanningMessage = "Failed to get authCode from QR code. Failed to verify."
                    } else if caregiverId == "" {
                        self.scanningMessage = "Failed to get caregiverId from QR code. Failed to verify."
                    } else if deviceId == "" {
                        self.scanningMessage = "Failed to get deviceId from. Failed to verify."
                    } else {
                        self.scanningMessage = "Unknown error occurred. Failed to verify."
                    }
                    
                    self.displayScanningResult()
                }
            } catch {
                errorScanning = true
                scanningMessage = "Failed to read QR code: \(error.localizedDescription)"
                print("Error while parsing JSON: \(error.localizedDescription)")
            }
            
        case .failure(let error):
            errorScanning = true
            scanningMessage = "Scanning failed: \(error.localizedDescription)"
            print("Scanning failed: \(error.localizedDescription)")
        }
    }

    var body: some View {
        if !verified {
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
                    Text("Device Id: \(deviceId)")
                        .font(.headline)
                    if isShowingScanningResult {
                        if scanningMessage == "Success" {
                            Text(scanningMessage)
                                .font(.body)
                                .foregroundColor(.green)
                                .frame(minWidth: 0, maxWidth: .infinity, alignment: .center)
                        } else {
                            Text(scanningMessage)
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
        } else {
            MobilityView()
        }
    }
}
