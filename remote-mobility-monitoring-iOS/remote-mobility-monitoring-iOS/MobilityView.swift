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
            HKObjectType.quantityType(forIdentifier: .walkingDoubleSupportPercentage)!,
            HKObjectType.quantityType(forIdentifier: .walkingSpeed)!,
            HKObjectType.quantityType(forIdentifier: .walkingAsymmetryPercentage)!,
            HKObjectType.quantityType(forIdentifier: .distanceWalkingRunning)!,
        ]
        healthStore?.requestAuthorization(toShare: nil, read: typesToRead, completion: { success, error in
            if success {
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
        
        if(name != "Distance Walked") {
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
                
                    self.distanceWalkingRunning = result.sumQuantity()!
                }
                self.updateMetrics()
            }
            self.healthStore!.execute(query)
        }
    }
    
    func updateMetrics() {
        let stepLengthStandard = HKQuantity(unit: HKUnit(from: "cm"), doubleValue: 10.0)
        let doubleSupportTimeStandard = HKQuantity(unit: HKUnit(from: "min"), doubleValue: 0.15)
        let walkingSpeedStandard = HKQuantity(unit: HKUnit(from: "m/s"), doubleValue: 20.0)
        let walkingAsymmetryStandard = HKQuantity(unit: HKUnit.percent(), doubleValue: 0.1)
        let distanceWalkedStandard = HKQuantity(unit: HKUnit.meter(), doubleValue: 20.0)
        
        DispatchQueue.main.async {
            self.metrics = [
                Metric(name: "Step Length", lastUpdated: "Today", value: self.walkingStepLength ?? stepLengthStandard, logo: "StepLength"),
                Metric(name: "Double Support Time", lastUpdated: "Today", value: self.walkingDoubleSupportPercentage ?? doubleSupportTimeStandard, logo: "DoubleSupportTime"),
                Metric(name: "Walking Speed", lastUpdated: "Today", value: self.walkingSpeed ?? walkingSpeedStandard, logo: "WalkingSpeed"),
                Metric(name: "Walking Asymmetry", lastUpdated: "Today", value: self.walkingAsymmetryPercentage ?? walkingAsymmetryStandard, logo: "WalkingAsymmetry"),
                Metric(name: "Distance Walked", lastUpdated: "Today", value: self.distanceWalkingRunning ?? distanceWalkedStandard, logo: "DistanceWalked")
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
                        RoundedRectangle(cornerRadius: 25)
                            .fill(Color.white)
                            .shadow(radius: 5)
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

struct MobilityView_Previews: PreviewProvider {
    static var previews: some View {
        MobilityView()
    }
}


