//
//  KeyboardModifier.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-04-04.
//

import Combine
import UIKit
import SwiftUI

struct DismissingKeyboard: ViewModifier {
    func body(content: Content) -> some View {
        content
            .onTapGesture {
                UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
            }
    }
}
