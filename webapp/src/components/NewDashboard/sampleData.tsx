function sampleData(){
    const patients = ["Emily Smith"];
    const metrics = [
        "step_length",
        "double_support_time",
        "walking_speed",
        "walking_asymmetry",
        "distance_walked",
        "step_count"
    ];
    
    const data = [];
    const data2 = [
        {
            "patient_id": "pat-0e389f6b-4dcb-474e-a4a3-49f4d71b66c0",
            "sex": "M",
            "birthday": "2000-02-05",
            "height": 190.0,
            "weight": 70.0,
            "metric_name": "distance_walked",
            "metric_value": "9362849.38271605",
            "timestamp": "2023-04-02 13:44:53.000000000"
        },
        {
            "patient_id": "pat-0e389f6b-4dcb-474e-a4a3-49f4d71b66c0",
            "sex": "M",
            "birthday": "2000-02-05",
            "height": 190.0,
            "weight": 70.0,
            "metric_name": "walking_speed",
            "metric_value": "1.5094444444444446",
            "timestamp": "2023-04-02 13:44:53.000000000"
        },
        {
            "patient_id": "pat-0e389f6b-4dcb-474e-a4a3-49f4d71b66c0",
            "sex": "M",
            "birthday": "2000-02-05",
            "height": 190.0,
            "weight": 70.0,
            "metric_name": "step_count",
            "metric_value": "1.392003401234568E7",
            "timestamp": "2023-04-02 13:44:53.000000000"
        },
        {
            "patient_id": "pat-0e389f6b-4dcb-474e-a4a3-49f4d71b66c0",
            "sex": "M",
            "birthday": "2000-02-05",
            "height": 190.0,
            "weight": 70.0,
            "metric_name": "walking_asymmetry",
            "metric_value": "0.003",
            "timestamp": "2023-04-02 13:44:53.000000000"
        },
        {
            "patient_id": "pat-0e389f6b-4dcb-474e-a4a3-49f4d71b66c0",
            "sex": "M",
            "birthday": "2000-02-05",
            "height": 190.0,
            "weight": 70.0,
            "metric_name": "double_support_time",
            "metric_value": "0.275947231",
            "timestamp": "2023-04-02 13:44:53.000000000"
        },
        {
            "patient_id": "pat-0e389f6b-4dcb-474e-a4a3-49f4d71b66c0",
            "sex": "M",
            "birthday": "2000-02-05",
            "height": 190.0,
            "weight": 70.0,
            "metric_name": "step_length",
            "metric_value": "74.4320987654321",
            "timestamp": "2023-04-02 13:44:53.000000000"
        },
        {
            "patient_id": "pat-0e389f6b-4dcb-474e-a4a3-49f4d71b66c0",
            "sex": "M",
            "birthday": "2000-02-05",
            "height": 190.0,
            "weight": 70.0,
            "metric_name": "distance_walked",
            "metric_value": "7358201.913580247",
            "timestamp": "2023-04-03 09:22:12.000000000"
        },
        {
            "patient_id": "pat-0e389f6b-4dcb-474e-a4a3-49f4d71b66c0",
            "sex": "M",
            "birthday": "2000-02-05",
            "height": 190.0,
            "weight": 70.0,
            "metric_name": "step_length",
            "metric_value": "78.12345678901234",
            "timestamp": "2023-04-03 09:22:12.000000000"
        },
        {
            "patient_id": "pat-0e389f6b-4dcb-474e-a4a3-49f4d71b66c0",
            "sex": "M",
            "birthday": "2000-02-05",
            "height": 190.0,
            "weight": 70.0,
            "metric_name": "step_count",
            "metric_value": "1.0923731439153438E7",
            "timestamp": "2023-04-03 09:22:12.000000000"
        },
        {
            "patient_id": "pat-0e389f6b-4dcb-474e-a4a3-49f4d71b66c0",
            "sex": "M",
            "birthday": "2000-02-05",
            "height": 190.0,
            "weight": 70.0,
            "metric_name": "double_support_time",
            "metric_value": "0.246891357",
            "timestamp": "2023-04-03 09:22:12.000000000"
        },
        {
            "patient_id": "pat-0e389f6b-4dcb-474e-a4a3-49f4d71b66c0",
            "sex": "M",
            "birthday": "2000-02-05",
            "height": 190.0,
            "weight": 70.0,
            "metric_name": "walking_speed",
            "metric_value": "1.318888888888889",
            "timestamp": "2023-04-03 09:22:12.000000000"
        },
        {
            "patient_id": "pat-0e389f6b-4dcb-474e-a4a3-49f4d71b66c0",
            "sex": "M",
            "birthday": "2000-02-05",
            "height": 190.0,
            "weight": 70.0,
            "metric_name": "walking_asymmetry",
            "metric_value": "0.002357936507936508",
            "timestamp": "2023-04-03 09:22:12.000000000"
        },
        {
            "patient_id": "pat-0e389f6b-4dcb-474e-a4a3-49f4d71b66c0",
            "sex": "M",
            "birthday": "2000-02-05",
            "height": 190.0,
            "weight": 70.0,
            "metric_name": "walking_speed",
            "metric_value": "1.4155882352941176",
            "timestamp": "2023-04-04 00:15:28.000000000"
        },
        {
            "patient_id": "pat-0e389f6b-4dcb-474e-a4a3-49f4d71b66c0",
            "sex": "M",
            "birthday": "2000-02-05",
            "height": 190.0,
            "weight": 70.0,
            "metric_name": "step_count",
            "metric_value": "1.25502450847363E7",
            "timestamp": "2023-04-04 00:15:28.000000000"
        },];
    const allMetrics: any = [];
    data2.forEach((m: any) => {
        const currMetric = {
            "patient_name": "name name",
            "metric_name": m.metric_name,
            "timestamp": m.timestamp,
            "metric_value": m.metric_value
        };
        allMetrics.push(currMetric);
    });
    
    for (let i = 0; i < patients.length; i++) {
        const patientName = patients[i];
    
        for (let j = 0; j < metrics.length; j++) {
            const metricName = metrics[j];
    
            for (let k = 0; k < 10; k++) {
                const date = new Date(2023, 3, k + 1).toISOString();
    
                const metric = {
                    patient_name: patientName,
                    metric_name: metricName,
                    metric_value: Math.floor(Math.random() * 100 + 1).toFixed(1),
                    timestamp: date,
                };
    
                data.push(metric);
            }
        }
    }
    
    return allMetrics;
    

}
export default sampleData;
