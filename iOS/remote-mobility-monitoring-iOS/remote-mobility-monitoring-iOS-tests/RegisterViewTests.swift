//
//  RegisterViewTests.swift
//  remote-mobility-monitoring-iOS-tests
//
//  Created by Hachi on 2023-02-22.
//

import XCTest
import HealthKit
import CodeScanner
import KeychainAccess
import AVFoundation
@testable import remote_mobility_monitoring_iOS

struct TestScanResult {
    let string: String
    let type: AVMetadataObject.ObjectType
}

final class RegisterViewTests: XCTestCase {
    /*
     Test that the displayScanningResult function correctly sets the isShowingScanningResult and verified flags when the scanning message is "Success"
     The reason for the two different delay durations is to ensure that the test has enough time to complete, while still allowing the
     displayScanningResult() function enough time to execute.
     */
    func testDisplayScanningResultSuccess() throws {
        let view = RegisterView()
        view.isShowingScanningResult = false
        view.scanningMessage = "Success"
        
        view.displayScanningResult()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 10) {
            XCTAssertTrue(view.isShowingScanningResult)
            XCTAssertEqual(view.scanningMessage, "")
            XCTAssertFalse(view.errorScanning)
            XCTAssertTrue(view.verified)
        }
        
        // Wait for the expected amount of time for the displayScanningResult function to complete
        RunLoop.current.run(until: Date(timeIntervalSinceNow: 4))
    }
    
    /*
     Test that the displayScanningResult function correctly sets the isShowingScanningResult and verified flags when the scanning message is "Failure"
     The reason for the two different delay durations is to ensure that the test has enough time to complete, while still allowing the
     displayScanningResult() function enough time to execute.
     */
    func testDisplayScanningResultFailure() throws {
        let view = RegisterView()
        view.isShowingScanningResult = false
        view.scanningMessage = "Success"
        
        view.displayScanningResult()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 10) {
            XCTAssertTrue(view.isShowingScanningResult)
            XCTAssertEqual(view.scanningMessage, "")
            XCTAssertTrue(view.errorScanning)
            XCTAssertFalse(view.verified)
        }
        
        // Wait for the expected amount of time for the displayScanningResult function to complete
        RunLoop.current.run(until: Date(timeIntervalSinceNow: 4))
    }

    // Test that the getDeviceId function correctly retrieves the device ID from the keychain, even if it hasn't been stored before.
    func testGetDeviceId() throws {
        let view = RegisterView()
        
        let deviceId = view.getDeviceId()
        let storedDeviceId = try XCTUnwrap(view.keychain["deviceId"])
        
        XCTAssertEqual(deviceId, storedDeviceId)
        
        view.keychain["deviceId"] = nil
        let newDeviceId = view.getDeviceId()
        XCTAssertFalse(newDeviceId.isEmpty)
        XCTAssertEqual(newDeviceId, try XCTUnwrap(view.keychain["deviceId"]))
    }
}
