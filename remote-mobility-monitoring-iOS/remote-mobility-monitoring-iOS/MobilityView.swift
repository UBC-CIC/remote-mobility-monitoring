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
    @Published var stepLength: HKQuantity?
    @Published var metrics: [Metric] = []
    
    init() {
        if HKHealthStore.isHealthDataAvailable() {
            healthStore = HKHealthStore()
        }
    }

    func setUpHealthStore() {
        let typesToRead: Set = [HKQuantityType.quantityType(forIdentifier: .stepCount)!]
        healthStore?.requestAuthorization(toShare: nil, read: typesToRead, completion: { success, error in
            if success {
                self.calculateStepLength()
                self.updateMetrics()
            }
        })
    }
    
    func calculateStepLength() {
        guard let stepCount = HKObjectType.quantityType(forIdentifier: .stepCount) else {
            fatalError("*** Unable to get the step count ***")
        }
        query = HKStatisticsQuery(quantityType: stepCount, quantitySamplePredicate: nil, options: .cumulativeSum) {
            query, statistics, error in
            DispatchQueue.main.async {
                self.stepLength = statistics?.sumQuantity()
                self.updateMetrics()
                print("----> calculateStepLength: \(String(describing: self.stepLength))")
            }
        }
        healthStore!.execute(query!)
    }
    
    func updateMetrics() {
        let stepLengthStandard = HKQuantity(unit: HKUnit(from: ""), doubleValue: 0.0)
        metrics = [Metric(name: "Step Length", lastUpdated: "Today", value: stepLength ?? stepLengthStandard, logo: "StepLength")]
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


