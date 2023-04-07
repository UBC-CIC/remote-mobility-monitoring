//
//  Styling.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-02-05.
//

import Foundation
import SwiftUI

struct MobimonTheme {
    static let purple = Color(UIColor(red: 0.30, green: 0.28, blue: 0.76, alpha: 1.00))
}

struct ButtonStyling {
    static let font = Font.headline
    static let color = MobimonTheme.purple
    static let foreGroundColor = Color.white
    static let cornerRadius: CGFloat = 25
}

struct CardStyling {
    static let shadow: CGFloat = 5
    static let color = MobimonTheme.purple
    static let foreGroundColor = Color.white
    static let cornerRadius: CGFloat = 25
    
}
