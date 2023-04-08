//
//  MobilityView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-01-24.
//

import SwiftUI
import HealthKit

struct MobilityView: View {
    @ObservedObject var healthStore = HealthStore()
    @Binding var isAuthenticated: Bool
    @Binding var patientId: String
    @Binding var idToken: String
    @Binding var hasCaregivers: Bool
    @State private var isShowingAddMetricsResult: Bool = false
    @State private var addMetricsMessage: String = ""
    @State private var selectedView: Int? = nil
    @EnvironmentObject var deepLinkURL: DeepLinkURL
    
    func displayAddMetricsResult(message: String) {
        self.isShowingAddMetricsResult = true
        self.addMetricsMessage = message
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            self.isShowingAddMetricsResult = false
            self.addMetricsMessage = ""
        }
    }
    
    var body: some View {
        NavigationView {
            ZStack {
                VStack(spacing: 50) {
                    HStack {
                        Text("Mobility")
                            .font(.largeTitle)
                        
                        Spacer()
                        NavigationLink("", destination: VerificationView(isAuthenticated: $isAuthenticated, patientId: $patientId, idToken: $idToken, hasCaregivers: $hasCaregivers), tag: 1, selection: $selectedView)
                            .opacity(0)
                        Spacer()
                        
                        Menu {
                            Button(action: {
                                selectedView = 1
                            }) {
                                Label("Verification", systemImage: "lock.shield")
                            }
                            LogoutButtonView(isAuthenticated: $isAuthenticated)
                        } label: {
                            Image(systemName: "ellipsis.circle")
                                .font(.system(size: 25))
                                .foregroundColor(MobimonTheme.purple)
                        }
                    }
                    VStack {
                        ForEach(healthStore.metrics, id: \.self) { metric in
                            GeometryReader { geo in
                                RoundedRectangle(cornerRadius: CardStyling.cornerRadius)
                                    .fill(CardStyling.foreGroundColor)
                                    .shadow(radius: CardStyling.shadow)
                                    .frame(height: geo.size.height * 0.9)
                                    .overlay(
                                        HStack {
                                            Image(metric.logo)
                                                .resizable()
                                                .scaledToFit()
                                            VStack(alignment: .leading) {
                                                Text(metric.name)
                                                Text(metric.lastUpdated)
                                            }
                                            Spacer()
                                            Text("\(metric.value)")
                                        }
                                        .padding(.trailing, 16)
                                    )
                            }
                        }
                    }.onAppear {
                        healthStore.setUpHealthStore()
                    }.onChange(of: deepLinkURL.url) { newUrlValue in
                        print(deepLinkURL)
                        if deepLinkURL.url != nil {
                            print("asdfasdfasdf")
                            // move to verification page
                            selectedView = 1;
                        }
                    }
                    
                    Button(action: {
                        let metricsData = healthStore.createMetricsData()
                        addMetrics(idToken: idToken, patientId: patientId, metrics: [metricsData]) { result in
                            switch result {
                            case .success(let responseObject):
                                displayAddMetricsResult(message: "Success")
                                print("Successfully added metrics: \(responseObject)")
                            case .failure(let error):
                                displayAddMetricsResult(message: "Failed to send metrics, please contact us!")
                                print("Failed to add metrics: \(error)")
                            }
                        }
                    }) {
                        Text("Send Data")
                            .font(ButtonStyling.font)
                            .foregroundColor(ButtonStyling.foreGroundColor)
                            .padding()
                            .frame(minWidth: 0, maxWidth: .infinity)
                            .background(ButtonStyling.color)
                            .cornerRadius(ButtonStyling.cornerRadius)
                    }
                }
                .padding(.horizontal, 32)
                
                if isShowingAddMetricsResult {
                    let maxPopUpHeight = self.addMetricsMessage == "Success" ? 0.1 : 0.2

                    GeometryReader { geometry in
                        VStack {
                            RoundedRectangle(cornerRadius: 12)
                                .fill(Color.white)
                                .shadow(radius: 10)
                                .overlay(
                                    VStack(alignment: .center) {
                                        if self.addMetricsMessage == "Success" {
                                            Text(self.addMetricsMessage)
                                                .font(.body)
                                                .foregroundColor(.green)
                                                .padding()
                                        } else {
                                            Text(self.addMetricsMessage)
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
