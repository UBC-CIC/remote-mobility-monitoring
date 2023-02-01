//
//  RegisterView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-01-09.
//
import SwiftUI
import HealthKit

struct RegisterView: View {
    @State var stepCount: Double = 0.0
    
    var body: some View {
        NavigationView {
            VStack(spacing: 16) {
                Text("Register")
                    .font(.largeTitle)
                Spacer()
                GeometryReader { geometry in
                    HStack {
                        Spacer()
                        VStack {
                            Spacer()
                            Button(action: {
                                self.addStepCountData()
                            }) {
                                Text("Scan QR Code")
                                    .font(.headline)
                                    .foregroundColor(.white)
                                    .padding()
                                    .frame(minWidth: 0,maxWidth: (geometry.size.width * 2/3))
                                    .background(Color(UIColor(red: 54/255, green: 51/255, blue: 140/255, alpha: 1.0)))
                                    .cornerRadius(25)
                            }
                            Spacer()
                        }
                        Spacer()
                    }
                }
                Spacer()
                NavigationLink(destination: MobilityView()) {
                    Text("Submit")
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
    
    func addStepCountData() {
        let healthStore = HKHealthStore()
        let typesToShare: Set = [
            HKObjectType.quantityType(forIdentifier: .stepCount)!
        ]

        let typesToRead: Set = [
            HKObjectType.quantityType(forIdentifier: .stepCount)!
        ]

        healthStore.requestAuthorization(toShare: typesToShare, read: typesToRead) { (success, error) in
            if !success {
                print("Error requesting authorization: \(error?.localizedDescription ?? "Unknown error")")
                return
            }
            
            let stepCountType = HKQuantityType.quantityType(forIdentifier: .stepCount)!
            let stepCountUnit = HKUnit.count()
            let stepCount = HKQuantity(unit: stepCountUnit, doubleValue: 5000)

            let sample = HKQuantitySample(type: stepCountType, quantity: stepCount, start: Date(), end: Date())

            healthStore.save(sample) { (success, error) in
                if success {
                    print("Step count data saved successfully.")
                    self.stepCount = stepCount.doubleValue(for: stepCountUnit)
                } else {
                    print("Error saving step count data: \(error?.localizedDescription ?? "Unknown error")")
                }
            }
        }
    }

}

struct RegisterView_Previews: PreviewProvider {
    static var previews: some View {
        RegisterView()
    }
}
