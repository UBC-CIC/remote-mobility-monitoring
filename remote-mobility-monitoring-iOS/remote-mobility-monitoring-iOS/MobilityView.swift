//
//  MobilityView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-01-24.
//

import SwiftUI
import HealthKit

struct Metric: Hashable {
    var name: String
    var lastUpdated: String
    var value: HKQuantity
    var logo: String
}

class HealthStore: ObservableObject {
    var healthStore: HKHealthStore?
    var query: HKStatisticsQuery?
    @Published var stepCount: HKQuantity?
    @Published var walkingStepLength: HKQuantity?
    @Published var walkingDoubleSupportPercentage: HKQuantity?
    @Published var walkingSpeed: HKQuantity?
    @Published var walkingAsymmetryPercentage: HKQuantity?
    @Published var distanceWalkingRunning: HKQuantity?
    @Published var metrics: [Metric] = []
    
    init() {
        if HKHealthStore.isHealthDataAvailable() {
            healthStore = HKHealthStore()
        }
    }

    func setUpHealthStore() {
        let typesToRead: Set = [
            HKObjectType.quantityType(forIdentifier: .stepCount)!,
            HKObjectType.quantityType(forIdentifier: .walkingStepLength)!,
            HKObjectType.quantityType(forIdentifier: .walkingDoubleSupportPercentage)!,
            HKObjectType.quantityType(forIdentifier: .walkingSpeed)!,
            HKObjectType.quantityType(forIdentifier: .walkingAsymmetryPercentage)!,
            HKObjectType.quantityType(forIdentifier: .distanceWalkingRunning)!,
        ]
        healthStore?.requestAuthorization(toShare: nil, read: typesToRead, completion: { success, error in
            if success {
                self.retrieveMetric(name: "Step Count", typeIdentifier: .stepCount)
                self.retrieveMetric(name: "Step Length", typeIdentifier: .walkingStepLength)
                self.retrieveMetric(name: "Double Support Time", typeIdentifier: .walkingDoubleSupportPercentage)
                self.retrieveMetric(name: "Walking Speed", typeIdentifier: .walkingSpeed)
                self.retrieveMetric(name: "Walking Asymmetry", typeIdentifier: .walkingAsymmetryPercentage)
                self.retrieveMetric(name: "Distance Walked", typeIdentifier: .distanceWalkingRunning)
                self.updateMetrics()
            }
        })
    }

    func retrieveMetric(name: String, typeIdentifier: HKQuantityTypeIdentifier) {
        guard let quantityType = HKQuantityType.quantityType(forIdentifier: typeIdentifier) else {
            return
        }
                
        let now = Date()
        let startOfDay = Calendar.current.startOfDay(for: now)
        let predicate = HKQuery.predicateForSamples(withStart: startOfDay, end: now, options: .strictEndDate)
        
        if(name != "Distance Walked" && name != "Step Count") {
            let query = HKStatisticsQuery(quantityType: quantityType,
                                        quantitySamplePredicate: predicate,
                                        options: .discreteAverage) {
                (query, result, error) in
                DispatchQueue.main.async {
                    guard let result = result, error == nil else {
                        return
                    }
                
                    switch name {
                        case "Step Length":
                            self.walkingStepLength = result.averageQuantity()!
                        case "Double Support Time":
                            self.walkingDoubleSupportPercentage = result.averageQuantity()!
                        case "Walking Speed":
                            self.walkingSpeed = result.averageQuantity()!
                        case "Walking Asymmetry":
                            self.walkingAsymmetryPercentage = result.averageQuantity()!
                        default:
                            return
                    }
                }
                self.updateMetrics()
            }
            self.healthStore!.execute(query)
        } else {
            let query = HKStatisticsQuery(quantityType: quantityType,
                                        quantitySamplePredicate: nil,
                                        options: .cumulativeSum) {
                (query, result, error) in
                DispatchQueue.main.async {
                    guard let result = result, error == nil else {
                        return
                    }
                    
                    switch name {
                        case "Distance Walked":
                            self.distanceWalkingRunning = result.sumQuantity()!
                        case "Step Count":
                            self.stepCount = result.sumQuantity()!
                        default:
                            return
                    }
                    
                }
                self.updateMetrics()
            }
            self.healthStore!.execute(query)
        }
    }
    
    func updateMetrics() {
        // Initialize all the data to some arbitrary mock data if data is not available in the store
        let stepCountStandard = HKQuantity(unit: HKUnit.count(), doubleValue: 2348)
        let stepLengthStandard = HKQuantity(unit: HKUnit(from: "cm"), doubleValue: 10.0)
        let doubleSupportTimeStandard = HKQuantity(unit: HKUnit.percent(), doubleValue: 0.15)
        let walkingSpeedStandard = HKQuantity(unit: HKUnit(from: "m/s"), doubleValue: 20.0)
        let walkingAsymmetryStandard = HKQuantity(unit: HKUnit.percent(), doubleValue: 0.083)
        let distanceWalkingRunningStandard = HKQuantity(unit: HKUnit.meter(), doubleValue: 1563.0)
        
        DispatchQueue.main.async {
            self.metrics = [
                Metric(name: "Step Count", lastUpdated: "Today", value: self.stepCount ?? stepCountStandard, logo: "stepCount"),
                Metric(name: "Step Length", lastUpdated: "Today", value: self.walkingStepLength ?? stepLengthStandard, logo: "stepLength"),
                Metric(name: "Double Support Time", lastUpdated: "Today", value: self.walkingDoubleSupportPercentage ?? doubleSupportTimeStandard, logo: "doubleSupportTime"),
                Metric(name: "Walking Speed", lastUpdated: "Today", value: self.walkingSpeed ?? walkingSpeedStandard, logo: "walkingSpeed"),
                Metric(name: "Walking Asymmetry", lastUpdated: "Today", value: self.walkingAsymmetryPercentage ?? walkingAsymmetryStandard, logo: "walkingAsymmetry"),
                Metric(name: "Distance Walked", lastUpdated: "Today", value: self.distanceWalkingRunning ?? distanceWalkingRunningStandard, logo: "distanceWalked")
            ]
        }
    }
}

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
    
    func getCurrentTimestamp() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        let timestamp = dateFormatter.string(from: Date())
        return timestamp
    }

    func createMetricsData() -> [String: Any] {
        var metricsData: [String: Any] = [:]
        
        for metric in healthStore.metrics {
            switch metric.name {
            case "Step Count":
                metricsData["step_count"] = metric.value.doubleValue(for: HKUnit.count())
            case "Step Length":
                metricsData["step_length"] = metric.value.doubleValue(for: HKUnit(from: "cm"))
            case "Double Support Time":
                metricsData["double_support_time"] = metric.value.doubleValue(for: HKUnit.percent())
            case "Walking Speed":
                metricsData["walking_speed"] = metric.value.doubleValue(for: HKUnit(from: "m/s"))
            case "Walking Asymmetry":
                metricsData["walking_asymmetry"] = metric.value.doubleValue(for: HKUnit.percent())
            case "Distance Walked":
                metricsData["distance_walked"] = metric.value.doubleValue(for: HKUnit.meter())
            default:
                break
            }
        }
        
        metricsData["timestamp"] = getCurrentTimestamp()
        
        return metricsData
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
                                    .frame(height: geo.size.height * 0.8)
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
                                    )
                            }
                        }
                    }.onAppear {
                        healthStore.setUpHealthStore()
                    }.onChange(of: deepLinkURL.url) { newUrlValue in
                        if let url = deepLinkURL.url {
                            // move to verification page
                            selectedView = 2;
                        }
                    }
                    
                    Button(action: {
                        let metricsData = createMetricsData()
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
