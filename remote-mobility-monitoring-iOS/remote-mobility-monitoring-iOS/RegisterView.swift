//
//  RegisterView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-01-09.
//
import SwiftUI
import HealthKit

struct RegisterView: View {
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
        }
    }
}

struct RegisterView_Previews: PreviewProvider {
    static var previews: some View {
        RegisterView()
    }
}
