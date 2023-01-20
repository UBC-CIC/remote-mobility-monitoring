//
//  RegisterView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-01-09.
//
import SwiftUI
import AVFoundation

struct RegisterView: View {
    // Variable to hold the scanned QR code data
    @State var scannedQRCode: String = ""
    @State private var captureSession: AVCaptureSession?
    @State private var metadataOutput: AVCaptureMetadataOutput?

    var body: some View {
        VStack(spacing: 16) {
            Text("Register")
                .font(.largeTitle)

            // Other UI elements, such as fields for the user's name and email address, go here.

            Spacer()
            
            GeometryReader { geometry in
                HStack {
                    Spacer()
                    VStack {
                        Spacer()
                        Button(action: {
                            // Start scanning for QR code
                            self.startScanning()
                        }) {
                            Text("Scan QR Code")
                                .font(.headline)
                                .foregroundColor(.white)
                                .padding()
                                .frame(minWidth: 0,maxWidth: (geometry.size.width * 2/3))
                                .background(Color(UIColor(red: 54/255, green: 51/255, blue: 140/255, alpha: 1.0)))
                                .cornerRadius(25)
                        }
                        Spacer()
                    }
                    Spacer()
                }
            }

            Spacer()
            
            Button(action: {
                // Action for submitting the form goes here.
            }) {
                Text("Submit")
                    .font(.headline)
                    .foregroundColor(.white)
                    .padding()
                    .frame(minWidth: 0, maxWidth: .infinity)
                    .background(Color(UIColor(red: 54/255, green: 51/255, blue: 140/255, alpha: 1.0)))
                    .cornerRadius(25)
            }
        }
        .padding(.horizontal, 32)
    }
}

struct ContentView_Previews2: PreviewProvider {
    static var previews: some View {
        RegisterView()
    }
}
