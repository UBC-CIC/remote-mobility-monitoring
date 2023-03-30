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
        let doubleSupportTimeStandard = HKQuantity(unit: HKUnit(from: "min"), doubleValue: 0.15)
        let walkingSpeedStandard = HKQuantity(unit: HKUnit(from: "m/s"), doubleValue: 20.0)
        let walkingAsymmetryStandard = HKQuantity(unit: HKUnit.percent(), doubleValue: 0.083)
        let distanceWalkedStandard = HKQuantity(unit: HKUnit.meter(), doubleValue: 1563.0)
        
        DispatchQueue.main.async {
            self.metrics = [
                Metric(name: "Step Count", lastUpdated: "Today", value: stepCountStandard, logo: "stepCount"),
                Metric(name: "Step Length", lastUpdated: "Today", value: self.walkingStepLength ?? stepLengthStandard, logo: "stepLength"),
                Metric(name: "Double Support Time", lastUpdated: "Today", value: self.walkingDoubleSupportPercentage ?? doubleSupportTimeStandard, logo: "doubleSupportTime"),
                Metric(name: "Walking Speed", lastUpdated: "Today", value: self.walkingSpeed ?? walkingSpeedStandard, logo: "walkingSpeed"),
                Metric(name: "Walking Asymmetry", lastUpdated: "Today", value: self.walkingAsymmetryPercentage ?? walkingAsymmetryStandard, logo: "walkingAsymmetry"),
                Metric(name: "Distance Walked", lastUpdated: "Today", value: distanceWalkedStandard, logo: "distanceWalked")
            ]
        }
    }
}

struct MobilityView: View {
    @ObservedObject var healthStore = HealthStore()
    
    var body: some View {
        VStack(spacing: 50) {
            Text("Mobility")
                .font(.largeTitle)
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
            }
            
            Button(action: {
                // Send data action
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
    }
}

struct MobilityView_Previews: PreviewProvider {
    static var previews: some View {
        MobilityView()
    }
}


