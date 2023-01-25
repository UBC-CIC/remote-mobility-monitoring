//
//  MobilityView.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-01-24.
//

import SwiftUI

struct Metric: Hashable {
    var name: String
    var lastUpdated: String
    var value: String
    var logo: String
}

struct MobilityView: View {
    // These metrics are dummy data. They will be fetched from HealthKit in the next ticket
    var metrics = [
        Metric(name: "Step length", lastUpdated: "2022-12-24", value: "10 cms", logo: "StepLength"),
        Metric(name: "Double support time", lastUpdated: "2022-12-24", value: "20 mins", logo: "DoubleSupportTime"),
        Metric(name: "Walking speed", lastUpdated: "2022-12-24", value: "20 km/h", logo: "WalkingSpeed"),
        Metric(name: "Walking asymmetry", lastUpdated: "2022-12-24", value: "20°", logo: "WalkingAsymmetry"),
        Metric(name: "Distance walked", lastUpdated: "2022-12-24", value: "20 kms", logo: "DistanceWalked")
    ];
    
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
}

struct MobilityView_Previews: PreviewProvider {
    static var previews: some View {
        MobilityView()
    }
}
