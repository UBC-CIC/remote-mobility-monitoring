//
//  InitialLoadView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-03-18.
//

import SwiftUI
import HealthKit

struct InitialLoadView: View {
    @State private var isLoading = true

    var body: some View {
        addHealthKitData()
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            self.isLoading = false
        }

        return Group {
            if isLoading {
                Text("Loading...")
            } else {
                CognitoView()
            }
        }
    }
    
    func addHealthKitData() {
        let healthStore = HKHealthStore()
        let typesToShare: Set = [
            HKObjectType.quantityType(forIdentifier: .stepCount)!,
            HKObjectType.quantityType(forIdentifier: .walkingStepLength)!,
            HKObjectType.quantityType(forIdentifier: .walkingDoubleSupportPercentage)!,
            HKObjectType.quantityType(forIdentifier: .walkingSpeed)!,
            HKObjectType.quantityType(forIdentifier: .distanceWalkingRunning)!,
        ]

        let typesToRead: Set = [
            HKObjectType.quantityType(forIdentifier: .stepCount)!,
            HKObjectType.quantityType(forIdentifier: .walkingStepLength)!,
            HKObjectType.quantityType(forIdentifier: .walkingDoubleSupportPercentage)!,
            HKObjectType.quantityType(forIdentifier: .walkingSpeed)!,
            HKObjectType.quantityType(forIdentifier: .walkingAsymmetryPercentage)!,
            HKObjectType.quantityType(forIdentifier: .distanceWalkingRunning)!,
        ]

        healthStore.requestAuthorization(toShare: typesToShare, read: typesToRead) { (success, error) in
            if !success {
                print("Error requesting authorization: \(error?.localizedDescription ?? "Unknown error")")
                return
            }
            
            let stepCountType = HKQuantityType.quantityType(forIdentifier: .stepCount)!
            let stepCountUnit = HKUnit.count()
            let stepCount = HKQuantity(unit: stepCountUnit, doubleValue: Double.random(in: 0...5000))

            let walkingStepLengthType = HKQuantityType.quantityType(forIdentifier: .walkingStepLength)!
            let walkingStepLengthUnit = HKUnit(from: "cm")
            let walkingStepLength = HKQuantity(unit: walkingStepLengthUnit, doubleValue: Double.random(in: 0...100))

            let walkingDoubleSupportPercentageType = HKQuantityType.quantityType(forIdentifier: .walkingDoubleSupportPercentage)!
            let walkingDoubleSupportPercentageUnit = HKUnit.percent()
            let walkingDoubleSupportPercentage = HKQuantity(unit: walkingDoubleSupportPercentageUnit, doubleValue: Double.random(in: 0...1))

            let walkingSpeedType = HKQuantityType.quantityType(forIdentifier: .walkingSpeed)!
            let walkingSpeedUnit = HKUnit(from: "m/s")
            let walkingSpeed = HKQuantity(unit: walkingSpeedUnit, doubleValue: Double.random(in: 0...20))

            let distanceWalkingRunningType = HKQuantityType.quantityType(forIdentifier: .distanceWalkingRunning)!
            let distanceWalkingRunningUnit = HKUnit.meter()
            let distanceWalkingRunning = HKQuantity(unit: distanceWalkingRunningUnit, doubleValue: Double.random(in: 0...100000))

            let stepCountSample = HKQuantitySample(type: stepCountType, quantity: stepCount, start: Date(), end: Date())
            let walkingStepLengthSample = HKQuantitySample(type: walkingStepLengthType, quantity: walkingStepLength, start: Date(), end: Date())
            let walkingDoubleSupportPercentageSample = HKQuantitySample(type: walkingDoubleSupportPercentageType, quantity: walkingDoubleSupportPercentage, start: Date(), end: Date())
            let walkingSpeedSample = HKQuantitySample(type: walkingSpeedType, quantity: walkingSpeed, start: Date(), end: Date())
            let distanceWalkingRunningSample = HKQuantitySample(type: distanceWalkingRunningType, quantity: distanceWalkingRunning, start: Date(), end: Date())

            healthStore.save(stepCountSample) { (success, error) in
                if success {
                    print("Step count data saved successfully.")
                } else {
                    print("Error saving step count data: \(error?.localizedDescription ?? "Unknown error")")
                }
            }
            
            healthStore.save(walkingStepLengthSample) { (success, error) in
                if success {
                    print("walking step length saved successfully.")
                } else {
                    print("Error saving step length data: \(error?.localizedDescription ?? "Unknown error")")
                }
            }
            healthStore.save(walkingDoubleSupportPercentageSample) { (success, error) in
                if success {
                    print("walking double support percentage saved successfully.")
                } else {
                    print("Error saving walking double support percentagedata: \(error?.localizedDescription ?? "Unknown error")")
                }
            }
            healthStore.save(walkingSpeedSample) { (success, error) in
                if success {
                    print("walking speed saved successfully.")
                } else {
                    print("Error saving walking speed data: \(error?.localizedDescription ?? "Unknown error")")
                }
            }
            healthStore.save(distanceWalkingRunningSample) { (success, error) in
                if success {
                    print("walking running distance saved successfully.")
                } else {
                    print("Error saving walking running distance data: \(error?.localizedDescription ?? "Unknown error")")
                }
            }
        }
    }
}

