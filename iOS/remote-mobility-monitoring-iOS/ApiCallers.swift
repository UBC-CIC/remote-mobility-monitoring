//
//  ApiCallers.swift
//  remote-mobility-monitoring-iOS
//
//  Created by Hachi on 2023-02-09.
//

import Foundation

func verifyPatient(patientId: String, caregiverId: String, authCode: String, idToken: String, completion: @escaping (Result<[String: Any], Error>) -> Void) {
    let endpoint = MobimonEnv.baseUrl + "/caregivers/\(caregiverId)" + "/patients/\(patientId)/accept"
    let body: [String: Any] = [
        "auth_code": authCode
    ]
    
    guard let url = URL(string: endpoint) else {
        completion(.failure(NSError(domain: "InvalidEndpoint", code: 0, userInfo: nil)))
        return
    }
    
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    request.setValue("Bearer \(idToken)", forHTTPHeaderField: "Authorization")
    request.httpBody = try? JSONSerialization.data(withJSONObject: body)

    let task = URLSession.shared.dataTask(with: request) { data, response, error in
        if let error = error {
            completion(.failure(error))
            return
        }
        
        guard let data = data, let response = response as? HTTPURLResponse else {
            completion(.failure(NSError(domain: "InvalidResponse", code: 0, userInfo: nil)))
            return
        }
        
        if response.statusCode != 200 {
            if let message = String(data: data, encoding: .utf8) {
                print(response.statusCode)
                print("Response message: \(message)")
                completion(.failure(NSError(domain: "InvalidResponse", code: response.statusCode, userInfo: [NSLocalizedDescriptionKey: message])))
            } else {
                completion(.failure(NSError(domain: "InvalidResponse", code: response.statusCode, userInfo: nil)))
            }
            return
        }
        
        do {
            let responseObject = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
            print("Response object 200: \(responseObject)")
            completion(.success(responseObject!))
        } catch {
            completion(.failure(error))
            print(error)
        }
    }

    task.resume()
}

func createPatient(email: String, password: String, firstName: String, lastName: String, phoneNumber: String, sex: String, height: Double, weight: Double, birthday: Date, completion: @escaping (Result<[String: Any], Error>) -> Void) {
    let dateFormatter = ISO8601DateFormatter()
    dateFormatter.formatOptions = [.withFullDate]
    let birthdayDateString = dateFormatter.string(from: birthday)
    
    let endpoint = MobimonEnv.baseUrl + "/patients"
    let body: [String: Any] = [
        "email": email,
        "password": password,
        "first_name": firstName,
        "last_name": lastName,
        "phone_number": phoneNumber,
        "sex": sex,
        "height": height,
        "weight": weight,
        "birthday": birthdayDateString,
    ]
    
    guard let url = URL(string: endpoint) else {
        completion(.failure(NSError(domain: "InvalidEndpoint", code: 0, userInfo: nil)))
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
            completion(.failure(NSError(domain: "InvalidResponse", code: 0, userInfo: nil)))
            return
        }
        
        if response.statusCode != 200 {
            if let message = String(data: data, encoding: .utf8) {
                print(response.statusCode)
                print("Response message: \(message)")
                completion(.failure(NSError(domain: "InvalidResponse", code: response.statusCode, userInfo: [NSLocalizedDescriptionKey: message])))
            } else {
                completion(.failure(NSError(domain: "InvalidResponse", code: response.statusCode, userInfo: nil)))
            }
            return
        }

        do {
            let responseObject = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
            print("Response object 200: \(responseObject)")
            completion(.success(responseObject!))
        } catch {
            completion(.failure(error))
            print(error)
        }
    }

    task.resume()
}

func addMetrics(idToken: String, patientId: String, metrics: [[String: Any]], completion: @escaping (Result<[String: Any], Error>) -> Void) {
    let endpoint = MobimonEnv.baseUrl + "/metrics"
    let body: [String: Any] = [
        "patient_id": patientId,
        "metrics": metrics
    ]
    
    print("Add Metrics")
    print(patientId)
    print(body)
    print(idToken)
    
    guard let url = URL(string: endpoint) else {
        completion(.failure(NSError(domain: "InvalidEndpoint", code: 0, userInfo: nil)))
        return
    }

    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    request.setValue("Bearer \(idToken)", forHTTPHeaderField: "Authorization")
    request.httpBody = try? JSONSerialization.data(withJSONObject: body)

    let task = URLSession.shared.dataTask(with: request) { data, response, error in
        if let error = error {
            completion(.failure(error))
            return
        }

        guard let data = data, let response = response as? HTTPURLResponse else {
            completion(.failure(NSError(domain: "InvalidResponse", code: 0, userInfo: nil)))
            return
        }

        if response.statusCode != 200 {
            if let message = String(data: data, encoding: .utf8) {
                print(response.statusCode)
                print("Response message: \(message)")
                completion(.failure(NSError(domain: "InvalidResponse", code: response.statusCode, userInfo: [NSLocalizedDescriptionKey: message])))
            } else {
                completion(.failure(NSError(domain: "InvalidResponse", code: response.statusCode, userInfo: nil)))
            }
            return
        }

        do {
            let responseObject = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
            print("Response object 200: \(responseObject)")
            completion(.success(responseObject!))
        } catch {
            completion(.failure(error))
            print(error)
        }
    }

    task.resume()
}

func getAllCaregiversForPatient(patientId: String, idToken: String, completion: @escaping (Result<[String: Any], Error>) -> Void) {
    let endpoint = MobimonEnv.baseUrl + "/patients/\(patientId)/caregivers"

    guard let url = URL(string: endpoint) else {
        completion(.failure(NSError(domain: "InvalidEndpoint", code: 0, userInfo: nil)))
        return
    }

    var request = URLRequest(url: url)
    request.httpMethod = "GET"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    request.setValue("Bearer \(idToken)", forHTTPHeaderField: "Authorization")

    let task = URLSession.shared.dataTask(with: request) { data, response, error in
        if let error = error {
            completion(.failure(error))
            return
        }

        guard let data = data, let response = response as? HTTPURLResponse else {
            completion(.failure(NSError(domain: "InvalidResponse", code: 0, userInfo: nil)))
            return
        }

        if response.statusCode != 200 {
            if let message = String(data: data, encoding: .utf8) {
                print(response.statusCode)
                print("Response message: \(message)")
                completion(.failure(NSError(domain: "InvalidResponse", code: response.statusCode, userInfo: [NSLocalizedDescriptionKey: message])))
            } else {
                completion(.failure(NSError(domain: "InvalidResponse", code: response.statusCode, userInfo: nil)))
            }
            return
        }

        do {
            let responseObject = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
            print("Response object 200: \(responseObject)")
            completion(.success(responseObject!))
        } catch {
            completion(.failure(error))
            print(error)
        }
    }

    task.resume()
}
