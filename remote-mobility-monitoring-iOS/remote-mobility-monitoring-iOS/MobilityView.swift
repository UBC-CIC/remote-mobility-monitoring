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
    var value: String
    var logo: String
}

struct MobilityView: View {
    let healthStore = HKHealthStore()
    
    var metrics = [Metric]()
    
    init() {
        self.requestAuthorization()
    }
    
    var body: some View {
        VStack(spacing: 50) {
            Text("Mobility")
                .font(.largeTitle)
            VStack {
                ForEach(metrics, id: \.self) { metric in
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
                                    Text(metric.value)
                                }
                            )
                    }
                }
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
    
    func requestAuthorization() {
        let mobilityDataTypes: Set<HKObjectType> = [
            HKObjectType.quantityType(forIdentifier: .stepCount)!,
            HKObjectType.quantityType(forIdentifier: .distanceWalkingRunning)!,
            HKObjectType.quantityType(forIdentifier: .flightsClimbed)!,
            HKObjectType.quantityType(forIdentifier: .walkingHeartRateAverage)!,
            HKObjectType.quantityType(forIdentifier: .appleExerciseTime)!
        ]
        
        healthStore.requestAuthorization(toShare: nil, read: mobilityDataTypes) { (success, error) in
            if success {
                // Fetch mobility metrics data from HealthKit
                // Update self.metrics with fetched data
            } else {
                print("Error requesting authorization: \(error?.localizedDescription ?? "")")
            }
        }
    }
}

struct MobilityView_Previews: PreviewProvider {
    static var previews: some View {
        MobilityView()
    }
}


