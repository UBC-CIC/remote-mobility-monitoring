//
//  HealthStore.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-04-07.
//

import Foundation
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
    @Published var walkingSteadiness: HKQuantity?
    @Published var metrics: [Metric] = []
    
    init() {
        if HKHealthStore.isHealthDataAvailable() {
            healthStore = HKHealthStore()
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
        
        for metric in self.metrics {
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
            case "Walking Steadiness":
                metricsData["walking_steadiness"] = metric.value.doubleValue(for: HKUnit.percent())
            default:
                break
            }
        }
        
        metricsData["timestamp"] = getCurrentTimestamp()
        
        return metricsData
    }

    func setUpHealthStore() {
        let typesToRead: Set = [
            HKObjectType.quantityType(forIdentifier: .stepCount)!,
            HKObjectType.quantityType(forIdentifier: .walkingStepLength)!,
            HKObjectType.quantityType(forIdentifier: .walkingDoubleSupportPercentage)!,
            HKObjectType.quantityType(forIdentifier: .walkingSpeed)!,
            HKObjectType.quantityType(forIdentifier: .walkingAsymmetryPercentage)!,
            HKObjectType.quantityType(forIdentifier: .distanceWalkingRunning)!,
            HKObjectType.quantityType(forIdentifier: .appleWalkingSteadiness)!,
        ]
        healthStore?.requestAuthorization(toShare: nil, read: typesToRead, completion: { success, error in
            if success {
                self.retrieveMetric(name: "Step Count", typeIdentifier: .stepCount)
                self.retrieveMetric(name: "Step Length", typeIdentifier: .walkingStepLength)
                self.retrieveMetric(name: "Double Support Time", typeIdentifier: .walkingDoubleSupportPercentage)
                self.retrieveMetric(name: "Walking Speed", typeIdentifier: .walkingSpeed)
                self.retrieveMetric(name: "Walking Asymmetry", typeIdentifier: .walkingAsymmetryPercentage)
                self.retrieveMetric(name: "Distance Walked", typeIdentifier: .distanceWalkingRunning)
                self.retrieveMetric(name: "Walking Steadiness", typeIdentifier: .appleWalkingSteadiness)
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
                    print(error as Any)
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
                        case "Walking Steadiness":
                            self.walkingSteadiness = result.averageQuantity()!
                        default:
                            return
                    }
                }
                self.updateMetrics()
            }
            self.healthStore!.execute(query)
        } else {
            let query = HKStatisticsQuery(quantityType: quantityType,
                                        quantitySamplePredicate: predicate,
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
        let stepLengthStandard = HKQuantity(unit: HKUnit(from: "cm"), doubleValue: 52.6)
        let doubleSupportTimeStandard = HKQuantity(unit: HKUnit.percent(), doubleValue: 0.15)
        let walkingSpeedStandard = HKQuantity(unit: HKUnit(from: "m/s"), doubleValue: 1.4)
        let walkingAsymmetryStandard = HKQuantity(unit: HKUnit.percent(), doubleValue: 0.083)
        let distanceWalkingRunningStandard = HKQuantity(unit: HKUnit.meter(), doubleValue: 1563.0)
        let walkingSteadinessStandard = HKQuantity(unit: HKUnit.percent(), doubleValue: 0.91)
        
        DispatchQueue.main.async {
            self.metrics = [
                Metric(name: "Step Count", lastUpdated: "Today", value: self.stepCount ?? stepCountStandard, logo: "stepCount"),
                Metric(name: "Step Length", lastUpdated: "Today", value: self.walkingStepLength ?? stepLengthStandard, logo: "stepLength"),
                Metric(name: "Double Support Time", lastUpdated: "Today", value: self.walkingDoubleSupportPercentage ?? doubleSupportTimeStandard, logo: "doubleSupportTime"),
                Metric(name: "Walking Speed", lastUpdated: "Today", value: self.walkingSpeed ?? walkingSpeedStandard, logo: "walkingSpeed"),
                Metric(name: "Walking Asymmetry", lastUpdated: "Today", value: self.walkingAsymmetryPercentage ?? walkingAsymmetryStandard, logo: "walkingAsymmetry"),
                Metric(name: "Distance Walked", lastUpdated: "Today", value: self.distanceWalkingRunning ?? distanceWalkingRunningStandard, logo: "distanceWalked"),
                Metric(name: "Walking Steadiness", lastUpdated: "Today", value: self.walkingSteadiness ?? walkingSteadinessStandard, logo: "walkingSteadiness")
            ]
        }
    }
}
