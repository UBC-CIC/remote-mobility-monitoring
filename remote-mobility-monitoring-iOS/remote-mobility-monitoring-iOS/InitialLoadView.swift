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
    @Binding var isAuthenticated: Bool

    var body: some View {
        requestReadHealthKitData()
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            self.isLoading = false
        }

        return Group {
            if isLoading {
                Text("Loading...")
            } else {
                CognitoView(isAuthenticated: $isAuthenticated)
            }
        }
    }
    
    func requestReadHealthKitData() {
        let healthStore = HKHealthStore()

        let typesToRead: Set = [
            HKObjectType.quantityType(forIdentifier: .stepCount)!,
            HKObjectType.quantityType(forIdentifier: .walkingStepLength)!,
            HKObjectType.quantityType(forIdentifier: .walkingDoubleSupportPercentage)!,
            HKObjectType.quantityType(forIdentifier: .walkingSpeed)!,
            HKObjectType.quantityType(forIdentifier: .walkingAsymmetryPercentage)!,
            HKObjectType.quantityType(forIdentifier: .distanceWalkingRunning)!,
        ]

        healthStore.requestAuthorization(toShare: nil, read: typesToRead) { (success, error) in
            if !success {
                print("Error requesting healthkit authorization: \(error?.localizedDescription ?? "Unknown error")")
                return
            }
            
            print("Successfully authorize healthkit read access")
        }
    }
}

