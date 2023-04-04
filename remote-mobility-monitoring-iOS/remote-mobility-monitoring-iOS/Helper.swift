//
//  Helper.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-04-01.
//

import Foundation
import SwiftUI

// Custom View Modifier
struct TextHeightReader: ViewModifier {
    @Binding var height: CGFloat
    
    func body(content: Content) -> some View {
        content
            .background(GeometryReader { proxy in
                Color.clear.preference(key: TextHeightPreferenceKey.self, value: proxy.size.height)
            })
            .onPreferenceChange(TextHeightPreferenceKey.self) { value in
                self.height = value
            }
    }
}

// Custom Preference Key
struct TextHeightPreferenceKey: PreferenceKey {
    typealias Value = CGFloat
    
    static var defaultValue: CGFloat = 0
    
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}
