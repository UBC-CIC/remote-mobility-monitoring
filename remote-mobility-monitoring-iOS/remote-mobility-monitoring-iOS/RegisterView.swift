//
//  RegisterView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-01-09.
//
import SwiftUI

struct RegisterView: View {
    var body: some View {
        VStack(spacing: 16) {
            Text("Register")
                .font(.largeTitle)
            Spacer()
            GeometryReader { geometry in
                HStack {
                    Spacer()
                    VStack {
                        Spacer()
                        Button(action: {
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
                // Action for submitting after scanning QR code
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

struct RegisterView_Previews: PreviewProvider {
    static var previews: some View {
        RegisterView()
    }
}
