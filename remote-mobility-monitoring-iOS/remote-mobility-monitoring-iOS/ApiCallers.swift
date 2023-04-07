//
//  ApiCallers.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-02-09.
//

import Foundation

enum ApiError: Error {
    case invalidEndpoint
    case invalidResponse
    case failedToVerifyPatient
}

func verifyPatient(patientId: String, caregiverId: String, authCode: String, deviceId: String, completion: @escaping (Result<[String: Any], Error>) -> Void) {
    let endpoint = "https://th8lr56bvd.execute-api.us-west-2.amazonaws.com/prod/patients/\(patientId)/verify"
    let body: [String: Any] = [
        "caregiver_id": caregiverId,
        "auth_code": authCode,
        "device_id": deviceId
    ]
    guard let url = URL(string: endpoint) else {
        completion(.failure(ApiError.invalidEndpoint))
        return
    }
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    request.httpBody = try? JSONSerialization.data(withJSONObject: body)

    let task = URLSession.shared.dataTask(with: request) { data, response, error in
        if let error = error {
            completion(.failure(error))
            return
        }

        guard let data = data, let response = response as? HTTPURLResponse else {
            completion(.failure(ApiError.invalidResponse))
            return
        }
        if response.statusCode != 200 {
            completion(.failure(ApiError.failedToVerifyPatient))
            return
        }

        do {
            let responseObject = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
            completion(.success(responseObject!))
        } catch {
            completion(.failure(error))
        }
    }

    task.resume()
}
